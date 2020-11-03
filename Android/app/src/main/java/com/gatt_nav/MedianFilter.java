package com.gatt_nav;

import java.util.Arrays;

public class MedianFilter {
    private double[] values;
    private int currentIndex;

    public MedianFilter(int kernelSize) {
        if (kernelSize < 1) {
            throw new RuntimeException("Median filter kernel size must be > 1");
        }
        values = new double[kernelSize];
        currentIndex = 0;
    }

    public double filterValueAndOutput(double value) {
        values[currentIndex] = value;
        Arrays.sort(values);
        currentIndex = (currentIndex + 1) % values.length;
        return output();
    }

    public double output() {
        return values[(values.length / 2) + 1];
    }
}
