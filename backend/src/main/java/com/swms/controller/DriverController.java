package com.swms.controller;

import com.swms.dto.ApiResponse;
import com.swms.dto.DriverRequest;
import com.swms.model.Driver;
import com.swms.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/drivers")
@CrossOrigin(originPatterns = "*", maxAge = 3600)
public class DriverController {

    private final DriverService driverService;

    @Autowired
    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<String>> addDriver(@RequestBody DriverRequest request) {
        try {
            String result = driverService.saveDriver(request);
            return ResponseEntity.ok(ApiResponse.success("Driver added successfully", result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to add driver: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Internal server error: " + e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Driver>>> getAllDrivers() {
        try {
            List<Driver> drivers = driverService.getAllDrivers();
            return ResponseEntity.ok(ApiResponse.success("Drivers retrieved successfully", drivers));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to retrieve drivers: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Driver>> getDriverById(@PathVariable String id) {
        try {
            Optional<Driver> driver = driverService.getDriverById(id);
            if (driver.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Driver found", driver.get()));
            }
            return ResponseEntity.status(404).body(ApiResponse.error("Driver not found with id: " + id));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to retrieve driver: " + e.getMessage()));
        }
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<Driver>> getDriverByEmail(@PathVariable String email) {
        try {
            Optional<Driver> driver = driverService.getDriverByEmail(email);
            if (driver.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Driver found", driver.get()));
            }
            return ResponseEntity.status(404).body(ApiResponse.error("Driver not found with email: " + email));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to retrieve driver: " + e.getMessage()));
        }
    }
    
    @GetMapping("/license-number/{licenseNumber}")
    public ResponseEntity<ApiResponse<Driver>> getDriverByLicenseNumber(@PathVariable String licenseNumber) {
        try {
            Optional<Driver> driver = driverService.getDriverByLicenseNumber(licenseNumber);
            if (driver.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Driver found", driver.get()));
            }
            return ResponseEntity.status(404).body(ApiResponse.error("Driver not found with license number: " + licenseNumber));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to retrieve driver: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> updateDriver(
            @PathVariable String id, 
            @RequestBody DriverRequest request) {
        try {
            String result = driverService.updateDriver(id, request);
            return ResponseEntity.ok(ApiResponse.success(result, null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update driver: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Internal server error: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteDriver(@PathVariable String id) {
        try {
            String result = driverService.deleteDriver(id);
            return ResponseEntity.ok(ApiResponse.success(result, null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to delete driver: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Internal server error: " + e.getMessage()));
        }
    }
}