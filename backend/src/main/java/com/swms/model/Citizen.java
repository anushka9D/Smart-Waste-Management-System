package com.swms.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "citizens")
public class Citizen extends User {
    
    @Indexed(unique = true)
    private String userType;
    
    private int age;
    
    // Default constructor
    public Citizen() {
        super();
    }
    
    // Constructor
    public Citizen(String userId, String name, String email, String phone, String password, String userType, int age) {
        super(userId, name, email, phone, password, LocalDateTime.now(), LocalDateTime.now(), true);
        this.userType = userType;
        this.age = age;
    }
    
    // Getters
    public String getUserType() {
        return userType;
    }
    
    public int getAge() {
        return age;
    }
    
    // Setters
    public void setUserType(String userType) {
        this.userType = userType;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
    
    public void submitReport() {
        // Implementation can be added later
    }
}