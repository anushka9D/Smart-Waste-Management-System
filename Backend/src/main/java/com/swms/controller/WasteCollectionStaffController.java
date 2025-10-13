package com.swms.controller;

import com.swms.dto.ApiResponse;
import com.swms.dto.WasteCollectionStaffRequest;
import com.swms.model.WasteCollectionStaff;
import com.swms.service.WasteCollectionStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/waste-collection-staff")
@CrossOrigin(originPatterns = "*", maxAge = 3600)
public class WasteCollectionStaffController {

    private final WasteCollectionStaffService wasteCollectionStaffService;

    @Autowired
    public WasteCollectionStaffController(WasteCollectionStaffService wasteCollectionStaffService) {
        this.wasteCollectionStaffService = wasteCollectionStaffService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<String>> addWasteCollectionStaff(@RequestBody WasteCollectionStaffRequest request) {
        try {
            String result = wasteCollectionStaffService.saveWasteCollectionStaff(request);
            return ResponseEntity.ok(ApiResponse.success("Waste Collection Staff added successfully", result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to add waste collection staff: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Internal server error: " + e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<WasteCollectionStaff>>> getAllWasteCollectionStaff() {
        try {
            List<WasteCollectionStaff> staff = wasteCollectionStaffService.getAllWasteCollectionStaff();
            return ResponseEntity.ok(ApiResponse.success("Waste Collection Staff retrieved successfully", staff));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to retrieve waste collection staff: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WasteCollectionStaff>> getWasteCollectionStaffById(@PathVariable String id) {
        try {
            Optional<WasteCollectionStaff> staff = wasteCollectionStaffService.getWasteCollectionStaffById(id);
            if (staff.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Waste Collection Staff found", staff.get()));
            }
            return ResponseEntity.status(404).body(ApiResponse.error("Waste Collection Staff not found with id: " + id));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to retrieve waste collection staff: " + e.getMessage()));
        }
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<WasteCollectionStaff>> getWasteCollectionStaffByEmail(@PathVariable String email) {
        try {
            Optional<WasteCollectionStaff> staff = wasteCollectionStaffService.getWasteCollectionStaffByEmail(email);
            if (staff.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Waste Collection Staff found", staff.get()));
            }
            return ResponseEntity.status(404).body(ApiResponse.error("Waste Collection Staff not found with email: " + email));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to retrieve waste collection staff: " + e.getMessage()));
        }
    }
    
    @GetMapping("/employee-id/{employeeId}")
    public ResponseEntity<ApiResponse<WasteCollectionStaff>> getWasteCollectionStaffByEmployeeId(@PathVariable String employeeId) {
        try {
            Optional<WasteCollectionStaff> staff = wasteCollectionStaffService.getWasteCollectionStaffByEmployeeId(employeeId);
            if (staff.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Waste Collection Staff found", staff.get()));
            }
            return ResponseEntity.status(404).body(ApiResponse.error("Waste Collection Staff not found with employee ID: " + employeeId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to retrieve waste collection staff: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> updateWasteCollectionStaff(
            @PathVariable String id, 
            @RequestBody WasteCollectionStaffRequest request) {
        try {
            String result = wasteCollectionStaffService.updateWasteCollectionStaff(id, request);
            return ResponseEntity.ok(ApiResponse.success(result, null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update waste collection staff: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Internal server error: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteWasteCollectionStaff(@PathVariable String id) {
        try {
            String result = wasteCollectionStaffService.deleteWasteCollectionStaff(id);
            return ResponseEntity.ok(ApiResponse.success(result, null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to delete waste collection staff: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Internal server error: " + e.getMessage()));
        }
    }
}