#include "NavBleClient.hpp"

#include <utility>
#include <limits>
#include "Arduino.h"


const BLEUUID NavBleClient::_gattnav_service_id("04831534-ace0-4ce2-aae6-e2bfd499016a");
const BLEUUID NavBleClient::_nav_data_id("76da4411-b2de-4bd6-b426-22a14912fea1");

void byteSwap32(std::string& data_str) {
  for(int i=0; i < data_str.size(); i += sizeof(float)){
    std::swap(data_str[i], data_str[i+3]);
    std::swap(data_str[i+1],data_str[i+2]);
  }
}

std::string StateStr(NavBleClient::State state) {
    switch(state) {
        case NavBleClient::State::SCANNING:
            return "Scanning";
        case NavBleClient::State::DISCONNECTED:
            return "Disconnected";
        case NavBleClient::State::CONNECTED:
            return "Connected";
        case NavBleClient::State::CONNECTING:
            return "Connecting";  
        case NavBleClient::State::SUSPENDED:
            return "Suspended";          
    }
    return "Unknown";
}

NavBleClient::NavBleClient() : _current_state(State::DISCONNECTED) {
    LOG() << "Init nav BLE client";
    BLEDevice::init("Gatt_Nav");
    Reset();
}

bool NavBleClient::validateDevice(BLEAdvertisedDevice device) {
    if (device.haveServiceUUID() && device.isAdvertisingService(_gattnav_service_id)) {
      _nav_device = std::unique_ptr<BLEAdvertisedDevice>(new BLEAdvertisedDevice(device));
      return true;
    } // Found our server*/
    return false;   
}
 /*void NavBleClient::onResult(BLEAdvertisedDevice advertisedDevice) {
    // We have found a device, let us now see if it contains the service we are looking for.
    LOG() << "Got advertised BLE device: " << advertisedDevice.getServiceUUID().toString();
    if (advertisedDevice.haveServiceUUID() && advertisedDevice.isAdvertisingService(_gattnav_service_id)) {
      _nav_device = std::unique_ptr<BLEAdvertisedDevice>(new BLEAdvertisedDevice(advertisedDevice));
      TransitionTo(State::CONNECTING);
    } // Found our server*/

void NavBleClient::onConnect(BLEClient* client) {
    LOG() << "BLE Connected";
    TransitionTo(State::CONNECTED);
}

void NavBleClient::onDisconnect(BLEClient *pClient) {
    LOG() << "BLE disconnected";
    if (_current_state == State::CONNECTED) {
        TransitionTo(State::DISCONNECTED);
    }
}

void NavBleClient::TransitionTo(State new_state) {
    State current_state = _current_state;
    
    if (current_state == new_state) {
        return;
    }

    _current_state = new_state;

    LOG() << "Transitioning state! Old: " << StateStr(current_state) << " new: " << StateStr(new_state);
    OnStateExited(current_state);
    OnStateEntered(new_state);
}

void NavBleClient::ProcessEvents() {
    auto to_execute = decltype(_next_event)();
    std::swap(_next_event, to_execute);
    if (to_execute) {
        to_execute();
    }
}

void NavBleClient::OnStateEntered(State new_state) {
    switch(new_state) {
        case State::SCANNING:
            StartScan();
            break;
        case State::CONNECTING:
            ConnectToServer();
            break;    
        case State::CONNECTED:
            break;
        case State::DISCONNECTED:
            Reset();
            break;  
        case State::SUSPENDED:
            break;     
    }
}

void NavBleClient::OnStateExited(State current_state) {
    switch(current_state) {
        case State::SCANNING:
         _ble_scan->stop();
         break;
         default:
         break;
    }
}

void NavBleClient::StartScan() {
    LOG() << "Start BLE Scan";
    _ble_scan->setInterval(100);
    _ble_scan->setWindow(100);
    _ble_scan->setActiveScan(true);
    bool found_server = false;

    auto results = _ble_scan->start(2, false);
    LOG() << "BLE scan done with " << results.getCount() << " results";
    for (int i = 0; i < results.getCount() && !found_server; i++) {
        found_server |= validateDevice(results.getDevice(i));
    }

    LOG() << "Found server: " << found_server;
    if (found_server) {
        TransitionTo(State::CONNECTING);
    } else {
        _next_event = [&] { StartScan(); };
    }
}

NavBleClient::NavData NavBleClient::GetNavData() {
    NavBleClient::NavData result;

     if (_current_state == State::CONNECTED && _nav_data_characteristic && _nav_data_characteristic->canRead()) {
        std::string value_str = _nav_data_characteristic->readValue();
        byteSwap32(value_str);
        const float* value_float_array = reinterpret_cast<const float*>(value_str.data());
        result.distance_to_dest = value_float_array[0];
        result.compass_angle = value_float_array[1];
        result.speed = value_float_array[2];
     } else {
        result.distance_to_dest = -1;
        result.compass_angle = -1;
        result.speed = -1;
        LOG() << "Nav data invalid, wrong state or cannot read characteristic";
     }

     return result;
}

void NavBleClient::Suspend() {
    if (_current_state == State::CONNECTED) {
        TransitionTo(State::SUSPENDED);
        _ble_client->disconnect();
    } else {
        LOG() << "Cannot suspend BLE client in from current state";
    }
}

void NavBleClient::Wakeup() {
    if (_current_state == State::SUSPENDED) {
        _ble_client->connect(_nav_device.get());
    } else {
        LOG() << "Cannot wakeup BLE client from current state";
    }
}

void NavBleClient::ConnectToServer() {
    delay(500);
    LOG() << "Connecting to server";
    _ble_client->setClientCallbacks(this);

    // Connect to the remove BLE Server.
    _ble_client->connect(_nav_device.get());  // if you pass BLEAdvertisedDevice instead of address, it will be recognized type of peer device address (public or private)

    // Obtain a reference to the service we are after in the remote BLE server.
    BLERemoteService* pRemoteService = _ble_client->getService(_gattnav_service_id);
    if (pRemoteService == nullptr) {
      LOG() << "cannot connect, no remote service";
      _ble_client->disconnect();
      return;
    }

    _nav_data_characteristic = std::unique_ptr<BLERemoteCharacteristic>(pRemoteService->getCharacteristic(_nav_data_id));
    if (!_nav_data_characteristic) {
      LOG() << "cannot connect, no characteristic";
      _ble_client->disconnect(); 
      return;
    }
    LOG() << "Done connecting";
}

void NavBleClient::Reset() {
    LOG() << "Reset connection";
    if (_current_state != State::DISCONNECTED) {
        TransitionTo(State::DISCONNECTED);
        return;
    }

    _ble_client = std::unique_ptr<decltype(_ble_client)::element_type>(BLEDevice::createClient());
    _ble_scan = std::unique_ptr<decltype(_ble_scan)::element_type>(BLEDevice::getScan());
    TransitionTo(State::SCANNING);
}

NavBleClient::State NavBleClient::GetState() const {
    return _current_state;
}

std::string NavBleClient::GetStateStr() const {
    return StateStr(_current_state);
}