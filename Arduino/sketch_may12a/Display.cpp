#include "Display.hpp"

#include <math.h>

#include "LCD_Functions.h"
#include "compass_arrow.h"

namespace gattnav {

static constexpr float TO_RADIANS = M_PI / 180.0f;

void Display::Display() {
    lcdBegin();
}

void Display::Draw() {
    updateDisplay();
}

void Display::Clear() {
    clearDisplay(false);
}

void Display::SetSpeed(float newSpeed) {
    char charBuf[8];
    sprintf(charbuf, "%02.1fkm/h", newSpeed);
    setStr(charBuf, speed_start_x, speed_start_y, true);
}

void Display::SetCompass(float degrees) {
    for(int i = 0; i < COMPASS_ARROW_WIDTH; i++) {
        for(int j=0; j < COMPASS_ARROW_HEIGHT; j++) {
            byte shift = j % 8;
            bool pixel = (compass_arrow[i + (j/8) * COMPASS_ARROW_WIDTH] & (1<<shift)) != 0;
            float radians = degrees * TO_RADIANS;
            float x_rot = cos(radians) * (static_cast<float>(i) - compass_arrow_center_x) - sin(radians) * (static_cast<float>(j) - compass_arrow_center_y) + compass_arrow_center_x;
            float y_rot = sin(radians) * (static_cast<float>(i) - compass_arrow_center_x) + cos(radians) * (static_cast<float>(j) - compass_arrow_center_y) + compass_arrow_center_y;
            
            float x_trans = x_rot + compass_center_x;
            float y_trans = y_rot + compass_center_y;
            setPixel(static_cast<int>(x_trans), static_cast<int>(y_trans), pixel);
        }
    }
}

}