package com.swms.dto.citizen;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class CitizenFeedbackDTO {
    private String id;
    
    @NotBlank
    private String citizenId;
    
    @NotBlank
    private String requestId;
    
    @NotBlank
    private String topic;
    
    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;
    
    private String comment;
    private String photoUrl;
    private LocalDateTime submittedAt;

    // Constructors
    public CitizenFeedbackDTO() {}

    public CitizenFeedbackDTO(String id, String citizenId, String requestId, String topic, Integer rating, String comment, String photoUrl, LocalDateTime submittedAt) {
        this.id = id;
        this.citizenId = citizenId;
        this.requestId = requestId;
        this.topic = topic;
        this.rating = rating;
        this.comment = comment;
        this.photoUrl = photoUrl;
        this.submittedAt = submittedAt;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(String citizenId) {
        this.citizenId = citizenId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
}