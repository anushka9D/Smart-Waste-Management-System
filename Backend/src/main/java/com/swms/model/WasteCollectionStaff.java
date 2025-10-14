package com.swms.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "waste_collection_staff")
public class WasteCollectionStaff extends User {
    
    @Indexed(unique = true)
    private String employeeId;
    
    private String routeArea;
    
    private boolean availability = true; // true = available, false = unavailable
    
    private String currentRouteId; // null if not assigned to any route
    
    private LocalDateTime lastUpdated;
    
    // Default constructor
    public WasteCollectionStaff() {
        super();
    }
    
    // Constructor
    public WasteCollectionStaff(String userId, String name, String email, String phone, String password, 
                               String employeeId, String routeArea) {
        super(userId, name, email, phone, password, java.time.LocalDateTime.now(), 
              java.time.LocalDateTime.now(), true);
        this.employeeId = employeeId;
        this.routeArea = routeArea;
        this.availability = true;
        this.lastUpdated = java.time.LocalDateTime.now();
    }
    
    // Getters
    public String getEmployeeId() {
        return employeeId;
    }
    
    public String getRouteArea() {
        return routeArea;
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
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
    
    public void setRouteArea(String routeArea) {
        this.routeArea = routeArea;
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