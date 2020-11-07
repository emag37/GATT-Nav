#pragma once

#include <stdint.h>
#include <stddef.h>

class CompassArrow {
    public:
    const uint16_t* bmp() const;
    size_t BmpLength() const;
    static constexpr int width() {return 108;}
    static constexpr int height() {return 108;}

    static constexpr int CenterX() {return width() >> 1;}
    static constexpr int CenterY() {return height() >> 1;}
};