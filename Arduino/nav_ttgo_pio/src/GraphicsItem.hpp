#pragma once

#include <stdint.h>
#include <stddef.h>

template <uint16_t Width, uint16_t Height, typename Derived> 
class GraphicsItem {
public:
    GraphicsItem() = delete;
    
    static const uint16_t* bmp() { return Derived::bmp();}

    static size_t BmpLength() { return Derived::BmpLength();}

    static constexpr int width() {return Width;}
    static constexpr int height() {return Height;}

    static constexpr int CenterX() {return width() >> 1;}
    static constexpr int CenterY() {return height() >> 1;}
};