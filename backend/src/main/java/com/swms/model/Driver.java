package com.swms.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "drivers")
public class Driver extends User {
    
    @Indexed(unique = true)
    private String licenseNumber;
    
    private String vehicleType;
    
    private boolean availability = true; // true = available, false = unavailable
    
    private String currentRouteId; // null if not assigned to any route
    
    private LocalDateTime lastUpdated;
    
    // Default constructor
    public Driver() {
        super();
    }
    
    // Constructor
    public Driver(String userId, String name, String email, String phone, String password, 
                 String userType, String licenseNumber, String vehicleType) {
        super(userId, name, email, phone, password, userType, java.time.LocalDateTime.now(), 
              java.time.LocalDateTime.now(), true);
        this.licenseNumber = licenseNumber;
        this.vehicleType = vehicleType;
        this.availability = true;
        this.lastUpdated = java.time.LocalDateTime.now();
    }
    
    // Getters
    public String getLicenseNumber() {
        return licenseNumber;
    }
    
    public String getVehicleType() {
        return vehicleType;
    }
    
    public boolean isAvailability() {
        return availability;
    }
    
    public String getCurrentRouteId() {
        return currentRouteId;
    }
    
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    
    // Setters
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }
    
    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }
    
    public void setAvailability(boolean availability) {
        this.availability = availability;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public void setCurrentRouteId(String currentRouteId) {
        this.currentRouteId = currentRouteId;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}