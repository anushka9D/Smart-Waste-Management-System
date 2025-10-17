package com.swms.repository;

import com.swms.model.RouteStop;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteStopRepository extends MongoRepository<RouteStop, String> {
    
    
    Optional<RouteStop> findByBinId(String binId);
    
   
    List<RouteStop> findByStatus(String status);
    
    
    List<RouteStop> findByStopIdIn(List<String> stopIds);
    
    
    List<RouteStop> findBySequenceOrder(int sequenceOrder);
}