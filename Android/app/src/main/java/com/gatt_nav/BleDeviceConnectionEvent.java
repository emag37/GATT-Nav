package com.gatt_nav;

public interface BleDeviceConnectionEvent {
    void OnDeviceConnectionStateChanged(boolean isConnected);
}
