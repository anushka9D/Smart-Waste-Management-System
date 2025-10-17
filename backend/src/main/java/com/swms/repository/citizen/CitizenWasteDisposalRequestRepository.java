package com.swms.repository.citizen;

import com.swms.model.citizen.CitizenWasteDisposalRequest;
import com.swms.model.citizen.CitizenRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CitizenWasteDisposalRequestRepository extends MongoRepository<CitizenWasteDisposalRequest, String> {
    
    // Find all requests by citizen ID with pagination
    Page<CitizenWasteDisposalRequest> findByCitizenId(String citizenId, Pageable pageable);
    
    // Find requests by citizen ID and status
    List<CitizenWasteDisposalRequest> findByCitizenIdAndStatus(String citizenId, CitizenRequestStatus status);
    
    // Find requests by status
    List<CitizenWasteDisposalRequest> findByStatus(CitizenRequestStatus status);
    
    // Find request by ID and citizen ID (for security)
    Optional<CitizenWasteDisposalRequest> findByRequestIdAndCitizenId(String requestId, String citizenId);
    
    // Count requests by citizen ID and status
    long countByCitizenIdAndStatus(String citizenId, CitizenRequestStatus status);
    
    // Find all requests with pagination (for admin/city authority)
    Page<CitizenWasteDisposalRequest> findAll(Pageable pageable);
}