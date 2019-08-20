#pragma once

#include <string>

class Display {
public:
    Display();
    
    void Message(const std::string& message);
    void SetValuesAndDraw(float compass_angle, float speed_kmh, float distance_km);


private:
    void SetCompass(float degrees);
    void SetSpeed(float newSpeedKmh);
    void SetDistance(float newDistanceKm);
    void Clear();
    void Draw();
};
