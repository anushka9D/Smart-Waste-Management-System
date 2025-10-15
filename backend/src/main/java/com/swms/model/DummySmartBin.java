package com.swms.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "DummySmartBin")
// Changed class name from SmartBin to DummySmartBin to match filename
public class DummySmartBin {
    
    @Id
    private String binId;
    
    private String location;
    
    private GPSLocation coordinates;
    
    private double currentLevel;
    
    private double capacity;
    
    private String status;
    
    private LocalDateTime lastCollected;
    
    // Default constructor
    public DummySmartBin() {
    }
    
    // Constructor with all fields
    public DummySmartBin(String binId, String location, GPSLocation coordinates, double currentLevel, 
                   double capacity, String status, LocalDateTime lastCollected) {
        this.binId = binId;
        this.location = location;
        this.coordinates = coordinates;
        this.currentLevel = currentLevel;
        this.capacity = capacity;
        this.status = status;
        this.lastCollected = lastCollected;
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
    
    public String getStatus() {
        return status;
    }
    
    public LocalDateTime getLastCollected() {
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
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setLastCollected(LocalDateTime lastCollected) {
        this.lastCollected = lastCollected;
    }
}