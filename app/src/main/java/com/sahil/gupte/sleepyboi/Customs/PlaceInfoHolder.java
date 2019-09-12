package com.sahil.gupte.sleepyboi.Customs;

public class PlaceInfoHolder {
    private double latitude;
    private double longitude;
    private String address;
    private String name;
    private int dbCount;

    public PlaceInfoHolder(Double lat, Double lng, String add, String name, int count) {
        latitude = lat;
        longitude = lng;
        address = add;
        this.name = name;
        dbCount = count;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public int getDbCount() {
        return dbCount;
    }

}
