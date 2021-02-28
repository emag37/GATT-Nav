#pragma once


#include <sstream>
#include <mutex>

#define LOG() SerialLog()

class SerialLog : public std::stringstream {
    static std::mutex _log_lock;
    static bool is_init;

public:
    friend std::ostream& operator<<(std::ostream& os, const SerialLog& dt);
    SerialLog() = default;
    ~SerialLog();
};