package com.gatt_nav;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.IBinder;
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

public class GattNav extends AppCompatActivity {
    private final String TAG = GattNav.class.getSimpleName();

    private boolean isNavigating = false;
    private Geocoder geocoder;

    private Place selectedPlace;

    private NavService.Binding boundNav;
    private TextView navDataText;
    private ImageView compassNeedleImg;
    private SwitchCompat useGyroscopeSwitch;
    private Button goBtn;
    private TextView currentAddressDisplay;
    private Timer updaterTask;
    private TextView connectionStatusLbl;
    private ImageView connectionStatusIndicator;
    private UpdateServiceConnection updateServiceConnection;

    private final int LOCATION_PERMISSION = 100;

    private class UpdateServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            boundNav = (NavService.Binding) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            boundNav = null;
        }
    }
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
        destinationEditTxt.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

// Set up a PlaceSelectionListener to handle the response.
        destinationEditTxt.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                selectedPlace = place;
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
        geocoder = new Geocoder(this);

        compassNeedleImg = findViewById(R.id.compassNeedleImg);
        navDataText = findViewById(R.id.navDataText);

        updaterTask = new Timer();
        updaterTask.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                on1SecondTick();
            }
        }, 0, 1000);

        Intent notificationIntent = new Intent(this, NavService.class);
        updateServiceConnection = new UpdateServiceConnection();
        bindService(notificationIntent, updateServiceConnection, BIND_AUTO_CREATE | BIND_IMPORTANT);
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
    protected void onDestroy() {
        super.onDestroy();
        updaterTask.cancel();
        updaterTask = null;
        unbindService(updateServiceConnection);
        updateServiceConnection = null;
    }

    public void onGoPressed(View view) {
        if (boundNav == null) {
            Toast.makeText(this,"Nav service not running yet...", Toast.LENGTH_SHORT).show();
            return;
        }
        isNavigating = !isNavigating;
        if (isNavigating) {
            if(selectedPlace == null) {
                Toast.makeText(this,"Please select a destination!", Toast.LENGTH_SHORT).show();
                return;
            }
            boundNav.setDestination(selectedPlace.getLatLng());

            Toast.makeText(this,"To " + selectedPlace.getName() + " we ride!", Toast.LENGTH_SHORT).show();
            goBtn.setText("Wait, stop!");
        } else {
            boundNav.setDestination(null);
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

    public void on1SecondTick() {
        runOnUiThread(() -> {
            if (boundNav == null) {
                return;
            }
            LatLng currentPos = boundNav.getPosition();
            if (currentPos != null) {
                updateCurrentAddress(currentPos);
            }
            if (boundNav.getDestination() != null) {
                float[] distAndBearing = boundNav.getDistanceAndBearingToDestination();
                if (distAndBearing[0] == GPS.INVALID_RESULT) {
                    navDataText.setText("No valid GPS data...");
                    return;
                }
                compassNeedleImg.setRotation(distAndBearing[1]);
                float speed = boundNav.getSpeed();
                navDataText.setText(String.format(Locale.getDefault(), "Distance: %.3f Speed: %.3f Bearing %.3f",distAndBearing[0], speed, distAndBearing[1]));
            }

            if(boundNav.isBleConnected()) {
                connectionStatusIndicator.setImageDrawable(getResources().getDrawable(R.drawable.circle_green,null));
                connectionStatusLbl.setText("Display Connected");
            } else {
                connectionStatusIndicator.setImageDrawable(getResources().getDrawable(R.drawable.circle_red, null));
                connectionStatusLbl.setText("Display Not Connected");
            }
        });
    }
}