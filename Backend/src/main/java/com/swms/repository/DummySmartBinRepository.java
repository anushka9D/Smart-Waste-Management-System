package com.swms.repository;

// Changed import from SmartBin to DummySmartBin
import com.swms.model.DummySmartBin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DummySmartBinRepository extends MongoRepository<DummySmartBin, String> {
    
    // Find smart bins by location
    List<DummySmartBin> findByLocation(String location);
    
    // Find smart bins by status
    List<DummySmartBin> findByStatus(String status);
    
    // Find smart bins with current level greater than a value
    List<DummySmartBin> findByCurrentLevelGreaterThan(double level);
    
    // Find smart bins with current level less than a value
    List<DummySmartBin> findByCurrentLevelLessThan(double level);
}