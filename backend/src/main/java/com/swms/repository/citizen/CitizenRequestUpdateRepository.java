package com.swms.repository.citizen;

import com.swms.model.citizen.CitizenRequestUpdate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitizenRequestUpdateRepository extends MongoRepository<CitizenRequestUpdate, String> {
    
    // Find all updates for a request, ordered by timestamp
    List<CitizenRequestUpdate> findByRequestIdOrderByTimestampAsc(String requestId);
    
    // Find latest update for a request
    CitizenRequestUpdate findTopByRequestIdOrderByTimestampDesc(String requestId);
}