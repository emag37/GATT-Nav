package com.gatt_nav;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class MedianLocationFilter
{
    MedianFilter[] filters;

    public MedianLocationFilter(int kernelSize) {
        filters = new MedianFilter[] {new MedianFilter(kernelSize), new MedianFilter(kernelSize)};
    }

    public LatLng filterAndOutput(Location location) {
        return new LatLng(filters[0].filterValueAndOutput(location.getLatitude()), filters[1].filterValueAndOutput(location.getLongitude()));
    }
}
