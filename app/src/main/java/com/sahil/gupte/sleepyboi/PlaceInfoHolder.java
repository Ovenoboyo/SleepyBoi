package com.sahil.gupte.sleepyboi;

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

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
