#include "Display.hpp"

#include <cmath>

#include "SerialLogger.hpp"

static constexpr int MSG_START_X = 0;
static constexpr int MSG_START_Y = 10;
static constexpr int COMPASS_OFFSET_X = 174 - CompassArrow::CenterX();
static constexpr int COMPASS_OFFSET_Y = 60 - CompassArrow::CenterY();

Display::Display(): _tft(TFT_eSPI()), _compass_sprite(&_tft),
  _speed_display(_tft, "km/h", "%02.1f", 0, 10),
  _distance_remaining_display(_tft, "km left", "%02.1f", 0, 80)
 {
   _tft.init();
   _tft.setRotation(1);
   _tft.fillScreen(TFT_BLACK);
   _tft.setTextColor(TFT_WHITE);
   _tft.setTextFont(2);
   _tft.setTextDatum(MC_DATUM);
   //LOG() << "Width: " << _tft.width() << " height: " << _tft.height();

   _compass_offset_y = _tft.height() / 2 - CompassArrow::CenterY();
   _compass_sprite.setColorDepth(16);
   _compass_sprite.setSwapBytes(true);
   _compass_sprite.createSprite(CompassArrow::width(), CompassArrow::height());
   _compass_sprite.setPivot(_compass_sprite.width() / 2, _compass_sprite.height() / 2);
   _tft.setPivot(COMPASS_OFFSET_X + CompassArrow::CenterX(), _compass_offset_y + CompassArrow::CenterY());
   _compass_sprite.pushSprite(COMPASS_OFFSET_X, _compass_offset_y);
   _compass_sprite.pushImage(0, 0, CompassArrow::width(), CompassArrow::height(), CompassArrow::bmp());
}

void Display::Clear() {
   _tft.fillScreen(TFT_BLACK);
}

void Display::Message(const std::string& message) {
  Clear();
  WriteString(MSG_START_X, MSG_START_Y, message.c_str());
}

void Display::SetValuesAndDraw(float speed_kmh) {
  SetValuesAndDraw(_current_compass_angle_deg, speed_kmh, _current_distance_remaining_km);
  DrawRedXOverCompass();
}

void Display::SetValuesAndDraw(float compass_angle, float speed_kmh, float distance_km){
  Clear();
  SetSpeed(speed_kmh);
  SetDistance(distance_km);
  SetCompass(compass_angle);
}

int Display::WriteString(int x, int y, const char* to_write, int fontsize) {
  _tft.setTextSize(fontsize);
  _tft.setCursor(x, y);
  _tft.print(to_write);
  return _tft.textWidth(to_write);
}

void Display::SetSpeed(float new_speed_kmh) {
  _current_speed_kmh = new_speed_kmh;
  _speed_display.Update(new_speed_kmh);
}

void Display::SetDistance(float new_distance_km) {
 _current_distance_remaining_km = new_distance_km;
 _distance_remaining_display.Update(new_distance_km);
}

void Display::SetCompass(float degrees) {
  _current_compass_angle_deg = degrees;
  _compass_sprite.pushRotated(static_cast<int16_t>(::roundf(degrees)), TFT_BLACK);
}

void Display::DrawRedXOverCompass() {
  for(int i = -3; i <= 3 ; i++) {
    _tft.drawLine(COMPASS_OFFSET_X, _compass_offset_y + i, COMPASS_OFFSET_X + CompassArrow::width(), _compass_offset_y + CompassArrow::height() + i, TFT_RED);
    _tft.drawLine(COMPASS_OFFSET_X, _compass_offset_y + CompassArrow::height() + i, COMPASS_OFFSET_X + CompassArrow::width(), _compass_offset_y + i, TFT_RED);
  }
}