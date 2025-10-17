package com.swms.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "smart_bins")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmartBin {
    @Id
    @Indexed(unique = true)
    private String binId;
    private String location;
    private GPSLocation coordinates;
    private Double currentLevel;
    private Double capacity;
    private String wasteType;
    private String status; // empty, half_full, full
    private LocalDateTime lastCollected;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdated;

    public String getLocation() {
        return location;
    }

    public String getBinId() {
        return binId;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getLastCollected() {
        return lastCollected;
    }

    public Double getCapacity() {
        return capacity;
    }

    public String getWasteType() {
    return wasteType;
    }

    public Double getCurrentLevel() {
        return currentLevel;
    }

    public GPSLocation getCoordinates() {
        return coordinates;
    }

    public void setLastCollected(LocalDateTime lastCollected) {
        this.lastCollected = lastCollected;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCurrentLevel(Double currentLevel) {
        this.currentLevel = currentLevel;
    }

    public void setBinId(String binId) {
        this.binId = binId;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCoordinates(GPSLocation coordinates) {
        this.coordinates = coordinates;
    }

    public void setCapacity(Double capacity) {
        this.capacity = capacity;
    }

    public void setWasteType(String wasteType) {
    this.wasteType = wasteType;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}