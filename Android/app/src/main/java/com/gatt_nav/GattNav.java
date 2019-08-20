package com.gatt_nav;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Stream;

public class GattNav extends AppCompatActivity implements GpsReadyEvent, IGetNavData, BleDeviceConnectionEvent {
    private final String TAG = GattNav.class.getSimpleName();

    private boolean isNavigating = false;
    private Geocoder geocoder;
    private LatLng destination;
    private String selectedPlaceName;

    private NavBle ble;
    private GPS gps;

    private Button goBtn;
    private TextView currentAddressDisplay;
    private Timer updaterTask;
    private TextView connectionStatusLbl;
    private ImageView connectionStatusIndicator;

    private final int LOCATION_PERMISSION = 100;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == LOCATION_PERMISSION && Arrays.stream(grantResults).allMatch(r -> r == PermissionChecker.PERMISSION_GRANTED)) {
            init();
        } else {
            finish();
        }
    }

    private void init() {
        setContentView(R.layout.activity_gatt_nav);

        // Devices with a display should not go to sleep
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Places.initialize(this, "AIzaSyC1WcvU5AuYsk-SpsEu9JkrPcgL4j67hTc");
        AutocompleteSupportFragment destinationEditTxt = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_address);

// Specify the types of place data to return.
        destinationEditTxt.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

// Set up a PlaceSelectionListener to handle the response.
        destinationEditTxt.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                selectedPlaceName = place.getName();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        goBtn = findViewById(R.id.goBtn);
        connectionStatusLbl = findViewById(R.id.connectedLbl);
        connectionStatusIndicator = findViewById(R.id.connectionStatusImg);
        currentAddressDisplay = findViewById(R.id.youAreAtTxt);

        ble = new NavBle(getApplicationContext(), this, this);
        geocoder = new Geocoder(this);
        gps = new GPS(getApplicationContext());

        updaterTask = new Timer();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED  || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION);
        } else {
            init();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onGoPressed(View view) {
        isNavigating = !isNavigating;
        if (isNavigating) {
            if(selectedPlaceName == null) {
                Toast.makeText(this,"Please select a destination!", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                List<Address> address = geocoder.getFromLocationName(selectedPlaceName, 1);
                destination = address.stream()
                        .map(a -> new LatLng(a.getLatitude(), a.getLongitude())).findFirst()
                        .orElse(new LatLng(0,0));
                Toast.makeText(this,"To " + (!address.isEmpty() ? address.iterator().next().getAddressLine(0) : "") + " we ride!", Toast.LENGTH_SHORT).show();
                goBtn.setText("Wait, stop!");
            } catch (IOException e) {
                e.printStackTrace();
                isNavigating = false;
            }


        } else {
            goBtn.setText("Go!");
        }
    }

    public void updateCurrentAddress(Pair<Float, Float> posLatLng) {
        try {
            geocoder.getFromLocation(posLatLng.first, posLatLng.second, 1).stream()
                    .map(a -> a.getAddressLine(0))
                    .findFirst().ifPresent(address -> runOnUiThread(() -> currentAddressDisplay.setText(address)));
        } catch (Exception e) {
            Log.e(TAG, "Error getting address from geocoder!");
            e.printStackTrace();
        }
    }

    @Override
    public NavDTO getData() {
        if(destination == null) {
            return new NavDTO(0, 0, 0);
        }
        Pair<Float,Float> pos = gps.getPosition();
        float bearing = gps.getCurrentBearing();

        Vector2D currPos = new Vector2D(pos.first, pos.second);
        Vector2D destVect = new Vector2D(destination.latitude, destination.longitude).subtract(currPos);
        Vector2D bearingVect = new Vector2D(Math.sin(Math.toRadians(bearing)), Math.cos(Math.toRadians(bearing)));
        float angleToDest = (float) Math.toDegrees(Math.atan2(destVect.getX(), destVect.getY()) - Math.atan2(bearingVect.getX(), bearingVect.getY()));

        float[] distResults = new float[3];
        Location.distanceBetween(pos.first, pos.second, destination.latitude, destination.longitude, distResults);

        for(int i = 0; i< distResults.length; i++) {
            distResults[i] /= 1000.0f;
        }

        float speed = gps.getCurrentSpeed();

        Log.d(TAG, "Angle between me and my destination is: " + angleToDest + "deg , it is " + distResults[0] + " km away");
        return new NavDTO(distResults[0], angleToDest, speed);
    }

    public void updateCurrentAddress() {
        updateCurrentAddress(gps.getPosition());
    }

    @Override
    public void onReady() {
        Log.i(TAG, "GPS Ready! Starting update task...");
        updaterTask.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateCurrentAddress();
            }
        }, 0, 1000);
    }

    @Override
    public void OnDeviceConnectionStateChanged(boolean isConnected) {
        if(isConnected) {
            connectionStatusIndicator.setImageDrawable(getResources().getDrawable(R.drawable.circle_green,null));
            connectionStatusLbl.setText("Display Connected");
        } else {
            connectionStatusIndicator.setImageDrawable(getResources().getDrawable(R.drawable.circle_red, null));
            connectionStatusLbl.setText("Display Not Connected");
        }
    }
}