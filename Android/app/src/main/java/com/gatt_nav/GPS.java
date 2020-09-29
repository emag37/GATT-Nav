package com.gatt_nav;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.Date;

import static android.content.Context.SENSOR_SERVICE;

public class GPS extends LocationCallback {
    private final String TAG = GPS.class.getSimpleName();
    private FusedLocationProviderClient locationProvider;
    private static final float metersPerSecondToKmPerHour = 3.6f;
    private float currentSpeed = 0.0f;
    private float currentCompassBearing = 0.0f;
    private Location currentLocation;
    private Date lastBearingAt;

    private Sensor rotationVectorSensor;
    private SensorManager sensorManager;
    private float[] rMat = new float[9];
    private float[] orientation = new float[3];


    static private final float FILTER_ALPHA = 0.25f;


    protected static float lowPassFilter( float input, float output ) {
        output = output + FILTER_ALPHA * (input - output);

        return output;
    }

    private SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public synchronized void onSensorChanged(SensorEvent event) {
            SensorManager.getRotationMatrixFromVector( rMat, event.values );
            // get the azimuth value (orientation[0]) in degree
            float newBearing = (float) Math.toDegrees( SensorManager.getOrientation( rMat, orientation )[0] );
            if(newBearing < 0) {
                newBearing += 360;
            }
            currentCompassBearing = lowPassFilter(newBearing, currentCompassBearing);

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public GPS(Context ctx) {
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationProvider = LocationServices.getFusedLocationProviderClient(ctx);
            LocationRequest req = new LocationRequest();
            req.setInterval(5000);
            req.setSmallestDisplacement(2.0f);
            req.setFastestInterval(2000);
            req.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationProvider.requestLocationUpdates(req,this, null);

            sensorManager = (SensorManager)ctx.getSystemService(SENSOR_SERVICE);

            rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            sensorManager.registerListener(sensorListener, rotationVectorSensor, SensorManager.SENSOR_DELAY_GAME);
        } else {
            throw new RuntimeException("Need location permission!");
        }
    }

    public synchronized LatLng getPosition() {
        if (currentLocation != null) {
            return new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        }
        return null;
    }

    public synchronized float getCurrentSpeed() {
        return currentSpeed;
    }

    public synchronized float getCurrentCompassBearing() {
        return currentCompassBearing;
    }

    public synchronized float getGpsBearingTo(Location targetLocation) {
        if (currentLocation != null && currentLocation.hasBearing()) {
            return currentLocation.bearingTo(targetLocation);
        }
        return 0f;
    }

    public synchronized float[] getGpsDistanceAndBearingTo(LatLng targetLocation) {
        float[] distResults = new float[3];
        if (currentLocation == null) {
            for (int i = 0; i< 3; i++) {
                distResults[i] = 0;
            }
            return distResults;
        }
        Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), targetLocation.latitude, targetLocation.longitude, distResults);

        distResults[0] /= 1000f;
        if (currentLocation.hasBearing()) {
            Log.d(TAG, "GPS Bearing: " + currentLocation.getBearing() + " bearing to location: " + distResults[1]);
            if (distResults[1] < 0) {
                distResults[1] += 360;
            }

            // The difference between where we're going and where we want to go
            distResults[1] -= currentLocation.getBearing();
        }

        return distResults;
    }

    @Override
    public synchronized void onLocationResult(LocationResult location) {
        Log.d(TAG, "Got location update: " + location.toString());
        currentSpeed = location.getLastLocation().getSpeed() * metersPerSecondToKmPerHour;
        currentLocation = location.getLastLocation();
    }
}
