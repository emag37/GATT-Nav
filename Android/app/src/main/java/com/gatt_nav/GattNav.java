package com.gatt_nav;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.PermissionChecker;

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
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class GattNav extends AppCompatActivity implements  IGetNavData, BleDeviceConnectionEvent {
    private final String TAG = GattNav.class.getSimpleName();

    private boolean isNavigating = false;
    private Geocoder geocoder;
    private LatLng destination;
    private String selectedPlaceName;

    private NavBle ble;
    private GPS gps;

    private TextView navDataText;
    private ImageView compassNeedleImg;
    private SwitchCompat useGyroscopeSwitch;
    private Button goBtn;
    private TextView currentAddressDisplay;
    private Timer updaterTask;
    private TextView connectionStatusLbl;
    private ImageView connectionStatusIndicator;

    private final int LOCATION_PERMISSION = 100;
    private PowerManager.WakeLock navigatingWl;
    private static final NavDTO BAD_NAV = new NavDTO(-1, 0, 0);

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

        Places.initialize(this, ApiKey.API_KEY);
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
        useGyroscopeSwitch = findViewById(R.id.useGyroscopeSwitch);
        ble = new NavBle(getApplicationContext(), this, this);
        geocoder = new Geocoder(this);
        gps = new GPS(getApplicationContext());
        compassNeedleImg = findViewById(R.id.compassNeedleImg);
        navDataText = findViewById(R.id.navDataText);

        updaterTask = new Timer();
        updaterTask.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                on1SecondTick();
            }
        }, 0, 1000);

        navigatingWl = ((PowerManager) getSystemService(
                POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "GattNav:NavigatingWL");
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
                navigatingWl.acquire();
            } catch (IOException e) {
                e.printStackTrace();
                isNavigating = false;
            }
        } else {
            navigatingWl.release();
            goBtn.setText("Go!");
        }
    }

    public void updateCurrentAddress(LatLng pos) {
        try {
            geocoder.getFromLocation(pos.latitude, pos.longitude, 1).stream()
                    .map(a -> a.getAddressLine(0))
                    .findFirst().ifPresent(address -> currentAddressDisplay.setText(address));
        } catch (Exception e) {
            Log.e(TAG, "Error getting address from geocoder!");
            e.printStackTrace();
        }
    }

    @Override
    public NavDTO getData() {
        if(destination == null) {
            return BAD_NAV;
        }

        float speed = gps.getCurrentSpeed();
        float[] distAndBearing = gps.getGpsDistanceAndBearingTo(destination);

        if (distAndBearing[0] == GPS.INVALID_RESULT) {
            return BAD_NAV;
        }
        float angleToDest = useGyroscopeSwitch.isChecked() ? gps.getCurrentCompassBearingTo(destination) : distAndBearing[1];

        Log.d(TAG, "Angle between me and my destination is: " + angleToDest + "deg , it is " + distAndBearing[0] + " km away");
        return new NavDTO(distAndBearing[0], angleToDest, speed);
    }

    public void on1SecondTick() {
        runOnUiThread(() -> {
            LatLng currentPos = gps.getPosition();
            updateCurrentAddress(currentPos);
            if (destination != null) {
                float[] distAndBearing = gps.getGpsDistanceAndBearingTo(destination);
                if (distAndBearing[0] == GPS.INVALID_RESULT) {
                    navDataText.setText("No valid GPS data...");
                    return;
                }
                compassNeedleImg.setRotation(distAndBearing[1]);
                float speed = gps.getCurrentSpeed();
                navDataText.setText(String.format(Locale.getDefault(), "Distance: %.3f Speed: %.3f Bearing %.3f",distAndBearing[0], speed, distAndBearing[1]));
            }
        });
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