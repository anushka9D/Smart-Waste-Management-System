package com.swms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmartBinDTO {
    private String binId;
    private String location;
    private GPSLocationDTO coordinates;
    private Double currentLevel;
    private Double capacity;
    private String status;
    private LocalDateTime lastCollected;
    private String binColor;

    public void setBinColor(String binColor) {
        this.binColor = binColor;
    }

    public void setLastCollected(LocalDateTime lastCollected) {
        this.lastCollected = lastCollected;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCapacity(Double capacity) {
        this.capacity = capacity;
    }

    public void setCurrentLevel(Double currentLevel) {
        this.currentLevel = currentLevel;
    }

    public void setCoordinates(GPSLocationDTO coordinates) {
        this.coordinates = coordinates;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setBinId(String binId) {
        this.binId = binId;
    }
}