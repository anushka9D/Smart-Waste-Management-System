package com.swms.repository;

import com.swms.model.WasteCollectionStaff;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WasteCollectionStaffRepository extends MongoRepository<WasteCollectionStaff, String> {
    
    // Find waste collection staff by email
    Optional<WasteCollectionStaff> findByEmail(String email);
    
    // Check if email exists
    Boolean existsByEmail(String email);
    
    // Find waste collection staff by employee ID
    Optional<WasteCollectionStaff> findByEmployeeId(String employeeId);
}