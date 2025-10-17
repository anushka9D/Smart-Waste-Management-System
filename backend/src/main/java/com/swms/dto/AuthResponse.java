package com.swms.dto;

public class AuthResponse {
    private String token;
    private String userId;
    private String name;
    private String email;
    private String phone;
    private String userType;
    private String message;

    // Default constructor
    public AuthResponse() {
    }

    // Constructor with all fields
    public AuthResponse(String token, String userId, String name, String email, String phone, String userType, String message) {
        this.token = token;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.userType = userType;
        this.message = message;
    }

    // Getters
    public String getToken() {
        return token;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getUserType() {
        return userType;
    }

    public String getMessage() {
        return message;
    }

    // Setters
    public void setToken(String token) {
        this.token = token;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}