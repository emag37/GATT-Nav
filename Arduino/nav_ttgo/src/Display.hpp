#pragma once
#include <TFT_eSPI.h>


#include <string>

#include "CompassArrow.h"
class Display {
public:
    Display();
    
    void Message(const std::string& message);
    void SetValuesAndDraw(float compass_angle, float speed_kmh, float distance_km);
    void SetValuesAndDraw(float speed_kmh);
private:
    CompassArrow _arrow;
    float _compass_offset_x;
    float _compass_offset_y;
    TFT_eSPI _tft;
    TFT_eSprite _sprite;
    void WriteString(int x, int y, const char* to_write);
    void SetCompass(float degrees);
    void SetSpeed(float newSpeedKmh);
    void SetDistance(float newDistanceKm);
    void Clear();

    float _current_compass_angle_deg = 0;
    float _current_speed_kmh = 0;
    float _current_distance_km = 0;
};
