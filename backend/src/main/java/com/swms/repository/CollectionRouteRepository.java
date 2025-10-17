package com.swms.repository;

import com.swms.model.CollectionRoute;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CollectionRouteRepository extends MongoRepository<CollectionRoute, String> {
    
    
    List<CollectionRoute> findByAssignedDriverId(String driverId);
    
    
    List<CollectionRoute> findByAssignedStaffIdsContaining(String staffId);
    
    
    List<CollectionRoute> findByDateBetween(LocalDateTime start, LocalDateTime end);
    
    
    List<CollectionRoute> findByStatus(String status);
    
    
    List<CollectionRoute> findByAssignedTruckId(String truckId);
    
    
    List<CollectionRoute> findByDateBetweenAndStatus(LocalDateTime start, LocalDateTime end, String status);
}