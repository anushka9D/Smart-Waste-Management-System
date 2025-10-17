package com.swms.repository.jayathu;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.swms.model.jayathu.*;
import java.util.List;

@Repository
public interface MapBinRepository extends MongoRepository<SmartBinCityAuth, String> {
    
   
    List<SmartBinCityAuth> findByWasteType(String wasteType);
    
   
    List<SmartBinCityAuth> findByStatus(String status);
    
   
    List<SmartBinCityAuth> findByLocation(String location);
}