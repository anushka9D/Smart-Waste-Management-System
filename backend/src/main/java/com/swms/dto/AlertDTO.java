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
public class AlertDTO {
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

    public void setLocation(String location) {
        this.location = location;
    }

    public void setBinId(String binId) {
        this.binId = binId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }
}