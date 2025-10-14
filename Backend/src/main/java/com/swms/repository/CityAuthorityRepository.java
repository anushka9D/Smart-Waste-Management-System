package com.swms.repository;

import com.swms.model.CityAuthority;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityAuthorityRepository extends MongoRepository<CityAuthority, String> {
    
    // Find city authority by email
    Optional<CityAuthority> findByEmail(String email);
    
    // Check if email exists
    Boolean existsByEmail(String email);
    
    // Find city authority by employee ID
    Optional<CityAuthority> findByEmployeeId(String employeeId);
}