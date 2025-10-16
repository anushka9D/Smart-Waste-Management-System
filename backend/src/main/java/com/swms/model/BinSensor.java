package com.swms.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "bin_sensors")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BinSensor {
    @Id
    @Indexed(unique = true)
    private String sensorId;
    private String binId;
    private String type; // working, faulty
    private String color; // red, green, blue
    private Double measurement;
    private LocalDateTime lastReading;
    private LocalDateTime createdAt;

    public void setMeasurement(Double measurement) {
        this.measurement = measurement;
    }

    public void setLastReading(LocalDateTime lastReading) {
        this.lastReading = lastReading;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setBinId(String binId) {
        this.binId = binId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }
    
    public String getSensorId() {
        return sensorId;
    }
    
    public String getBinId() {
        return binId;
    }
    
    public String getType() {
        return type;
    }
    
    public String getColor() {
        return color;
    }
    
    public Double getMeasurement() {
        return measurement;
    }
    
    public LocalDateTime getLastReading() {
        return lastReading;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}