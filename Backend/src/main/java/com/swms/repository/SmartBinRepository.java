package com.swms.repository;

import com.swms.model.SmartBin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SmartBinRepository extends MongoRepository<SmartBin, String> {
    
    // Find smart bins by location
    List<SmartBin> findByLocation(String location);
    
    // Find smart bins by status
    List<SmartBin> findByStatus(String status);
    
    // Find smart bins with current level greater than a value
    List<SmartBin> findByCurrentLevelGreaterThan(double level);
    
    // Find smart bins with current level less than a value
    List<SmartBin> findByCurrentLevelLessThan(double level);
}