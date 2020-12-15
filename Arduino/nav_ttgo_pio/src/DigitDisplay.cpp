#include "DigitDisplay.hpp"


DigitDisplay::DigitDisplay(TFT_eSPI& tft, const std::string& label, const std::string& format, int upper_left_x, int upper_left_y) :
_tft(tft), 
_label(label), 
_format(format), 
_upper_left_x(upper_left_x), 
_upper_left_y(upper_left_y) {

}

int DigitDisplay::StartOffsetForCentered(int to_center_x, const std::string& to_center, int centered_to_width) {
  int to_display_width = _tft.textWidth(to_center.c_str());

  return (centered_to_width / 2 - to_center_x) - to_display_width / 2;
}

void DigitDisplay::Update(float new_value) {
  char val_buf[20];
  sprintf(val_buf, _format.c_str(), new_value);

  _tft.setTextSize(3);
  _tft.setCursor(_upper_left_x, _upper_left_y);
  _tft.print(val_buf);
  int printed_width = _tft.textWidth(val_buf);
  
  _tft.setTextSize(1);
  int offset = StartOffsetForCentered(_upper_left_x, _label, printed_width);
  _tft.setCursor(offset, _upper_left_y - 10);
  _tft.print(_label.c_str());
}