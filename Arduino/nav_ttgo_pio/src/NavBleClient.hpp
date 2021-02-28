#pragma once
#include <memory>
#include <atomic>
#include <functional>

#include "BLEDevice.h"

#include "SerialLogger.hpp"

class NavBleClient : public BLEClientCallbacks {
public:
    enum class State: int {
        DISCONNECTED,
        SCANNING,
        CONNECTING,
        CONNECTED,
        SUSPENDED
    };

    struct NavData { 
        float compass_angle;
        float distance_to_dest;
        float speed;

        bool is_speed_valid() const { return speed >= 0; }
        bool is_nav_valid() const { return distance_to_dest >= 0;}
    };

private:
    std::function<void()> _next_event;
    std::unique_ptr<BLEClient> _ble_client;
    BLEScan* _ble_scan; // This is a singleton managed by the BLEDevice
    std::unique_ptr<BLEAdvertisedDevice> _nav_device;
    BLERemoteCharacteristic* _nav_data_characteristic; // Managed by the BLEClient

    static const BLEUUID _gattnav_service_id;
    static const BLEUUID _nav_data_id;
    std::atomic<State> _current_state;
    
    void OnScanComplete(BLEScanResults* results);
    void OnStateEntered(State new_state);
    void OnStateExited(State exited_state);
    void TransitionTo(State new_state);
    void StartScan();
    void ConnectToServer();
    void Reset();
    
protected:
    void onConnect(BLEClient* client) override;
    void onDisconnect(BLEClient *pClient) override;
    bool validateDevice(BLEAdvertisedDevice device);
public:
    NavBleClient();
    void Suspend();
    void Wakeup();
    void ProcessEvents();
    State GetState() const;
    std::string GetStateStr() const;
    NavData GetNavData();
};