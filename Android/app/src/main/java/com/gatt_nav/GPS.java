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

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.Calendar;
import java.util.Date;

import static android.content.Context.SENSOR_SERVICE;

public class GPS extends LocationCallback {
    public static final int INVALID_RESULT = -1;

    private static final int GPS_UPDATE_INTERVAL_MS = 1000;
    private static final int GPS_UPDATE_FASTEST_INTERVAL_MS = 250;

    private final String TAG = GPS.class.getSimpleName();
    private FusedLocationProviderClient locationProvider;
    private static final float metersPerSecondToKmPerHour = 3.6f;
    private float currentSpeed = 0.0f;
    private float currentCompassBearing = 0.0f;
    private Location currentLocation;
    private LatLng currentFilteredLatLng;
    private Date lastLocationUpdateAt;

    private Sensor rotationVectorSensor;
    private SensorManager sensorManager;
    private SensorEventListener sensorListener;
    private float[] rMat = new float[9];
    private float[] orientation = new float[3];
    private MedianLocationFilter locationFilter;

    static private final float FILTER_ALPHA = 0.25f;


    protected static float lowPassFilter( float input, float output ) {
        output = output + FILTER_ALPHA * (input - output);

        return output;
    }

    public GPS(Context ctx) {
        init(ctx);
    }

    public void init(Context ctx) {
        locationFilter = new MedianLocationFilter(3);
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationProvider = LocationServices.getFusedLocationProviderClient(ctx);
            LocationRequest req = new LocationRequest();
            lastLocationUpdateAt = new Date(0);
            req.setInterval(GPS_UPDATE_INTERVAL_MS);
            req.setFastestInterval(GPS_UPDATE_FASTEST_INTERVAL_MS);
            req.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationProvider.requestLocationUpdates(req,this, null);

            sensorManager = (SensorManager)ctx.getSystemService(SENSOR_SERVICE);

            rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            sensorListener = new SensorEventListener() {
                @Override
                public synchronized void onSensorChanged(SensorEvent event) {
                    SensorManager.getRotationMatrixFromVector(rMat, event.values);
                    float newBearing = (float) Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]);
                    if (newBearing < 0) {
                        newBearing += 360;
                    }
                    currentCompassBearing = lowPassFilter(newBearing, currentCompassBearing);
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }
            };
            sensorManager.registerListener(sensorListener, rotationVectorSensor, SensorManager.SENSOR_DELAY_GAME);
        } else {
            throw new RuntimeException("Need location permission!");
        }
    }

    public synchronized void deinit() {
        locationProvider.removeLocationUpdates(this);
        locationProvider = null;
        sensorManager.unregisterListener(sensorListener);
        sensorListener = null;
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

    private boolean isGpsBearingValid() {
        Date validFrom = new Date(System.currentTimeMillis() - (1000 * 20)); // valid from 20 seconds ago
        return currentLocation != null && lastLocationUpdateAt.after(validFrom) && currentFilteredLatLng != null;
    }

    public synchronized boolean hasGpsBearing() {
        return isGpsBearingValid();
    }

    public synchronized float getCurrentCompassBearingTo(LatLng targetLocation) {
        if (currentLocation == null) {
            return 0.f;
        }
        Vector2D currPos = new Vector2D(currentLocation.getLatitude(), currentLocation.getLongitude());
        Vector2D destVect = new Vector2D(targetLocation.latitude, targetLocation.longitude).subtract(currPos);
        Vector2D bearingVect = new Vector2D(Math.sin(Math.toRadians(currentCompassBearing)), Math.cos(Math.toRadians(currentCompassBearing)));

        return (float) Math.toDegrees(Math.atan2(destVect.getX(), destVect.getY()) - Math.atan2(bearingVect.getX(), bearingVect.getY()));
    }

    // Returns an array of 3 values: Distance in km, start bearing, end bearing
    public synchronized float[] getGpsDistanceAndBearingTo(LatLng targetLocation) {
        if (currentLocation == null || !isGpsBearingValid()) {
            return new float[]{INVALID_RESULT,-1,-1,-1};
        }
        float[] distResults = new float[3];
        Location.distanceBetween(currentFilteredLatLng.latitude, currentFilteredLatLng.longitude, targetLocation.latitude, targetLocation.longitude, distResults);

        distResults[0] /= 1000f;
        //Log.d(TAG, "GPS Bearing: " + currentLocation.getBearing() + " bearing to location: " + distResults[1]);
        if (distResults[1] < 0) {
            distResults[1] += 360;
        }

        // The difference between where we're going and where we want to go
        //Log.d(TAG, "Current bearing: " + currentLocation.getBearing() + " initial bearing: " + distResults[1] + " final bearing: " + distResults[2] + " moved: " + Math.abs(currentLocation.distanceTo(lastLocation)));
        distResults[1] -= currentLocation.getBearing();
        return distResults;
    }

    @Override
    public synchronized void onLocationResult(LocationResult location) {
        currentSpeed = location.getLastLocation().getSpeed() * metersPerSecondToKmPerHour;
        currentLocation = location.getLastLocation();
        currentFilteredLatLng = locationFilter.filterAndOutput(location.getLastLocation());
        lastLocationUpdateAt = new Date();
    }
}
