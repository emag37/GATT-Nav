#include "Display.hpp"

#include <cmath>

#include "SerialLogger.hpp"

static constexpr int speed_start_x = 0, speed_start_y = 10;
static constexpr int dist_start_x = 0;
static constexpr int dist_start_y = 80;
static constexpr int MSG_START_X = 0;
static constexpr int MSG_START_Y = 10;
static constexpr int COMPASS_OFFSET_X = 174 - CompassArrow::CenterX();
static constexpr int COMPASS_OFFSET_Y = 60 - CompassArrow::CenterY();

const char speed_units[] = "km/h";
const char dist_units[] = "km left";

Display::Display(): _tft(TFT_eSPI()), _sprite(&_tft) {
   _tft.init();
   _tft.setRotation(1);
   _tft.fillScreen(TFT_BLACK);
   _tft.setTextColor(TFT_WHITE);
   _tft.setTextFont(2);
   _tft.setTextDatum(MC_DATUM);
   //LOG() << "Width: " << _tft.width() << " height: " << _tft.height();

   _compass_offset_y = _tft.height() / 2 - CompassArrow::CenterY();
   _sprite.setColorDepth(16);
   _sprite.setSwapBytes(true);
   _sprite.createSprite(_arrow.width(), _arrow.height());
   _sprite.setPivot(_sprite.width() / 2, _sprite.height() / 2);
   _tft.setPivot(COMPASS_OFFSET_X + _arrow.CenterX(), _compass_offset_y + _arrow.CenterY());
   _sprite.pushSprite(COMPASS_OFFSET_X, _compass_offset_y);
   _sprite.pushImage(0, 0, _arrow.width(), _arrow.height(), _arrow.bmp());
}


void Display::Clear() {
   _tft.fillScreen(TFT_BLACK);
}


int Display::StartOffsetForCentered(int to_center_x, const String& to_center, int centered_to_width) {
  int to_display_width = _tft.textWidth(to_center);

  return (centered_to_width / 2 - to_center_x) - to_display_width / 2;
}

void Display::Message(const std::string& message) {
  Clear();
  WriteString(MSG_START_X, MSG_START_Y, message.c_str());
}

void Display::SetValuesAndDraw(float speed_kmh) {
  SetValuesAndDraw(_current_compass_angle_deg, speed_kmh, _current_distance_km);
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

void Display::SetSpeed(float newSpeedKmh) {
  _current_speed_kmh = newSpeedKmh;
  char val_buf[20];
  sprintf(val_buf, "%02.1f", newSpeedKmh);
  int width = WriteString(speed_start_x, speed_start_y, val_buf, 3);
  _tft.setTextSize(1);
  int offset = StartOffsetForCentered(speed_start_x, speed_units, width);
  WriteString(offset, speed_start_y - 10, speed_units, 1);
}

void Display::SetDistance(float newDistanceKm) {
  _current_distance_km = newDistanceKm;
  char val_buf[20];
  sprintf(val_buf, "%03.1f", newDistanceKm);
  int width = WriteString(dist_start_x, dist_start_y, val_buf, 3);
  _tft.setTextSize(1);
  int offset = StartOffsetForCentered(dist_start_x, dist_units, width);
  WriteString(offset, dist_start_y - 10, dist_units, 1);
}

void Display::SetCompass(float degrees) {
  _current_compass_angle_deg = degrees;
  _sprite.pushRotated(static_cast<int16_t>(::roundf(degrees)));
}
