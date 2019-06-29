#pragma once
namespace gattnav {
class Display {
public:
    Display();
    void SetCompass(float degrees);
    void SetSpeed(float newSpeed);
    void SetDistance(float newDistanceKm){};
    void SetConnected(bool isConnected){};
    
    void Clear();
    void Draw();
private:
    static constexpr float compass_center_x = static_cast<float>(LCD_WIDTH) / 2.0f;
    static constexpr float compass_center_y = static_cast<float>(LCD_HEIGHT) / 2.0f;
    static constexpr float compass_arrow_center_x = static_cast<float>(COMPASS_ARROW_WIDTH) / 2.0f;
    static constexpr float compass_arrow_center_y = static_cast<float>(COMPASS_ARROW_HEIGHT) / 2.0f;
    static constexpr int speed_start_x = 0, speed_start_y = 0;
};
}