package com.swms.model.citizen;

import com.swms.model.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "citizens")
public class Citizen extends User {
    
    private int age;
    
    // Default constructor
    public Citizen() {
        super();
    }
    
    // Constructor
    public Citizen(String userId, String name, String email, String phone, String password, 
                   String userType, int age) {
        super(userId, name, email, phone, password, userType, LocalDateTime.now(), 
              LocalDateTime.now(), true);
        this.age = age;
    }
    
    // Getters
    public int getAge() {
        return age;
    }
    
    // Setters
    public void setAge(int age) {
        this.age = age;
    }
}