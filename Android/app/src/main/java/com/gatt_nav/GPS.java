package com.gatt_nav;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import static android.content.Context.SENSOR_SERVICE;

public class GPS extends LocationCallback {
    private final String TAG = GPS.class.getSimpleName();
    private FusedLocationProviderClient locationProvider;
    private static final float metersPerSecondToKmPerHour = 3.6f;
    private float currentSpeed = 0.0f;
    private float currentBearing = 0.0f;
    private float currentLatitude = 0.0f;
    private float currentLongitude = 0.0f;
    private boolean gotFirstReading = false;
    private GpsReadyEvent readyCallback = null;

    private Sensor rotationVector;
    private SensorManager sensorManager;
    private float[] rMat = new float[9];
    private float[] orientation = new float[3];

    GeomagneticField geomagneticField = null;

    static private final float FILTER_ALPHA = 0.25f;


    protected static float lowPassFilter( float input, float output ) {
        output = output + FILTER_ALPHA * (input - output);

        return output;
    }

    private SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            SensorManager.getRotationMatrixFromVector( rMat, event.values );
            // get the azimuth value (orientation[0]) in degree
            float newBearing = (float) Math.toDegrees( SensorManager.getOrientation( rMat, orientation )[0] );
            if(newBearing < 0) {
                newBearing += 360;
            }
            newBearing -= 90;
            currentBearing = lowPassFilter(newBearing, currentBearing);
            //Log.d(TAG, "Current bearing is: " + currentBearing + " degrees");
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
            req.setSmallestDisplacement(5.0f);
            req.setFastestInterval(2000);
            req.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationProvider.requestLocationUpdates(req,this, null);

            sensorManager = (SensorManager)ctx.getSystemService(SENSOR_SERVICE);

            rotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            sensorManager.registerListener(sensorListener, rotationVector, SensorManager.SENSOR_DELAY_GAME);
        } else {
            throw new RuntimeException("Need location permission!");
        }
    }

    public void setOnPositionCallback(GpsReadyEvent cb) {
        readyCallback = cb;
    }

    public Pair<Float,Float> getPosition() {
        return new Pair<>(currentLatitude, currentLongitude);
    }

    public float getCurrentSpeed() {
        return currentSpeed;
    }

    public float getCurrentBearing() {
        return currentBearing;
    }

    @Override
    public void onLocationResult(LocationResult location) {
        Log.d(TAG, "Got location update: " + location.toString());
        currentSpeed = location.getLastLocation().getSpeed() * metersPerSecondToKmPerHour;
        if(location.getLastLocation().hasBearing()) {
            currentBearing = location.getLastLocation().getBearing();
        }
        currentLongitude = (float) location.getLastLocation().getLongitude();
        currentLatitude = (float) location.getLastLocation().getLatitude();
        geomagneticField = new GeomagneticField(
                (float) location.getLastLocation().getLatitude(),
                (float) location.getLastLocation().getLongitude(),
                (float) location.getLastLocation().getAltitude(),
                System.currentTimeMillis());

        if(!gotFirstReading) {
            gotFirstReading = true;
            if(readyCallback != null) {
                readyCallback.onReady();
            }
        }

    }
}
