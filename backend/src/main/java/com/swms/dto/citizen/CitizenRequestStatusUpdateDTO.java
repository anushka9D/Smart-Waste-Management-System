package com.swms.dto.citizen;

import com.swms.model.citizen.CitizenRequestStatus;
import jakarta.validation.constraints.NotNull;

public class CitizenRequestStatusUpdateDTO {
    
    @NotNull(message = "Status is required")
    private CitizenRequestStatus status;
    
    private String note;
    
    // Getters and setters
    public CitizenRequestStatus getStatus() { return status; }
    public void setStatus(CitizenRequestStatus status) { this.status = status; }
    
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}