package com.swms.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "route_stops")
public class RouteStop {
    
    @Id
    private String stopId;
    
    private String binId;
    
    private String binLocation;
    
    private GPSLocation coordinates;
    
    private int sequenceOrder;
    
    private String status; // PENDING, IN_PROGRESS, COMPLETED
    
    private LocalDateTime collectionTime;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Default constructor
    public RouteStop() {
    }
    
    // Constructor with all fields
    public RouteStop(String stopId, String binId, String binLocation, GPSLocation coordinates, 
                    int sequenceOrder, String status, LocalDateTime collectionTime, 
                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.stopId = stopId;
        this.binId = binId;
        this.binLocation = binLocation;
        this.coordinates = coordinates;
        this.sequenceOrder = sequenceOrder;
        this.status = status;
        this.collectionTime = collectionTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters
    public String getStopId() {
        return stopId;
    }
    
    public String getBinId() {
        return binId;
    }
    
    public String getBinLocation() {
        return binLocation;
    }
    
    public GPSLocation getCoordinates() {
        return coordinates;
    }
    
    public int getSequenceOrder() {
        return sequenceOrder;
    }
    
    public String getStatus() {
        return status;
    }
    
    public LocalDateTime getCollectionTime() {
        return collectionTime;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    // Setters
    public void setStopId(String stopId) {
        this.stopId = stopId;
    }
    
    public void setBinId(String binId) {
        this.binId = binId;
    }
    
    public void setBinLocation(String binLocation) {
        this.binLocation = binLocation;
    }
    
    public void setCoordinates(GPSLocation coordinates) {
        this.coordinates = coordinates;
    }
    
    public void setSequenceOrder(int sequenceOrder) {
        this.sequenceOrder = sequenceOrder;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setCollectionTime(LocalDateTime collectionTime) {
        this.collectionTime = collectionTime;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}