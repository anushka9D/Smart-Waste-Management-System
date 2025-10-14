package com.swms.repository;

import com.swms.model.Truck;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TruckRepository extends MongoRepository<Truck, String> {
    
    // Find truck by registration number
    Optional<Truck> findByRegistrationNumber(String registrationNumber);
    
    // Find trucks by current status
    List<Truck> findByCurrentStatus(String currentStatus);
    
    // Find available trucks
    List<Truck> findByCurrentStatusAndAssignedDriverIdIsNull(String status);
    
    // Find trucks assigned to a driver
    List<Truck> findByAssignedDriverId(String driverId);
    
    // Check if registration number exists
    Boolean existsByRegistrationNumber(String registrationNumber);
}