package com.gatt_nav;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class NavService extends Service {
    public NavService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
