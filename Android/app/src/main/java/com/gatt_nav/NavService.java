package com.gatt_nav;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class NavService extends Service  implements  IGetNavData, BleDeviceConnectionEvent {
    private final String TAG = NavService.class.getSimpleName();

    private final float MINIMUM_SPEED = 3.0f;
    private final String CHANNEL_ID = "NavService.Channel";

    private NavBle ble;
    private GPS gps;
    private LatLng destination;
    private boolean isBleConnected = false;
    private boolean useCompass = false;
    private static final NavDTO BAD_NAV = new NavDTO(-1, 0, 0);

    public class Binding extends Binder {
        void setDestination(LatLng newDestination) {
            destination = newDestination;
        }

        LatLng getDestination() {
            return destination;
        }

        LatLng getPosition() {
            return gps.getPosition();
        }

        float getSpeed() {
            return gps.getCurrentSpeed();
        }

        float[] getDistanceAndBearingToDestination() {
            if (destination != null) {
                return gps.getGpsDistanceAndBearingTo(destination);
            }

            return new float[]{GPS.INVALID_RESULT,-1,0,0};
        }

        boolean isUsingCompass() {
            return useCompass;
        }

        void setUseCompass(boolean wantCompass) {
            useCompass = wantCompass;
        }

        boolean isBleConnected() {
            return isBleConnected;
        }
    }

    public NavService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        gps = new GPS(getApplicationContext());
        ble = new NavBle(getApplicationContext(), this, this);

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "NavServiceChannel", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Channel for NavService Foreground Notification");

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.compass_arrow);
        Intent notificationIntent = new Intent(this, GattNav.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification =
                new Notification.Builder(this, CHANNEL_ID)
                        .setContentTitle("Nav Service")
                        .setContentText("Service running...")
                        .setSmallIcon(Icon.createWithBitmap(icon))
                        .setLargeIcon(icon)
                        .setContentIntent(pendingIntent)
                        .build();
        startForeground(10, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ble.deInit();
        gps.deinit();
        ble = null;
        gps = null;
    }

    @Override
    public NavDTO getData() {
        float speed = gps.getCurrentSpeed();
        float angleToDest = -1;
        float distToDest = -1;

        if(destination != null && speed >= MINIMUM_SPEED) {
            float[] distAndBearing = gps.getGpsDistanceAndBearingTo(destination);

            if (distAndBearing[0] != GPS.INVALID_RESULT) {
                angleToDest = useCompass ? gps.getCurrentCompassBearingTo(destination) : distAndBearing[1];
                distToDest = distAndBearing[0];
            }
        }
        Log.d(TAG, "Angle between me and my destination is: " + angleToDest + "deg , it is " + distToDest + " km away");
        return new NavDTO(distToDest, angleToDest, speed);
    }


    @Override
    public void OnDeviceConnectionStateChanged(boolean isConnected) {
        isBleConnected = isConnected;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new Binding();
    }
}
