package com.swms.dto;

import com.swms.model.GPSLocation;

public class SmartBinRequest {
    private String binId;
    private String location;
    private GPSLocation coordinates;
    private double currentLevel;
    private double capacity;
    private String lastCollected; // As string for easier JSON parsing
    
    // Default constructor
    public SmartBinRequest() {
    }
    
    // Getters
    public String getBinId() {
        return binId;
    }
    
    public String getLocation() {
        return location;
    }
    
    public GPSLocation getCoordinates() {
        return coordinates;
    }
    
    public double getCurrentLevel() {
        return currentLevel;
    }
    
    public double getCapacity() {
        return capacity;
    }
    
    public String getLastCollected() {
        return lastCollected;
    }
    
    // Setters
    public void setBinId(String binId) {
        this.binId = binId;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public void setCoordinates(GPSLocation coordinates) {
        this.coordinates = coordinates;
    }
    
    public void setCurrentLevel(double currentLevel) {
        this.currentLevel = currentLevel;
    }
    
    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }
    
    public void setLastCollected(String lastCollected) {
        this.lastCollected = lastCollected;
    }
}