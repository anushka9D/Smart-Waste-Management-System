package com.swms.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "trucks")
public class Truck {
    
    @Id
    private String truckId;
    
    private String registrationNumber;
    
    private double capacity; // in liters or cubic meters
    
    private String currentStatus; // AVAILABLE, IN_USE, MAINTENANCE, OUT_OF_SERVICE
    
    private String assignedDriverId;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Default constructor
    public Truck() {
    }
    
    // Constructor with all fields
    public Truck(String truckId, String registrationNumber, double capacity, String currentStatus,
                String assignedDriverId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.truckId = truckId;
        this.registrationNumber = registrationNumber;
        this.capacity = capacity;
        this.currentStatus = currentStatus;
        this.assignedDriverId = assignedDriverId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters
    public String getTruckId() {
        return truckId;
    }
    
    public String getRegistrationNumber() {
        return registrationNumber;
    }
    
    public double getCapacity() {
        return capacity;
    }
    
    public String getCurrentStatus() {
        return currentStatus;
    }
    
    public String getAssignedDriverId() {
        return assignedDriverId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    // Setters
    public void setTruckId(String truckId) {
        this.truckId = truckId;
    }
    
    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }
    
    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }
    
    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }
    
    public void setAssignedDriverId(String assignedDriverId) {
        this.assignedDriverId = assignedDriverId;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}