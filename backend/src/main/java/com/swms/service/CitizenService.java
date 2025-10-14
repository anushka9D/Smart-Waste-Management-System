package com.swms.service;

import com.swms.dto.CitizenRequest;
import com.swms.model.Citizen;
import com.swms.repository.CitizenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CitizenService {

    @Autowired
    private CitizenRepository citizenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String saveCitizen(CitizenRequest request) {
        // Check if email already exists
        if (citizenRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }

        Citizen citizen = new Citizen();
        citizen.setName(request.getName());
        citizen.setEmail(request.getEmail());
        citizen.setPhone(request.getPhone());
        citizen.setPassword(passwordEncoder.encode(request.getPassword()));
        citizen.setUserType("CITIZEN");
        citizen.setAge(request.getAge());
        citizen.setCreatedAt(LocalDateTime.now());
        citizen.setUpdatedAt(LocalDateTime.now());
        citizen.setEnabled(true);
        
        Citizen savedCitizen = citizenRepository.save(citizen);
        
        return "Citizen created with ID: " + savedCitizen.getUserId();
    }
    
    public List<Citizen> getAllCitizens() {
        return citizenRepository.findAll();
    }
    
    public Optional<Citizen> getCitizenById(String id) {
        return citizenRepository.findById(id);
    }
    
    public Optional<Citizen> getCitizenByName(String name) {
        return citizenRepository.findByName(name);
    }
    
    public Optional<Citizen> getCitizenByEmail(String email) {
        return citizenRepository.findByEmail(email);
    }
    
    public String updateCitizen(String id, CitizenRequest request) {
        Optional<Citizen> existingCitizen = citizenRepository.findById(id);
        
        if (existingCitizen.isPresent()) {
            Citizen citizen = existingCitizen.get();
            citizen.setName(request.getName());
            citizen.setEmail(request.getEmail());
            citizen.setPhone(request.getPhone());
            citizen.setAge(request.getAge());
            citizen.setUpdatedAt(LocalDateTime.now());
            
            citizenRepository.save(citizen);
            return "Citizen updated successfully";
        }
        
        throw new RuntimeException("Citizen not found with id: " + id);
    }
    
    public String deleteCitizen(String id) {
        if (citizenRepository.existsById(id)) {
            citizenRepository.deleteById(id);
            return "Citizen deleted successfully";
        }
        
        throw new RuntimeException("Citizen not found with id: " + id);
    }
}