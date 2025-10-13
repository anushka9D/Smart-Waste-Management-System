package com.swms.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "drivers")
public class Driver extends User {
    
    @Indexed(unique = true)
    private String licenseNumber;
    
    private String vehicleType;
    
    // Default constructor
    public Driver() {
        super();
    }
    
    // Constructor
    public Driver(String userId, String name, String email, String phone, String password, 
                 String licenseNumber, String vehicleType) {
        super(userId, name, email, phone, password, java.time.LocalDateTime.now(), 
              java.time.LocalDateTime.now(), true);
        this.licenseNumber = licenseNumber;
        this.vehicleType = vehicleType;
    }
    
    // Getters
    public String getLicenseNumber() {
        return licenseNumber;
    }
    
    public String getVehicleType() {
        return vehicleType;
    }
    
    // Setters
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }
    
    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }
}