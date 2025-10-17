package com.swms.service;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryService {
    
    private final Cloudinary cloudinary;
    
    public CloudinaryService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {
        
        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        
        this.cloudinary = new Cloudinary(config);
    }
    
    /**
     * Uploads a MultipartFile to Cloudinary
     */
    public String uploadImage(MultipartFile file) throws IOException {
        validateFile(file);
        return uploadFileData(file.getBytes(), "citizen_waste_disposal_requests");
    }
    
    /**
     * Uploads a MultipartFile to Cloudinary with a specific folder
     */
    public String uploadImage(MultipartFile file, String folder) throws IOException {
        validateFile(file);
        return uploadFileData(file.getBytes(), folder);
    }
    
    private String uploadFileData(byte[] fileData, String folder) throws IOException {
        Map<String, Object> uploadOptions = createUploadOptions(folder);
        Map<String, Object> uploadResult = performCloudinaryUpload(fileData, uploadOptions);
        return extractSecureUrl(uploadResult);
    }
    
    /**
     * Isolates the Cloudinary library call that produces the warning
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> performCloudinaryUpload(byte[] fileData, Map<String, Object> options) throws IOException {
        return cloudinary.uploader().upload(fileData, options);
    }
    
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or null");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }
        
        // Validate file size (5MB limit)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("File size must be less than 5MB");
        }
    }
    
    private Map<String, Object> createUploadOptions(String folder) {
        Map<String, Object> options = new HashMap<>();
        options.put("folder", folder);
        options.put("resource_type", "image");
        return options;
    }
    
    private String extractSecureUrl(Map<String, Object> uploadResult) {
        Object secureUrl = uploadResult.get("secure_url");
        if (secureUrl instanceof String) {
            return (String) secureUrl;
        }
        throw new RuntimeException("Invalid upload result: secure_url not found");
    }
}