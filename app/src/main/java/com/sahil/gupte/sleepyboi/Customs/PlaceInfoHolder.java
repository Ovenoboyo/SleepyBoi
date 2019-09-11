package com.sahil.gupte.sleepyboi.Customs;

public class PlaceInfoHolder {
    private long latitude;
    private long longitude;
    private String address;

    public PlaceInfoHolder(Double lat, Double lng, String add) {
        latitude = Double.doubleToRawLongBits(lat);
        longitude = Double.doubleToRawLongBits(lng);
        address = add;
    }

    public long getLatitude() {
        return latitude;
    }

    public long getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

}
