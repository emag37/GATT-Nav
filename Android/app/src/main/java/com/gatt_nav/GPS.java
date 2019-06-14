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
    private float currentSpeed = 0.0f;
    private float currentBearing = 0.0f;
    private float currentLatitude = 0.0f;
    private float currentLongitude = 0.0f;
    private boolean gotFirstReading = false;
    private GpsReadyEvent readyCallback = null;

    private Sensor accelerometer;
    private Sensor magnetometer;
    private SensorManager sensorManager;
    private float[] accel = new float[3];
    private float[] mag = new float[3];
    private float[] rMat = new float[9];
    private float[] orientation = new float[3];

    GeomagneticField geomagneticField = null;

    static final float FILTER_ALPHA = 0.25f;

    protected static float[] lowPassFilter( float[] input, float[] output ) {
        if ( output == null ) return input;

        for ( int i=0; i < input.length; i++ ) {
            output[i] = output[i] + FILTER_ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    private SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    accel = lowPassFilter(event.values, accel);
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    mag = lowPassFilter(event.values, mag);
                    break;
                default:
                    return;
            }
            if (SensorManager.getRotationMatrix(rMat, null, accel, mag)) {

                SensorManager.getOrientation(rMat, orientation);
                float bearing = (float) Math.toDegrees(orientation[0]);
                if(geomagneticField != null) {
                    bearing += geomagneticField.getDeclination();
                }
                bearing -= 90.0f;
                if(bearing < 0) {
                    bearing += 360.0f;
                } 
                //Log.d(TAG, "Current bearing is: " + bearing + " degrees");
                currentBearing = bearing; //East of north to 0 if north
            }
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
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

            sensorManager.registerListener(sensorListener, accelerometer, SensorManager.SENSOR_DELAY_GAME);
            sensorManager.registerListener(sensorListener, magnetometer, SensorManager.SENSOR_DELAY_GAME);
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
        currentSpeed = location.getLastLocation().getSpeed();
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
