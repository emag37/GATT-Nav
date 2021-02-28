#include <utility>
#include <string>
#include <esp_wifi.h>
#include <driver/adc.h>
#include <driver/rtc_io.h>
#include <esp_err.h>
#include <esp_pm.h>
#include <esp32/ulp.h>

#include "ulp_main.h"
#include "freertos/task.h"

#include "BLEDevice.h"
#include "Display.hpp"
#include "NavBleClient.hpp"
#include "SerialLogger.hpp"

static Display* display;
static NavBleClient* nav_ble_client;

extern const uint8_t ulp_bin_start[] asm("_binary_ulp_main_bin_start");
extern const uint8_t ulp_bin_end[]   asm("_binary_ulp_main_bin_end");

void UpdateDisplayFromNav() {
    auto nav = nav_ble_client->GetNavData();

    if (nav.is_nav_valid()) {
      display->SetValuesAndDraw(nav.compass_angle, nav.speed, nav.distance_to_dest);
    } else if (nav.is_speed_valid()) {
      display->SetValuesAndDraw(nav.speed);
    }
}

void HoldLCDPinsForSleep() {
  gpio_hold_en(GPIO_NUM_23);
}

void ReleaseLCDPinsOnWakeup() {
  gpio_hold_dis(GPIO_NUM_23);
}

void setup() {
  // Backlight pin setup
  ESP_ERROR_CHECK(rtc_gpio_init(GPIO_NUM_4));
  ESP_ERROR_CHECK(rtc_gpio_set_direction(GPIO_NUM_4, RTC_GPIO_MODE_OUTPUT_ONLY));
  ESP_ERROR_CHECK(rtc_gpio_pulldown_dis(GPIO_NUM_4));
  ESP_ERROR_CHECK(rtc_gpio_pullup_dis(GPIO_NUM_4));
  
  ESP_ERROR_CHECK(ulp_set_wakeup_period(0, 2000));
  ESP_ERROR_CHECK(ulp_load_binary(0, ulp_bin_start, (ulp_bin_end - ulp_bin_start) / sizeof(uint32_t)));
  ESP_ERROR_CHECK(ulp_run(&ulp_entry - RTC_SLOW_MEM));

  
  // Enable light sleep and reduce CPU frequency
  esp_pm_config_esp32_t pm_config = {
        .max_freq_mhz = 160, // e.g. 80, 160, 240
        .min_freq_mhz = 40, // e.g. 40
        .light_sleep_enable = true, // enable light sleep
    };
  
  esp_wifi_set_mode(WIFI_MODE_NULL);
  esp_wifi_stop();
  esp_pm_configure(&pm_config);
  esp_bt_controller_enable(ESP_BT_MODE_BLE);
  esp_bt_sleep_enable();
  gpio_deep_sleep_hold_en();
  adc_power_off();

  display = new Display();
  display->Message("Startup...");
  nav_ble_client = new NavBleClient();
  LOG() << "Starting...";
  
  // Reduce BLE power level
  esp_ble_tx_power_set(ESP_BLE_PWR_TYPE_DEFAULT, ESP_PWR_LVL_N12);
}

void loop() {
  if (nav_ble_client->GetState() == NavBleClient::State::CONNECTED) {
    UpdateDisplayFromNav();
    //LOG() << "Updated display, suspending";
    nav_ble_client->Suspend();
    HoldLCDPinsForSleep();
    delay(1500);
  } else if (nav_ble_client->GetState() == NavBleClient::State::SUSPENDED) {
    //LOG() << "Resuming BLE...";
    ReleaseLCDPinsOnWakeup();
    nav_ble_client->Wakeup();
  } else {
    adc_power_on();
    char val_buf[20];
    double voltage = ((static_cast<double>(analogRead(34)) / 4095) * 3.9) * 2;
    sprintf(val_buf, "%01.4lf", voltage);
    display->Message(nav_ble_client->GetStateStr() + " " + val_buf);
    adc_power_off();
  }
  nav_ble_client->ProcessEvents();
}
