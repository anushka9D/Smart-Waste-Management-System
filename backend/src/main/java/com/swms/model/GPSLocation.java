package com.swms.model;

public class GPSLocation {
    private double latitude;
    private double longitude;
    
    // Default constructor
    public GPSLocation() {
    }
    
    // Constructor with all fields
    public GPSLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    // Getters
    public double getLatitude() {
        return latitude;
    }
    
    public double getLongitude() {
        return longitude;
    }
    
    // Setters
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}