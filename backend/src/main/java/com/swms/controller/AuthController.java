package com.swms.controller;

import com.swms.dto.*;
import com.swms.security.JwtUtil;
import com.swms.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(originPatterns = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${cookie.expiration}")
    private int cookieExpiration;

    // Citizen registration
    @PostMapping("/register/citizen")
    public ResponseEntity<ApiResponse<AuthResponse>> registerCitizen(
            @Valid @RequestBody CitizenRequest request,
            HttpServletResponse response) {
        try {
            AuthResponse authResponse = authService.registerCitizen(request);
            
            // Set JWT in cookie
            setJwtCookie(response, authResponse.getToken());
            
            return ResponseEntity.ok(ApiResponse.success("Citizen registered successfully", authResponse));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Internal server error during registration"));
        }
    }
    
    // City Authority registration
    @PostMapping("/register/city-authority")
    public ResponseEntity<ApiResponse<AuthResponse>> registerCityAuthority(
            @Valid @RequestBody CityAuthorityRequest request,
            HttpServletResponse response) {
        try {
            AuthResponse authResponse = authService.registerCityAuthority(request);
            
            // Set JWT in cookie
            setJwtCookie(response, authResponse.getToken());
            
            return ResponseEntity.ok(ApiResponse.success("City Authority registered successfully", authResponse));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Internal server error during registration"));
        }
    }
    
    // Driver registration
    @PostMapping("/register/driver")
    public ResponseEntity<ApiResponse<AuthResponse>> registerDriver(
            @Valid @RequestBody DriverRequest request,
            HttpServletResponse response) {
        try {
            AuthResponse authResponse = authService.registerDriver(request);
            
            // Set JWT in cookie
            setJwtCookie(response, authResponse.getToken());
            
            return ResponseEntity.ok(ApiResponse.success("Driver registered successfully", authResponse));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Internal server error during registration"));
        }
    }
    
    // Waste Collection Staff registration
    @PostMapping("/register/waste-collection-staff")
    public ResponseEntity<ApiResponse<AuthResponse>> registerWasteCollectionStaff(
            @Valid @RequestBody WasteCollectionStaffRequest request,
            HttpServletResponse response) {
        try {
            AuthResponse authResponse = authService.registerWasteCollectionStaff(request);
            
            // Set JWT in cookie
            setJwtCookie(response, authResponse.getToken());
            
            return ResponseEntity.ok(ApiResponse.success("Waste Collection Staff registered successfully", authResponse));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Internal server error during registration"));
        }
    }

    @PostMapping("/register/sensor-manager")
    public ResponseEntity<ApiResponse<AuthResponse>> registerSensorManager(
            @Valid @RequestBody SensorManagerRequest request,
            HttpServletResponse response) {
        try {
            AuthResponse authResponse = authService.registerSensorManager(request);
            
            // Set JWT in cookie
            setJwtCookie(response, authResponse.getToken());
            
            return ResponseEntity.ok(ApiResponse.success("Sensor Manager registered successfully", authResponse));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Internal server error during registration"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        try {
            AuthResponse authResponse = authService.login(request);
            
            // Set JWT in cookie
            setJwtCookie(response, authResponse.getToken());
            
            return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid email or password"));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<Boolean>> validateToken(@RequestHeader("Authorization") String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                String jwt = token.substring(7);
                String email = jwtUtil.getEmailFromToken(jwt);
                boolean isValid = jwtUtil.validateToken(jwt, email);
                return ResponseEntity.ok(ApiResponse.success(isValid));
            }
            return ResponseEntity.ok(ApiResponse.success(false));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.success(false));
        }
    }

    private void setJwtCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(isHttpsEnabled());
        cookie.setPath("/");
        cookie.setMaxAge(cookieExpiration);
        response.addCookie(cookie);
    }
    
    // Helper method to determine if HTTPS should be enabled
    private boolean isHttpsEnabled() {
        // In production, you would check environment variables or properties
        // For now, returning false to maintain current behavior
        return false;
    }
}