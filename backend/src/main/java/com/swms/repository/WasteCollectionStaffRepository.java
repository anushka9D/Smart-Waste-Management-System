package com.swms.repository;

import com.swms.model.WasteCollectionStaff;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WasteCollectionStaffRepository extends MongoRepository<WasteCollectionStaff, String> {
    
    // Find staff by email
    Optional<WasteCollectionStaff> findByEmail(String email);
    
    // Check if email exists
    Boolean existsByEmail(String email);
    
    // Find staff by employee ID
    Optional<WasteCollectionStaff> findByEmployeeId(String employeeId);
    
    // Find available staff
    List<WasteCollectionStaff> findByAvailabilityTrueAndCurrentRouteIdIsNull();
    
    // Find staff by current route ID
    List<WasteCollectionStaff> findByCurrentRouteId(String routeId);
}