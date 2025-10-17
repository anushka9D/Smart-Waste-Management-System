package com.swms.repository;

import com.swms.model.CityAuthority;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityAuthorityRepository extends MongoRepository<CityAuthority, String> {
    
    
    Optional<CityAuthority> findByEmail(String email);
    
    
    Boolean existsByEmail(String email);
    
    
    Optional<CityAuthority> findByEmployeeId(String employeeId);
}