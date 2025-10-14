package com.swms.service;

import com.swms.dto.SensorManagerRequest;
import com.swms.model.SensorManager;
import com.swms.repository.SensorManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SensorManagerService {

    @Autowired
    private SensorManagerRepository sensorManagerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String saveSensorManager(SensorManagerRequest request) {
        // Check if email already exists
        if (sensorManagerRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }

        SensorManager sensorManager = new SensorManager();
        sensorManager.setUserId(UUID.randomUUID().toString());
        sensorManager.setName(request.getName());
        sensorManager.setEmail(request.getEmail());
        sensorManager.setPhone(request.getPhone());
        sensorManager.setPassword(passwordEncoder.encode(request.getPassword()));
        sensorManager.setUserType("SENSOR_MANAGER");
        sensorManager.setEmployeeId(request.getEmployeeId());
        sensorManager.setAssignedZone(request.getAssignedZone());
        sensorManager.setCreatedAt(LocalDateTime.now());
        sensorManager.setUpdatedAt(LocalDateTime.now());
        sensorManager.setEnabled(true);
        
        SensorManager savedSensorManager = sensorManagerRepository.save(sensorManager);
        
        return "Sensor Manager created with ID: " + savedSensorManager.getUserId();
    }
    
    public List<SensorManager> getAllSensorManagers() {
        return sensorManagerRepository.findAll();
    }
    
    public Optional<SensorManager> getSensorManagerById(String id) {
        return sensorManagerRepository.findById(id);
    }
    
    public Optional<SensorManager> getSensorManagerByEmail(String email) {
        return sensorManagerRepository.findByEmail(email);
    }
    
    public Optional<SensorManager> getSensorManagerByEmployeeId(String employeeId) {
        return sensorManagerRepository.findByEmployeeId(employeeId);
    }
    
    public String updateSensorManager(String id, SensorManagerRequest request) {
        Optional<SensorManager> existingSensorManager = sensorManagerRepository.findById(id);
        
        if (existingSensorManager.isPresent()) {
            SensorManager sensorManager = existingSensorManager.get();
            sensorManager.setName(request.getName());
            sensorManager.setEmail(request.getEmail());
            sensorManager.setPhone(request.getPhone());
            sensorManager.setEmployeeId(request.getEmployeeId());
            sensorManager.setAssignedZone(request.getAssignedZone());
            sensorManager.setUpdatedAt(LocalDateTime.now());
            
            sensorManagerRepository.save(sensorManager);
            return "Sensor Manager updated successfully";
        }
        
        throw new RuntimeException("Sensor Manager not found with id: " + id);
    }
    
    public String deleteSensorManager(String id) {
        if (sensorManagerRepository.existsById(id)) {
            sensorManagerRepository.deleteById(id);
            return "Sensor Manager deleted successfully";
        }
        
        throw new RuntimeException("Sensor Manager not found with id: " + id);
    }
}