#include <utility>
#include <string>

#include "BLEDevice.h"
#include "Display.hpp"
#include "NavBleClient.hpp"
#include "SerialLogger.hpp"


static Display* display;
static NavBleClient* nav_ble_client;

void UpdateDisplayFromNav() {
    auto nav = nav_ble_client->GetNavData();
    if (nav.is_nav_valid()) {
      display->SetValuesAndDraw(nav.compass_angle, nav.speed, nav.distance_to_dest);
    } else if (nav.is_speed_valid()) {
      display->SetValuesAndDraw(nav.speed);
    }
}

void setup() {

  LOG() << "Initializing BLE client";

  display = new Display();
  display->Message("Startup...");
  nav_ble_client = new NavBleClient();
  LOG() << "Starting...";
  
  esp_bt_sleep_enable();
}

void loop() {
  if (nav_ble_client->GetState() == NavBleClient::State::CONNECTED) {
    UpdateDisplayFromNav();
    delay(1000);
  } else {
    display->Message(nav_ble_client->GetStateStr());
    delay(250);
  }
  nav_ble_client->ProcessEvents();
}
