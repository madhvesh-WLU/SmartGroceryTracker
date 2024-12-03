package com.example.smartgrocerytracker.ModelClass;
public class Place {
    public String name;
    public String address;
    public String status;
    public String distance;
    public String estTime;
    public double latitude;
    public double longitude;

    public Place(String name, String address, String status, String distance, String estTime, double latitude, double longitude) {
        this.name = name;
        this.address = address;
        this.status = status;
        this.distance = distance;
        this.estTime = estTime;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
