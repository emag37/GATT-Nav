#include "SerialLogger.hpp"

#define LOG_LOCAL_LEVEL ESP_LOG_DEBUG
#include "esp_log.h"

static const int SERIAL_BAUD = 115200;

std::mutex SerialLog::_log_lock;

bool SerialLog::is_init = false;

SerialLog::~SerialLog() {
    std::lock_guard<decltype(_log_lock)> lock(_log_lock);
    if (!is_init) {
        esp_log_level_set("GATTNAV", ESP_LOG_DEBUG);
        is_init = true; 
    }
    ESP_LOGD("GATTNAV", "%s", str().c_str());
}