package com.wasterouting.controller;

import com.wasterouting.dto.ApiResponse;
import com.wasterouting.service.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/firebase")
public class FirebaseTestController extends BaseController {

    private final FirebaseService firebaseService;

    @Autowired
    public FirebaseTestController(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    @GetMapping("/test")
    public ResponseEntity<ApiResponse<Map<String, String>>> testFirebaseConnection() {
        try {
            String result = firebaseService.testConnection();
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", result);
            return success("Firebase connection test completed", response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());
            return error("Firebase connection failed: " + e.getMessage());
        }
    }
}
