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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController extends BaseController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${cookie.expiration}")
    private int cookieExpiration;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response) {
        try {
            AuthResponse authResponse = authService.register(request);
            
            // Set JWT in cookie
            setJwtCookie(response, authResponse.getToken());
            
            return success("User registered successfully", authResponse);
        } catch (Exception e) {
            return error(e.getMessage());
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
            
            return success("Login successful", authResponse);
        } catch (Exception e) {
            return error("Invalid username or password");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletResponse response) {
        // Clear JWT cookie
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set to true in production with HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return success("Logout successful", null);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getCurrentUser(Authentication authentication) {
        try {
            String username = authentication.getName();
            UserInfoResponse userInfo = authService.getUserInfo(username);
            return success(userInfo);
        } catch (Exception e) {
            return error("User not found");
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<Boolean>> validateToken(@RequestHeader("Authorization") String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                String jwt = token.substring(7);
                String username = jwtUtil.getUsernameFromToken(jwt);
                boolean isValid = jwtUtil.validateToken(jwt, username);
                return success(isValid);
            }
            return success(false);
        } catch (Exception e) {
            return success(false);
        }
    }

    private void setJwtCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set to true in production with HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(cookieExpiration);
        response.addCookie(cookie);
    }
}