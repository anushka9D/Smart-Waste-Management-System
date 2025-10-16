package com.swms.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "alerts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alert {
    @Id
    private String alertId;
    private String binId;
    private String location;
    private String alertTitle;
    private String message;
    private LocalDateTime createdAt;
    private String isReviewed;

    public void setIsReviewed(String isReviewed) {
        this.isReviewed = isReviewed;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setAlertTitle(String alertTitle) {
        this.alertTitle = alertTitle;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setBinId(String binId) {
        this.binId = binId;
    }

    public String getIsReviewed() {
        return isReviewed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getMessage() {
        return message;
    }

    public String getAlertTitle() {
        return alertTitle;
    }

    public String getLocation() {
        return location;
    }

    public String getBinId() {
        return binId;
    }

    public String getAlertId() {
        return alertId;
    }
}