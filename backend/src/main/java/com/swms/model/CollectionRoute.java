package com.swms.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "collection_routes")
public class CollectionRoute {
    
    @Id
    private String routeId;
    
    private LocalDateTime date;
    
    private String status; // PLANNED, IN_PROGRESS, COMPLETED, CANCELLED
    
    private String assignedTruckId;
    
    private String assignedDriverId;
    
    private List<String> assignedStaffIds;
    
    private double totalDistance; // in kilometers
    
    private int estimatedTime; // in minutes
    
    private List<String> stopIds; // References to RouteStop entities
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Default constructor
    public CollectionRoute() {
    }
    
    // Constructor with all fields
    public CollectionRoute(String routeId, LocalDateTime date, String status, String assignedTruckId,
                          String assignedDriverId, List<String> assignedStaffIds, double totalDistance,
                          int estimatedTime, List<String> stopIds, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.routeId = routeId;
        this.date = date;
        this.status = status;
        this.assignedTruckId = assignedTruckId;
        this.assignedDriverId = assignedDriverId;
        this.assignedStaffIds = assignedStaffIds;
        this.totalDistance = totalDistance;
        this.estimatedTime = estimatedTime;
        this.stopIds = stopIds;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters
    public String getRouteId() {
        return routeId;
    }
    
    public LocalDateTime getDate() {
        return date;
    }
    
    public String getStatus() {
        return status;
    }
    
    public String getAssignedTruckId() {
        return assignedTruckId;
    }
    
    public String getAssignedDriverId() {
        return assignedDriverId;
    }
    
    public List<String> getAssignedStaffIds() {
        return assignedStaffIds;
    }
    
    public double getTotalDistance() {
        return totalDistance;
    }
    
    public int getEstimatedTime() {
        return estimatedTime;
    }
    
    public List<String> getStopIds() {
        return stopIds;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    // Setters
    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }
    
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setAssignedTruckId(String assignedTruckId) {
        this.assignedTruckId = assignedTruckId;
    }
    
    public void setAssignedDriverId(String assignedDriverId) {
        this.assignedDriverId = assignedDriverId;
    }
    
    public void setAssignedStaffIds(List<String> assignedStaffIds) {
        this.assignedStaffIds = assignedStaffIds;
    }
    
    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }
    
    public void setEstimatedTime(int estimatedTime) {
        this.estimatedTime = estimatedTime;
    }
    
    public void setStopIds(List<String> stopIds) {
        this.stopIds = stopIds;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}