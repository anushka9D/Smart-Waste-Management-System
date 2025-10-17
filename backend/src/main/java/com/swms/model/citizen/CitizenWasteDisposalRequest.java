package com.swms.model.citizen;

import com.swms.model.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "citizen_waste_disposal_requests")
public class CitizenWasteDisposalRequest {
    
    @Id
    private String requestId;
    
    private String citizenId;
    private String citizenName;
    private String citizenEmail;
    
    private CitizenRequestCategory category;
    private String customCategory; // Only used when category is OTHER
    
    private String description;
    private String binId; // Optional - can be null
    
    // Location using existing GPSLocation
    private GPSLocation coordinates;
    private String address;
    
    // Photo evidence
    private String photoUrl; // Cloudinary URL
    
    private CitizenRequestStatus status;
    private LocalDateTime submittedAt;
    private LocalDateTime updatedAt;
    
    // Default constructor
    public CitizenWasteDisposalRequest() {
    }
    
    // Constructor
    public CitizenWasteDisposalRequest(String requestId, String citizenId, String citizenName, 
                               String citizenEmail, CitizenRequestCategory category, String description, 
                               GPSLocation coordinates, String address) {
        this.requestId = requestId;
        this.citizenId = citizenId;
        this.citizenName = citizenName;
        this.citizenEmail = citizenEmail;
        this.category = category;
        this.description = description;
        this.coordinates = coordinates;
        this.address = address;
        this.status = CitizenRequestStatus.SUBMITTED;
        this.submittedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and setters
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    
    public String getCitizenId() { return citizenId; }
    public void setCitizenId(String citizenId) { this.citizenId = citizenId; }
    
    public String getCitizenName() { return citizenName; }
    public void setCitizenName(String citizenName) { this.citizenName = citizenName; }
    
    public String getCitizenEmail() { return citizenEmail; }
    public void setCitizenEmail(String citizenEmail) { this.citizenEmail = citizenEmail; }
    
    public CitizenRequestCategory getCategory() { return category; }
    public void setCategory(CitizenRequestCategory category) { this.category = category; }
    
    public String getCustomCategory() { return customCategory; }
    public void setCustomCategory(String customCategory) { this.customCategory = customCategory; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getBinId() { return binId; }
    public void setBinId(String binId) { this.binId = binId; }
    
    public GPSLocation getCoordinates() { return coordinates; }
    public void setCoordinates(GPSLocation coordinates) { this.coordinates = coordinates; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    
    public CitizenRequestStatus getStatus() { return status; }
    public void setStatus(CitizenRequestStatus status) { this.status = status; }
    
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}