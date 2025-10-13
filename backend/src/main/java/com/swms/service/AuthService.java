package com.swms.service;

import com.swms.dto.AuthResponse;
import com.swms.dto.LoginRequest;
import com.swms.dto.CitizenRequest;
import com.swms.model.Citizen;
import com.swms.repository.CitizenRepository;
import com.swms.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    @Autowired
    private CitizenRepository citizenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthResponse register(CitizenRequest request) {
        // Check if email already exists
        if (citizenRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }

        // Create new citizen
        Citizen citizen = new Citizen();
        citizen.setName(request.getName());
        citizen.setEmail(request.getEmail()); // This is now directly on Citizen
        citizen.setPhone(request.getPhone());
        citizen.setPassword(passwordEncoder.encode(request.getPassword()));
        citizen.setUserType(request.getUserType());
        citizen.setAge(request.getAge());
        citizen.setCreatedAt(LocalDateTime.now());
        citizen.setUpdatedAt(LocalDateTime.now());
        citizen.setEnabled(true);

        Citizen savedCitizen = citizenRepository.save(citizen);

        // Generate JWT token
        String token = jwtUtil.generateToken(
            savedCitizen.getUserId(), 
            savedCitizen.getName(), 
            savedCitizen.getEmail()
        );

        return new AuthResponse(
                token,
                savedCitizen.getUserId(),
                savedCitizen.getName(),
                savedCitizen.getEmail(),
                savedCitizen.getPhone(),
                savedCitizen.getUserType(),
                "Citizen registered successfully"
        );
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Citizen citizen = citizenRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Citizen not found"));

        String token = jwtUtil.generateToken(
            citizen.getUserId(), 
            citizen.getName(), 
            citizen.getEmail()
        );

        return new AuthResponse(
                token,
                citizen.getUserId(),
                citizen.getName(),
                citizen.getEmail(),
                citizen.getPhone(),
                citizen.getUserType(),
                "Login successful"
        );
    }
}