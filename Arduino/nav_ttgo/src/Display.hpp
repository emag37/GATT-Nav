#pragma once
#include <TFT_eSPI.h>


#include <string>

#include "CompassArrow.h"
class Display {
public:
    Display();
    
    void Message(const std::string& message);
    void SetValuesAndDraw(float compass_angle, float speed_kmh, float distance_km);


private:
    CompassArrow _arrow;
    float _compass_offset_x;
    float _compass_offset_y;
    TFT_eSPI _tft;
    TFT_eSprite _sprite;
    void WriteString(int x, int y, const char* to_write);
    void SetCompass(int degrees);
    void SetSpeed(float newSpeedKmh);
    void SetDistance(float newDistanceKm);
    void Clear();
};
