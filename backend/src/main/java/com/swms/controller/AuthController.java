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

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody CitizenRequest request,
            HttpServletResponse response) {
        try {
            AuthResponse authResponse = authService.register(request);
            
            // Set JWT in cookie
            setJwtCookie(response, authResponse.getToken());
            
            return ResponseEntity.ok(ApiResponse.success("Citizen registered successfully", authResponse));
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

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletResponse response) {
        // Clear JWT cookie
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(isHttpsEnabled());
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok(ApiResponse.success("Logout successful", null));
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