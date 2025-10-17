package com.swms.repository;

import com.swms.model.Driver;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends MongoRepository<Driver, String> {
    
    
    Optional<Driver> findByEmail(String email);
    
    
    Boolean existsByEmail(String email);
    
    
    Optional<Driver> findByLicenseNumber(String licenseNumber);
    
    
    List<Driver> findByAvailabilityTrueAndCurrentRouteIdIsNull();
    
    
    Optional<Driver> findByCurrentRouteId(String routeId);
}