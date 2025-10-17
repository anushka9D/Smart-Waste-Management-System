package com.swms.repository;

import com.swms.model.Truck;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TruckRepository extends MongoRepository<Truck, String> {
    
   
    Optional<Truck> findByRegistrationNumber(String registrationNumber);
    
    
    List<Truck> findByCurrentStatus(String currentStatus);
    
    
    List<Truck> findByCurrentStatusAndAssignedDriverIdIsNull(String status);
    
    
    List<Truck> findByCurrentStatusAndCapacityGreaterThanEqual(String status, double capacity);
    
    
    List<Truck> findByAssignedDriverId(String driverId);
    
    
    Boolean existsByRegistrationNumber(String registrationNumber);
}