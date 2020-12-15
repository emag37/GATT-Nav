#pragma once

#include "GraphicsItem.hpp"

class CompassArrow : public GraphicsItem<108,108,CompassArrow>{
public:
    static const uint16_t* bmp();
    static size_t BmpLength();
};