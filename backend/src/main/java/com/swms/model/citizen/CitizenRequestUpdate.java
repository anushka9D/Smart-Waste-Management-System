package com.swms.model.citizen;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "citizen_request_updates")
public class CitizenRequestUpdate {
    
    @Id
    private String updateId;
    
    private String requestId;
    private CitizenRequestStatus status;
    private String note;
    private LocalDateTime timestamp;
    private String updatedBy; // Could be system, citizen, or staff
    
    // Default constructor
    public CitizenRequestUpdate() {
    }
    
    // Constructor
    public CitizenRequestUpdate(String requestId, CitizenRequestStatus status, String note, String updatedBy) {
        this.requestId = requestId;
        this.status = status;
        this.note = note;
        this.updatedBy = updatedBy;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters and setters
    public String getUpdateId() { return updateId; }
    public void setUpdateId(String updateId) { this.updateId = updateId; }
    
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    
    public CitizenRequestStatus getStatus() { return status; }
    public void setStatus(CitizenRequestStatus status) { this.status = status; }
    
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}