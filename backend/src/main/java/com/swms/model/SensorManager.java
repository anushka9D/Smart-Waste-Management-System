package com.swms.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "sensor_managers")
public class SensorManager extends User {
    
    @Indexed(unique = true)
    private String employeeId;
    
    private String assignedZone;
    
    // Default constructor
    public SensorManager() {
        super();
    }
    
    // Constructor
    public SensorManager(String userId, String name, String email, String phone, String password, 
                        String userType, String employeeId, String assignedZone) {
        super(userId, name, email, phone, password, userType, java.time.LocalDateTime.now(), 
              java.time.LocalDateTime.now(), true);
        this.employeeId = employeeId;
        this.assignedZone = assignedZone;
    }
    
    // Getters
    public String getEmployeeId() {
        return employeeId;
    }
    
    public String getAssignedZone() {
        return assignedZone;
    }
    
    // Setters
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
    
    public void setAssignedZone(String assignedZone) {
        this.assignedZone = assignedZone;
    }
}