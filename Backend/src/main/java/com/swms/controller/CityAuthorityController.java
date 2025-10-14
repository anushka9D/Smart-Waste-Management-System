package com.swms.controller;

import com.swms.dto.ApiResponse;
import com.swms.dto.CityAuthorityRequest;
import com.swms.model.CityAuthority;
import com.swms.service.CityAuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/city-authorities")
@CrossOrigin(originPatterns = "*", maxAge = 3600)
public class CityAuthorityController {

    private final CityAuthorityService cityAuthorityService;

    @Autowired
    public CityAuthorityController(CityAuthorityService cityAuthorityService) {
        this.cityAuthorityService = cityAuthorityService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<String>> addCityAuthority(@RequestBody CityAuthorityRequest request) {
        try {
            String result = cityAuthorityService.saveCityAuthority(request);
            return ResponseEntity.ok(ApiResponse.success("City Authority added successfully", result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to add city authority: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Internal server error: " + e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<CityAuthority>>> getAllCityAuthorities() {
        try {
            List<CityAuthority> cityAuthorities = cityAuthorityService.getAllCityAuthorities();
            return ResponseEntity.ok(ApiResponse.success("City Authorities retrieved successfully", cityAuthorities));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to retrieve city authorities: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CityAuthority>> getCityAuthorityById(@PathVariable String id) {
        try {
            Optional<CityAuthority> cityAuthority = cityAuthorityService.getCityAuthorityById(id);
            if (cityAuthority.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("City Authority found", cityAuthority.get()));
            }
            return ResponseEntity.status(404).body(ApiResponse.error("City Authority not found with id: " + id));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to retrieve city authority: " + e.getMessage()));
        }
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<CityAuthority>> getCityAuthorityByEmail(@PathVariable String email) {
        try {
            Optional<CityAuthority> cityAuthority = cityAuthorityService.getCityAuthorityByEmail(email);
            if (cityAuthority.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("City Authority found", cityAuthority.get()));
            }
            return ResponseEntity.status(404).body(ApiResponse.error("City Authority not found with email: " + email));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to retrieve city authority: " + e.getMessage()));
        }
    }
    
    @GetMapping("/employee-id/{employeeId}")
    public ResponseEntity<ApiResponse<CityAuthority>> getCityAuthorityByEmployeeId(@PathVariable String employeeId) {
        try {
            Optional<CityAuthority> cityAuthority = cityAuthorityService.getCityAuthorityByEmployeeId(employeeId);
            if (cityAuthority.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("City Authority found", cityAuthority.get()));
            }
            return ResponseEntity.status(404).body(ApiResponse.error("City Authority not found with employee ID: " + employeeId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to retrieve city authority: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> updateCityAuthority(
            @PathVariable String id, 
            @RequestBody CityAuthorityRequest request) {
        try {
            String result = cityAuthorityService.updateCityAuthority(id, request);
            return ResponseEntity.ok(ApiResponse.success(result, null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update city authority: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Internal server error: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCityAuthority(@PathVariable String id) {
        try {
            String result = cityAuthorityService.deleteCityAuthority(id);
            return ResponseEntity.ok(ApiResponse.success(result, null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to delete city authority: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Internal server error: " + e.getMessage()));
        }
    }
}