#include "SerialLogger.hpp"

#include <HardwareSerial.h>

static const int SERIAL_BAUD = 115200;

std::mutex SerialLog::_log_lock;

bool SerialLog::is_init = false;

SerialLog::~SerialLog() {
    std::lock_guard<decltype(_log_lock)> lock(_log_lock);
    if (!is_init) {
        Serial.begin(SERIAL_BAUD);
        is_init = true;
    }
    Serial.println(str().c_str());
}