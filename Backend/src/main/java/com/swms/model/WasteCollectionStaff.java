package com.swms.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "waste_collection_staff")
public class WasteCollectionStaff extends User {
    
    @Indexed(unique = true)
    private String employeeId;
    
    private String routeArea;
    
    // Default constructor
    public WasteCollectionStaff() {
        super();
    }
    
    // Constructor
    public WasteCollectionStaff(String userId, String name, String email, String phone, String password, 
                               String userType, String employeeId, String routeArea) {
        super(userId, name, email, phone, password, userType, java.time.LocalDateTime.now(), 
              java.time.LocalDateTime.now(), true);
        this.employeeId = employeeId;
        this.routeArea = routeArea;
    }
    
    // Getters
    public String getEmployeeId() {
        return employeeId;
    }
    
    public String getRouteArea() {
        return routeArea;
    }
    
    // Setters
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
    
    public void setRouteArea(String routeArea) {
        this.routeArea = routeArea;
    }
}