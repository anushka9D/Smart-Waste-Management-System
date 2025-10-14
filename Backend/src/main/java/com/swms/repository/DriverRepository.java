package com.swms.repository;

import com.swms.model.Driver;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends MongoRepository<Driver, String> {
    
    // Find driver by email
    Optional<Driver> findByEmail(String email);
    
    // Check if email exists
    Boolean existsByEmail(String email);
    
    // Find driver by license number
    Optional<Driver> findByLicenseNumber(String licenseNumber);
}