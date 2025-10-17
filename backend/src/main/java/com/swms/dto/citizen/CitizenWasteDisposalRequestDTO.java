package com.swms.dto.citizen;

import com.swms.model.citizen.CitizenRequestCategory;
import com.swms.model.GPSLocation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CitizenWasteDisposalRequestDTO {
    
    @NotNull(message = "Category is required")
    private CitizenRequestCategory category;
    
    private String customCategory; // Only required when category is OTHER
    
    @NotBlank(message = "Description is required")
    private String description;
    
    private String binId; // Optional
    
    @NotNull(message = "Location coordinates are required")
    private GPSLocation coordinates;
    
    @NotBlank(message = "Address is required")
    private String address;
    
    private String photoBase64; // Base64 encoded image
    
    // Getters and setters
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
    
    public String getPhotoBase64() { return photoBase64; }
    public void setPhotoBase64(String photoBase64) { this.photoBase64 = photoBase64; }
}