package com.swms.repository.citizen;

import com.swms.model.citizen.Citizen;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CitizenRepository extends MongoRepository<Citizen, String> {
    
    // Find citizen by name
    Optional<Citizen> findByName(String name);
    
    // Find citizen by email
    Optional<Citizen> findByEmail(String email);
    
    // Check if email exists
    Boolean existsByEmail(String email);
    
    // Find citizens by user type
    List<Citizen> findByUserType(String userType);
}