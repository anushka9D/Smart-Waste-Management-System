package com.swms.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "city_authorities")
public class CityAuthority extends User {
    
    @Indexed(unique = true)
    private String employeeId;
    
    private String department;
    
    private String userType;
    
    // Default constructor
    public CityAuthority() {
        super();
    }
    
    // Constructor
    public CityAuthority(String userId, String name, String email, String phone, String password, 
                        String employeeId, String department, String userType) {
        super(userId, name, email, phone, password, java.time.LocalDateTime.now(), 
              java.time.LocalDateTime.now(), true);
        this.employeeId = employeeId;
        this.department = department;
        this.userType = userType;
    }
    
    // Getters
    public String getEmployeeId() {
        return employeeId;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public String getUserType() {
        return userType;
    }
    
    // Setters
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public void setUserType(String userType) {
        this.userType = userType;
    }
}