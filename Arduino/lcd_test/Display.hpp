#pragma once
class Display {
public:
    Display();
    void SetCompass(float degrees);
    void SetSpeed(float newSpeedKmh);
    void SetDistance(float newDistanceKm);
    void SetConnected(bool isConnected){};
    
    void Clear();
    void Draw();
};
