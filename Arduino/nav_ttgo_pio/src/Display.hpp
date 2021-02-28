#pragma once

// Note: Select #include <User_Setups/Setup25_TTGO_T_Display.h> in User_Setup_Select.h 
// and undefine TFT_BL (we want to control the backlight pin independently)
#include <TFT_eSPI.h>

#include <string>

#include "CompassArrow.h"
#include "DigitDisplay.hpp"

class Display {
public:
    Display();
    
    void Message(const std::string& message);
    void SetValuesAndDraw(float compass_angle, float speed_kmh, float distance_km);
    void SetValuesAndDraw(float speed_kmh);
private:
    TFT_eSPI _tft;
    TFT_eSprite _compass_sprite;
    DigitDisplay _speed_display;
    DigitDisplay _distance_remaining_display;

    int WriteString(int x, int y, const char* to_write, int fontsize=2);
    
    void SetCompass(float degrees);
    void SetSpeed(float newSpeedKmh);
    void SetDistance(float newDistanceKm);
    void DrawRedXOverCompass();
    void Clear();

    int _compass_offset_y;
    float _current_compass_angle_deg = 0;
    float _current_speed_kmh = 0;
    float _current_distance_remaining_km = 0;
};
