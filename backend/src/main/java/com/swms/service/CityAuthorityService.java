package com.swms.service;

import com.swms.dto.CityAuthorityRequest;
import com.swms.model.CityAuthority;
import com.swms.repository.CityAuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CityAuthorityService {

    @Autowired
    private CityAuthorityRepository cityAuthorityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String saveCityAuthority(CityAuthorityRequest request) {
        // Check if email already exists
        if (cityAuthorityRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }

        CityAuthority cityAuthority = new CityAuthority();
        cityAuthority.setUserId(UUID.randomUUID().toString());
        cityAuthority.setName(request.getName());
        cityAuthority.setEmail(request.getEmail());
        cityAuthority.setPhone(request.getPhone());
        cityAuthority.setPassword(passwordEncoder.encode(request.getPassword()));
        cityAuthority.setUserType("CITY_AUTHORITY");
        cityAuthority.setEmployeeId(request.getEmployeeId());
        cityAuthority.setDepartment(request.getDepartment());
        cityAuthority.setCreatedAt(LocalDateTime.now());
        cityAuthority.setUpdatedAt(LocalDateTime.now());
        cityAuthority.setEnabled(true);
        
        CityAuthority savedCityAuthority = cityAuthorityRepository.save(cityAuthority);
        
        return "City Authority created with ID: " + savedCityAuthority.getUserId();
    }
    
    public List<CityAuthority> getAllCityAuthorities() {
        return cityAuthorityRepository.findAll();
    }
    
    public Optional<CityAuthority> getCityAuthorityById(String id) {
        return cityAuthorityRepository.findById(id);
    }
    
    public Optional<CityAuthority> getCityAuthorityByEmail(String email) {
        return cityAuthorityRepository.findByEmail(email);
    }
    
    public Optional<CityAuthority> getCityAuthorityByEmployeeId(String employeeId) {
        return cityAuthorityRepository.findByEmployeeId(employeeId);
    }
    
    public String updateCityAuthority(String id, CityAuthorityRequest request) {
        Optional<CityAuthority> existingCityAuthority = cityAuthorityRepository.findById(id);
        
        if (existingCityAuthority.isPresent()) {
            CityAuthority cityAuthority = existingCityAuthority.get();
            cityAuthority.setName(request.getName());
            cityAuthority.setEmail(request.getEmail());
            cityAuthority.setPhone(request.getPhone());
            cityAuthority.setEmployeeId(request.getEmployeeId());
            cityAuthority.setDepartment(request.getDepartment());
            cityAuthority.setUpdatedAt(LocalDateTime.now());
            
            cityAuthorityRepository.save(cityAuthority);
            return "City Authority updated successfully";
        }
        
        throw new RuntimeException("City Authority not found with id: " + id);
    }
    
    public String deleteCityAuthority(String id) {
        if (cityAuthorityRepository.existsById(id)) {
            cityAuthorityRepository.deleteById(id);
            return "City Authority deleted successfully";
        }
        
        throw new RuntimeException("City Authority not found with id: " + id);
    }
}