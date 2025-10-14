package com.swms.repository;

import com.swms.model.SensorManager;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SensorManagerRepository extends MongoRepository<SensorManager, String> {
    
    // Find sensor manager by email
    Optional<SensorManager> findByEmail(String email);
    
    // Check if email exists
    Boolean existsByEmail(String email);
    
    // Find sensor manager by employee ID
    Optional<SensorManager> findByEmployeeId(String employeeId);
}