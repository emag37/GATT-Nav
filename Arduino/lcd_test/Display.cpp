#include "Display.hpp"

#include <math.h>

#include "LCD_Functions.h"
#include "compass_arrow.h"

static constexpr float TO_RADIANS = M_PI / 180.0f;


static constexpr float compass_arrow_center_x = static_cast<float>(COMPASS_ARROW_WIDTH) / 2.0f;
static constexpr float compass_arrow_center_y = static_cast<float>(COMPASS_ARROW_HEIGHT) / 2.0f;
static constexpr float compass_offset_x = static_cast<float>(LCD_WIDTH) / 2.0f - compass_arrow_center_x;
static constexpr float compass_offset_y = static_cast<float>(LCD_HEIGHT) / 2.0f - compass_arrow_center_y + 5;
    
static constexpr int speed_start_x = 0, speed_start_y = 0;
static constexpr int dist_start_y = 0;

const char speed_units[] = "km/h";
const char dist_units[] = "km";

Display::Display() {
    lcdBegin();
}

void Display::Draw() {
    updateDisplay();
}

void Display::Clear() {
    clearDisplay(false);
    updateDisplay();
}

void Display::SetSpeed(float newSpeedKmh) {
    char val_buf[6];
    sprintf(val_buf, "%02.1f", newSpeedKmh);
    setStr(val_buf, speed_start_x, speed_start_y, true);
    setStr(speed_units, speed_start_x, speed_start_y + LCD_CHAR_HEIGHT, true);
}

void Display::SetDistance(float newDistanceKm) {
  char val_buf[6];
  sprintf(val_buf, "%03.1f", newDistanceKm);
  int dist_start_x = LCD_WIDTH - ((strlen(val_buf) + 1) * LCD_CHAR_WIDTH);
  setStr(val_buf, dist_start_x, dist_start_y, true);
  setStr(dist_units, dist_start_x, dist_start_y + LCD_CHAR_HEIGHT, true);
}

void Display::SetCompass(float degrees) {
    for(int i = 0; i < COMPASS_ARROW_WIDTH; i++) {
        for(int j=0; j < COMPASS_ARROW_HEIGHT; j++) {
            byte shift = j % 8;
            bool pixel = (compass_arrow[i + (j/8) * COMPASS_ARROW_WIDTH] & (1<<shift)) != 0;
            float radians = degrees * TO_RADIANS;
            float x_rot = cosf(radians) * (static_cast<float>(i) - compass_arrow_center_x) - sinf(radians) * (static_cast<float>(j) - compass_arrow_center_y) + compass_arrow_center_x;
            float y_rot = sinf(radians) * (static_cast<float>(i) - compass_arrow_center_x) + cosf(radians) * (static_cast<float>(j) - compass_arrow_center_y) + compass_arrow_center_y;
            
            float x_trans = x_rot + compass_offset_x;
            float y_trans = y_rot + compass_offset_y;
            setPixel(static_cast<int>(roundf(x_trans)), static_cast<int>(roundf(y_trans)), pixel);
        }
    }
}
