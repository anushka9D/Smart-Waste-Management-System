package com.swms.repository;

import com.swms.model.WasteCollectionStaff;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WasteCollectionStaffRepository extends MongoRepository<WasteCollectionStaff, String> {
    
    
    Optional<WasteCollectionStaff> findByEmail(String email);
    
    
    Boolean existsByEmail(String email);
    
    
    Optional<WasteCollectionStaff> findByEmployeeId(String employeeId);
    
  
    List<WasteCollectionStaff> findByAvailabilityTrueAndCurrentRouteIdIsNull();
    
    
    List<WasteCollectionStaff> findByCurrentRouteId(String routeId);
}