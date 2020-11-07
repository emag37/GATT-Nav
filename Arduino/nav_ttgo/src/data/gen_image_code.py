import sys
import os
import imageio
import numpy as np

path = sys.argv[1]
image = imageio.imread(path)

with open('out.c','w') as out_file:
    out_file.write('static const uint16_t bmp_data[] PROGMEM = {\n\t')
    for row in image:
        for column in row:
            r = int((31 * int(column[0])) / 255)
            g = int((63 * int(column[1])) / 255)
            b = int((31 * int(column[1])) / 255)
            out_val = r << 11
            out_val |= g << 5
            out_val |= b
            out_file.write(f"{out_val:#0{6}x}, ")
        out_file.write('\n\t')
    out_file.write('};\n\n')
    out_file.write(f"constexpr int width = {image.shape[0]}\n")
    out_file.write(f"constexpr int height = {image.shape[1]}\n")