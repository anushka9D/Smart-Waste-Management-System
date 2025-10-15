package com.swms.controller.citizen;

import com.swms.dto.ApiResponse;
import com.swms.dto.citizen.CitizenRequest;
import com.swms.model.citizen.Citizen;
import com.swms.service.citizen.CitizenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/citizens")
@CrossOrigin(originPatterns = "*", maxAge = 3600)
public class CitizenController {

    private final CitizenService citizenService;

    @Autowired
    public CitizenController(CitizenService citizenService) {
        this.citizenService = citizenService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<String>> addCitizen(@RequestBody CitizenRequest citizen) {
        try {
            String result = citizenService.saveCitizen(citizen);
            return ResponseEntity.ok(ApiResponse.success("Citizen added successfully", result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to add citizen: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Internal server error: " + e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Citizen>>> getAllCitizens() {
        try {
            List<Citizen> citizens = citizenService.getAllCitizens();
            return ResponseEntity.ok(ApiResponse.success("Citizens retrieved successfully", citizens));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to retrieve citizens: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Citizen>> getCitizenById(@PathVariable String id) {
        try {
            Optional<Citizen> citizen = citizenService.getCitizenById(id);
            if (citizen.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Citizen found", citizen.get()));
            }
            return ResponseEntity.status(404).body(ApiResponse.error("Citizen not found with id: " + id));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to retrieve citizen: " + e.getMessage()));
        }
    }
    
    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<Citizen>> getCitizenByName(@PathVariable String name) {
        try {
            Optional<Citizen> citizen = citizenService.getCitizenByName(name);
            if (citizen.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Citizen found", citizen.get()));
            }
            return ResponseEntity.status(404).body(ApiResponse.error("Citizen not found with name: " + name));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to retrieve citizen: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> updateCitizen(
            @PathVariable String id, 
            @RequestBody CitizenRequest citizen) {
        try {
            String result = citizenService.updateCitizen(id, citizen);
            return ResponseEntity.ok(ApiResponse.success(result, null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update citizen: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Internal server error: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCitizen(@PathVariable String id) {
        try {
            String result = citizenService.deleteCitizen(id);
            return ResponseEntity.ok(ApiResponse.success(result, null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to delete citizen: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Internal server error: " + e.getMessage()));
        }
    }
}