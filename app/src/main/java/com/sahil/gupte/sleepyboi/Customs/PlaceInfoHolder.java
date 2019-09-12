package com.sahil.gupte.sleepyboi.Customs;

public class PlaceInfoHolder {
    private long latitude;
    private long longitude;
    private String address;
    private String name;

    public PlaceInfoHolder(Double lat, Double lng, String add,String name) {
        latitude = Double.doubleToRawLongBits(lat);
        longitude = Double.doubleToRawLongBits(lng);
        address = add;
        this.name = name;
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

    public String getName() {
        return name;
    }

}
