#include "Display.hpp"

#include <math.h>



static constexpr float implicit_rotation_deg = 90.0;
static constexpr int speed_start_x = 0, speed_start_y = 0;
static constexpr int dist_start_x = 0;
static constexpr int dist_start_y = 30;
static constexpr int MSG_START_X = 0;
static constexpr int MSG_START_Y = 10;

const char speed_units[] = "km/h";
const char dist_units[] = "km";

Display::Display(): _tft(TFT_eSPI()), _sprite(&_tft) {
   _tft.init();
   _tft.setRotation(0);
   _tft.fillScreen(TFT_BLACK);
   _tft.setTextSize(2);
   _tft.setTextColor(TFT_BLUE);
   _tft.setTextFont(2);
   _tft.setTextDatum(MC_DATUM);
   _compass_offset_x = static_cast<float>(_tft.width()) / 2.0f - _arrow.CenterX();
   _compass_offset_y = 100;
   Serial.println(_compass_offset_y);

   _sprite.setColorDepth(16);
   _sprite.setSwapBytes(true);
   _sprite.createSprite(_arrow.width(), _arrow.height());
   _sprite.setPivot(_sprite.width() / 2, _sprite.height() / 2);
   _tft.setPivot(_compass_offset_x + _arrow.CenterX(), _compass_offset_y + _arrow.CenterY());
   _sprite.pushSprite(_compass_offset_x, _compass_offset_y);
   _sprite.pushImage(0, 0, _arrow.width(), _arrow.height(), _arrow.bmp());
}


void Display::Clear() {
  Serial.println("Clear!");
   _tft.fillScreen(TFT_BLACK);
}

void Display::Message(const std::string& message) {
  Clear();
  WriteString(MSG_START_X, MSG_START_Y, message.c_str());
}

void Display::SetValuesAndDraw(float compass_angle, float speed_kmh, float distance_km){
  Clear();
  SetSpeed(speed_kmh);
  SetDistance(distance_km);
  SetCompass(compass_angle);
}

void Display::WriteString(int x, int y, const char* to_write) {
  _tft.setCursor(x, y);
  _tft.print(to_write);
}

void Display::SetSpeed(float newSpeedKmh) {
    char val_buf[20];
    sprintf(val_buf, "%02.1f %s", newSpeedKmh, speed_units);
    WriteString(speed_start_x, speed_start_y, val_buf);
}

void Display::SetDistance(float newDistanceKm) {
  char val_buf[20];
  sprintf(val_buf, "%03.1f %s", newDistanceKm, dist_units);
  WriteString(dist_start_x, dist_start_y, val_buf);
}

void Display::SetCompass(int degrees) {
    _sprite.pushRotated((int)degrees);
}
