#include <Arduino.h>
#include <utility>
#include <string>

#include "BLEDevice.h"
#include "Display.hpp"

static const int SERIAL_BAUD = 115200;

static BLEClient* bleClient = nullptr;
static BLEScan* pBLEScan = nullptr;
static BLEUUID gattNavServiceUUID("04831534-ace0-4ce2-aae6-e2bfd499016a");
static BLEUUID navDataUUID("76da4411-b2de-4bd6-b426-22a14912fea1");

static bool doConnect = false;
static bool isConnected = false;
static bool isScanning = false;
static BLERemoteCharacteristic* speedCharacteristic;
static BLERemoteCharacteristic* navDataCharacteristic;
static BLEAdvertisedDevice* navDevice;

static Display* display;

class GatNavClientCallbacks: public BLEClientCallbacks {

  void onConnect(BLEClient *pClient){
    Serial.println("Client connected!");
    isConnected = true;
  }

  void onDisconnect(BLEClient *pClient) {
    Serial.println("client disconnected!");
    isConnected = false;
  }
};

class GattNavAdvertizedDeviceCallbacks: public BLEAdvertisedDeviceCallbacks {
  void onResult(BLEAdvertisedDevice advertisedDevice) {
    Serial.print("BLE Advertised Device found: ");
    Serial.println(advertisedDevice.toString().c_str());

    // We have found a device, let us now see if it contains the service we are looking for.
    if (advertisedDevice.haveServiceUUID() && advertisedDevice.isAdvertisingService(gattNavServiceUUID)) {
      Serial.print("BLE Advertised Device has the nav service ID.");
      isScanning = false;
      BLEDevice::getScan()->stop();
      navDevice = new BLEAdvertisedDevice(advertisedDevice);
      doConnect = true;

    } // Found our server
  } // onResult
};

static GattNavAdvertizedDeviceCallbacks* deviceCallbacks;

bool connectToServer() {
    Serial.print("Forming a connection to ");
    Serial.println(navDevice->getAddress().toString().c_str());
    
    bleClient = BLEDevice::createClient();
    Serial.println(" - Created client");

    bleClient->setClientCallbacks(new GatNavClientCallbacks());

    // Connect to the remove BLE Server.
    bleClient->connect(navDevice);  // if you pass BLEAdvertisedDevice instead of address, it will be recognized type of peer device address (public or private)
    Serial.println(" - Connected to server");

    // Obtain a reference to the service we are after in the remote BLE server.
    BLERemoteService* pRemoteService = bleClient->getService(gattNavServiceUUID);
    if (pRemoteService == nullptr) {
      Serial.print("Failed to find our service UUID: ");
      Serial.println(gattNavServiceUUID.toString().c_str());
      bleClient->disconnect();
      return false;
    }
    Serial.println(" - Found our service");


    navDataCharacteristic = pRemoteService->getCharacteristic(navDataUUID);
    if (navDataCharacteristic == nullptr) {
      Serial.print("Failed to find our characteristic UUID: ");
      Serial.println(navDataUUID.toString().c_str());
      bleClient->disconnect();
      return false;
    }
    Serial.println(" - Found next direction & distance characteristic");

    /*if(navDataCharacteristic->canNotify()){
      navDataCharacteristic->registerForNotify(onNewNotification);
    }*/

    doConnect = false;
    return true;
}

void byteSwap32(std::string& data_str) {
  for(int i=0; i < data_str.size(); i += sizeof(float)){
    std::swap(data_str[i], data_str[i+3]);
    std::swap(data_str[i+1],data_str[i+2]);
  }
}
void updateDisplayFromGatt() {
    if(navDataCharacteristic->canRead()) {
    
      std::string value_str = navDataCharacteristic->readValue();
      byteSwap32(value_str);
      const float* value_float_array = reinterpret_cast<const float*>(value_str.data());

      Serial.println("Got new values: ");
      for(int i=0; i< value_str.size(); i++) {
        Serial.print(value_str[i],HEX);
        Serial.print(" ");
      }
      float dist = value_float_array[0];
      float angle = value_float_array[1];
      float speed = value_float_array[2];
      
      Serial.println();
      Serial.println(value_str.c_str());
      Serial.print("Speed: ");
      Serial.print(speed);
      Serial.println();
      Serial.print("Angle: ");
      Serial.print(angle);
      Serial.println();
      Serial.print("Distance: ");
      Serial.println(dist);
      display->SetValuesAndDraw(angle, speed, dist);
    }
}

void startBleScanning() {
  if(!bleClient) {
    bleClient = BLEDevice::createClient();
    pBLEScan = BLEDevice::getScan();
  }
  pBLEScan->setAdvertisedDeviceCallbacks(deviceCallbacks);
  pBLEScan->setInterval(1349);
  pBLEScan->setWindow(449);
  pBLEScan->setActiveScan(true);
  pBLEScan->start(5, true);
  isScanning = true;
}

void setup() {
  doConnect = false;
  isConnected = false;
  isScanning = false;
  deviceCallbacks = new GattNavAdvertizedDeviceCallbacks();
  Serial.begin(SERIAL_BAUD);
  Serial.println("Initializing BLE client");

  display = new Display();

  display->Message("Scanning!");
  Serial.println("Starting scan...");
  BLEDevice::init("Gatt_Nav");
  esp_bt_sleep_enable();
}

void loop() {
  
  if(doConnect) {
    display->Message("Connecting!");
    connectToServer();
    delay(250);
  } else if(isConnected) {
    updateDisplayFromGatt();
    delay(1000);
  } else if(!isScanning) {
    display->Message("Reconnecting!");
    startBleScanning();
    delay(250);
  }
  /*float compassAngle = static_cast<float>(rand() % 360);
  float speed = static_cast<float>(rand() % 300) / 10.f;
  float distance =  static_cast<float>(rand() % 1000) / 10.f;
  display->SetValuesAndDraw(compassAngle, speed, distance);
  delay(1000);
  */
}
