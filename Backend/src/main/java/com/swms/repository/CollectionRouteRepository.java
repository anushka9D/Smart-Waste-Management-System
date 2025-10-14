package com.swms.repository;

import com.swms.model.CollectionRoute;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CollectionRouteRepository extends MongoRepository<CollectionRoute, String> {
    
    // Find routes by driver ID
    List<CollectionRoute> findByAssignedDriverId(String driverId);
    
    // Find routes by staff ID (in the assignedStaffIds list)
    List<CollectionRoute> findByAssignedStaffIdsContaining(String staffId);
    
    // Find routes by date
    List<CollectionRoute> findByDateBetween(LocalDateTime start, LocalDateTime end);
    
    // Find routes by status
    List<CollectionRoute> findByStatus(String status);
    
    // Find routes by truck ID
    List<CollectionRoute> findByAssignedTruckId(String truckId);
    
    // Find route by date and status
    List<CollectionRoute> findByDateBetweenAndStatus(LocalDateTime start, LocalDateTime end, String status);
}