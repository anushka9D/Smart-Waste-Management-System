package com.swms.controller;

import com.swms.dto.ApiResponse;
import com.swms.dto.SensorManagerRequest;
import com.swms.model.SensorManager;
import com.swms.service.SensorManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sensor-managers")
@CrossOrigin(originPatterns = "*", maxAge = 3600)
public class SensorManagerController {

    private final SensorManagerService sensorManagerService;

    @Autowired
    public SensorManagerController(SensorManagerService sensorManagerService) {
        this.sensorManagerService = sensorManagerService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<String>> addSensorManager(@RequestBody SensorManagerRequest request) {
        try {
            String result = sensorManagerService.saveSensorManager(request);
            return ResponseEntity.ok(ApiResponse.success("Sensor Manager added successfully", result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to add sensor manager: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Internal server error: " + e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<SensorManager>>> getAllSensorManagers() {
        try {
            List<SensorManager> sensorManagers = sensorManagerService.getAllSensorManagers();
            return ResponseEntity.ok(ApiResponse.success("Sensor Managers retrieved successfully", sensorManagers));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to retrieve sensor managers: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SensorManager>> getSensorManagerById(@PathVariable String id) {
        try {
            Optional<SensorManager> sensorManager = sensorManagerService.getSensorManagerById(id);
            if (sensorManager.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Sensor Manager found", sensorManager.get()));
            }
            return ResponseEntity.status(404).body(ApiResponse.error("Sensor Manager not found with id: " + id));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to retrieve sensor manager: " + e.getMessage()));
        }
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<SensorManager>> getSensorManagerByEmail(@PathVariable String email) {
        try {
            Optional<SensorManager> sensorManager = sensorManagerService.getSensorManagerByEmail(email);
            if (sensorManager.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Sensor Manager found", sensorManager.get()));
            }
            return ResponseEntity.status(404).body(ApiResponse.error("Sensor Manager not found with email: " + email));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to retrieve sensor manager: " + e.getMessage()));
        }
    }
    
    @GetMapping("/employee-id/{employeeId}")
    public ResponseEntity<ApiResponse<SensorManager>> getSensorManagerByEmployeeId(@PathVariable String employeeId) {
        try {
            Optional<SensorManager> sensorManager = sensorManagerService.getSensorManagerByEmployeeId(employeeId);
            if (sensorManager.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Sensor Manager found", sensorManager.get()));
            }
            return ResponseEntity.status(404).body(ApiResponse.error("Sensor Manager not found with employee ID: " + employeeId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to retrieve sensor manager: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> updateSensorManager(
            @PathVariable String id, 
            @RequestBody SensorManagerRequest request) {
        try {
            String result = sensorManagerService.updateSensorManager(id, request);
            return ResponseEntity.ok(ApiResponse.success(result, null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update sensor manager: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Internal server error: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteSensorManager(@PathVariable String id) {
        try {
            String result = sensorManagerService.deleteSensorManager(id);
            return ResponseEntity.ok(ApiResponse.success(result, null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to delete sensor manager: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Internal server error: " + e.getMessage()));
        }
    }
}