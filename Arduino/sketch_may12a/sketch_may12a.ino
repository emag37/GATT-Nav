#include "BLEDevice.h"

static BLEClient* bleClient;
static BLEUUID gattNavServiceUUID("04831534-ace0-4ce2-aae6-e2bfd499016a");
static BLEUUID nextDirectionDistanceUUID("76da4411-b2de-4bd6-b426-22a14912fea1");
static BLEUUID speedUUID("6156d492-e4a5-4e4c-a189-391791e05301");

static bool doConnect = false;
static bool isConnected = false;
static BLERemoteCharacteristic* speedCharacteristic;
static BLERemoteCharacteristic* nextDirectionDistanceCharacteristic;
static BLEAdvertisedDevice* navDevice;

void onNewNotification(BLERemoteCharacteristic* pChar, uint8_t* pData, size_t length, bool isNotify) {
    if( pChar == nextDirectionDistanceCharacteristic) {
      Serial.println("We got a notification for next direction and distance");
    } else if ( pChar == speedCharacteristic) {
      Serial.println("We got a notification for speed.");
    } else {
      Serial.println("We got an unknown notifiation");
    }
    Serial.print("Data: ");
     for(int i=0; i< length; i++) {
        Serial.print(pData[i],HEX);
     }
    Serial.println();
  }

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
 /*
   * Called for each advertising BLE server.
   */
  void onResult(BLEAdvertisedDevice advertisedDevice) {
    Serial.print("BLE Advertised Device found: ");
    Serial.println(advertisedDevice.toString().c_str());

    // We have found a device, let us now see if it contains the service we are looking for.
    if (advertisedDevice.haveServiceUUID() && advertisedDevice.isAdvertisingService(gattNavServiceUUID)) {
      Serial.print("BLE Advertised Device has the nav service ID.");
      BLEDevice::getScan()->stop();
      navDevice = new BLEAdvertisedDevice(advertisedDevice);
      doConnect = true;

    } // Found our server
  } // onResult
}; // MyAdvertisedDeviceCallbacks

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

    speedCharacteristic = pRemoteService->getCharacteristic(speedUUID);
    Serial.println(" - Found speed characteristic");
    if (speedCharacteristic == nullptr) {
      Serial.print("Failed to find our characteristic UUID: ");
      Serial.println(speedUUID.toString().c_str());
      bleClient->disconnect();
      return false;
    }

    if(speedCharacteristic->canNotify()){
      Serial.println("Register speed notification");
      speedCharacteristic->registerForNotify(onNewNotification);
    }


     nextDirectionDistanceCharacteristic = pRemoteService->getCharacteristic(nextDirectionDistanceUUID);
    if (nextDirectionDistanceCharacteristic == nullptr) {
      Serial.print("Failed to find our characteristic UUID: ");
      Serial.println(nextDirectionDistanceUUID.toString().c_str());
      bleClient->disconnect();
      return false;
    }
    Serial.println(" - Found next direction & distance characteristic");
    if(nextDirectionDistanceCharacteristic->canNotify()){
      nextDirectionDistanceCharacteristic->registerForNotify(onNewNotification);
    }


    delay(250);
    // Read the value of the characteristic.
    if(nextDirectionDistanceCharacteristic->canRead()) {
      Serial.println("Try reading the next direction");
      std::string value = nextDirectionDistanceCharacteristic->readValue();
      Serial.print("The direction/distance characteristic value was: ");
      for(int i=0; i< value.size(); i++) {
        Serial.print(value[i],HEX);
      }
      Serial.println();
    }
    // Read the value of the characteristic.
    if(speedCharacteristic->canRead()) {
      Serial.println("Try reading the next speed");
      std::string speedValue = speedCharacteristic->readValue();
      Serial.print("The speed characteristic value was: ");
      for(int i=0; i< speedValue.size(); i++) {
        Serial.print(speedValue[i],HEX);
      }
      Serial.println();
    }
    doConnect = false;
}

void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  Serial.println("Initializing BLE client");
  
  BLEDevice::init("Gatt_Nav");
  bleClient = BLEDevice::createClient();
  BLEScan* pBLEScan = BLEDevice::getScan();
  pBLEScan->setAdvertisedDeviceCallbacks(new GattNavAdvertizedDeviceCallbacks());
  pBLEScan->setInterval(1349);
  pBLEScan->setWindow(449);
  pBLEScan->setActiveScan(true);
  Serial.println("Starting scan...");
  pBLEScan->start(5, true);
}

void loop() {
  if(doConnect) {
    connectToServer();
  }
}
