package com.swms.repository;

import com.swms.model.RouteStop;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteStopRepository extends MongoRepository<RouteStop, String> {
    
    // Find stops by bin ID
    Optional<RouteStop> findByBinId(String binId);
    
    // Find stops by status
    List<RouteStop> findByStatus(String status);
    
    // Find stops by route (using stop IDs that would be stored in CollectionRoute)
    List<RouteStop> findByStopIdIn(List<String> stopIds);
    
    // Find stops by sequence order
    List<RouteStop> findBySequenceOrder(int sequenceOrder);
}