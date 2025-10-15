package com.swms.dto.citizen;

import com.swms.model.citizen.CitizenRequestCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CitizenWasteDisposalMultipartDTO {
    @NotNull(message = "Category is required")
    private CitizenRequestCategory category; // Changed from String to enum
    
    private String binId;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotBlank(message = "Address is required")
    private String address;
    
    @NotNull(message = "Latitude is required")
    private Double latitude;
    
    @NotNull(message = "Longitude is required")
    private Double longitude;
    
    private MultipartFile photo;
    
}