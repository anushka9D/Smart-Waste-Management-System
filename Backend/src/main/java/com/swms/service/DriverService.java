package com.swms.service;

import com.swms.dto.DriverRequest;
import com.swms.model.Driver;
import com.swms.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String saveDriver(DriverRequest request) {
        // Check if email already exists
        if (driverRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }

        Driver driver = new Driver();
        driver.setUserId(UUID.randomUUID().toString());
        driver.setName(request.getName());
        driver.setEmail(request.getEmail());
        driver.setPhone(request.getPhone());
        driver.setPassword(passwordEncoder.encode(request.getPassword()));
        driver.setLicenseNumber(request.getLicenseNumber());
        driver.setVehicleType(request.getVehicleType());
        driver.setCreatedAt(LocalDateTime.now());
        driver.setUpdatedAt(LocalDateTime.now());
        driver.setEnabled(true);
        
        Driver savedDriver = driverRepository.save(driver);
        
        return "Driver created with ID: " + savedDriver.getUserId();
    }
    
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }
    
    public Optional<Driver> getDriverById(String id) {
        return driverRepository.findById(id);
    }
    
    public Optional<Driver> getDriverByEmail(String email) {
        return driverRepository.findByEmail(email);
    }
    
    public Optional<Driver> getDriverByLicenseNumber(String licenseNumber) {
        return driverRepository.findByLicenseNumber(licenseNumber);
    }
    
    public String updateDriver(String id, DriverRequest request) {
        Optional<Driver> existingDriver = driverRepository.findById(id);
        
        if (existingDriver.isPresent()) {
            Driver driver = existingDriver.get();
            driver.setName(request.getName());
            driver.setEmail(request.getEmail());
            driver.setPhone(request.getPhone());
            driver.setLicenseNumber(request.getLicenseNumber());
            driver.setVehicleType(request.getVehicleType());
            driver.setUpdatedAt(LocalDateTime.now());
            
            driverRepository.save(driver);
            return "Driver updated successfully";
        }
        
        throw new RuntimeException("Driver not found with id: " + id);
    }
    
    public String deleteDriver(String id) {
        if (driverRepository.existsById(id)) {
            driverRepository.deleteById(id);
            return "Driver deleted successfully";
        }
        
        throw new RuntimeException("Driver not found with id: " + id);
    }
}