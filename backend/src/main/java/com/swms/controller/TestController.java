package com.swms.controller;

import com.swms.dto.ApiResponse;
import com.swms.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController extends BaseController {

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<String>> publicEndpoint() {
        return success("This is a public endpoint - no authentication required");
    }

    @GetMapping("/protected")
    public ResponseEntity<ApiResponse<Map<String, Object>>> protectedEndpoint(
            Authentication authentication,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        Map<String, Object> data = new HashMap<>();
        data.put("message", "This is a protected endpoint");
        data.put("username", authentication.getName());
        
        // Extract additional info from JWT if available
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String userId = jwtUtil.getUserIdFromToken(token);
                String name = jwtUtil.getNameFromToken(token);
                
                data.put("userId", userId);
                data.put("name", name);
            } catch (Exception e) {
                data.put("tokenError", e.getMessage());
            }
        }
        
        return success("Protected endpoint accessed successfully", data);
    }

    @GetMapping("/user-info")
    public ResponseEntity<ApiResponse<Map<String, String>>> getUserInfo(
            Authentication authentication,
            @RequestHeader("Authorization") String authHeader) {
        
        Map<String, String> userInfo = new HashMap<>();
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            try {
                userInfo.put("username", jwtUtil.getUsernameFromToken(token));
                userInfo.put("userId", jwtUtil.getUserIdFromToken(token));
                userInfo.put("name", jwtUtil.getNameFromToken(token));
                userInfo.put("authenticatedUser", authentication.getName());
                
                return success("User information retrieved successfully", userInfo);
            } catch (Exception e) {
                return error("Error extracting user information: " + e.getMessage());
            }
        }
        
        return error("Authorization header is missing or invalid");
    }

    @GetMapping("/hello")
    public ResponseEntity<ApiResponse<String>> hello(@RequestParam(defaultValue = "World") String name) {
        return success("Hello, " + name + "!");
    }
}