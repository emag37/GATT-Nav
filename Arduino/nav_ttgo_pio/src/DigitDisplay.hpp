#pragma once

#include <string>

#include "TFT_eSPI.h"

class DigitDisplay {
    TFT_eSPI& _tft;
    const std::string _label;
    const std::string _format;
    const int _upper_left_x, _upper_left_y;

    int StartOffsetForCentered(int to_center_x, const std::string& to_center, int centered_to_width);
public:
    DigitDisplay(TFT_eSPI& tft, const std::string& label, const std::string& format, int upper_left_x, int upper_left_y);
    void Update(float new_value);
};