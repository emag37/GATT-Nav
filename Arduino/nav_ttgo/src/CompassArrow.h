#pragma once

#include <stdint.h>
#include <stddef.h>

class CompassArrow {
    public:
    const uint16_t* bmp() const;
    size_t BmpLength() const;
    constexpr int width() {return 134;}
    constexpr int height() {return 134;}

    constexpr int CenterX() {return width() >> 1;}
    constexpr int CenterY() {return height() >> 1;}
};