package com.swms.controller.citizen;

import com.swms.dto.ApiResponse;
import com.swms.dto.citizen.CitizenWasteDisposalMultipartDTO;
import com.swms.model.citizen.Citizen;
import com.swms.model.citizen.CitizenWasteDisposalRequest;
import com.swms.model.citizen.CitizenRequestUpdate;
import com.swms.repository.citizen.CitizenRepository;
import com.swms.service.citizen.CitizenWasteDisposalRequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/citizen/waste-disposal-requests")
@CrossOrigin(originPatterns = "*", maxAge = 3600)
public class CitizenWasteDisposalRequestController {

    @Autowired
    private CitizenWasteDisposalRequestService requestService;
    
    @Autowired
    private CitizenRepository citizenRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CitizenWasteDisposalRequest>>> getCitizenRequests(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        try {
            String citizenId = getUserCitizenId(userDetails);
            Pageable pageable = PageRequest.of(page, size);
            Page<CitizenWasteDisposalRequest> requests = requestService.getRequestsByCitizen(citizenId, pageable);
            return ResponseEntity.ok(ApiResponse.success("Requests retrieved successfully", requests));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to retrieve requests"));
        }
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ApiResponse<CitizenWasteDisposalRequest>> getRequestDetails(
            @PathVariable String requestId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String citizenId = getUserCitizenId(userDetails);
            CitizenWasteDisposalRequest request = requestService.getRequestById(requestId, citizenId)
                    .orElseThrow(() -> new RuntimeException("Request not found"));
            return ResponseEntity.ok(ApiResponse.success("Request details retrieved", request));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to retrieve request details"));
        }
    }

    @GetMapping("/{requestId}/updates")
    public ResponseEntity<ApiResponse<List<CitizenRequestUpdate>>> getRequestUpdates(
            @PathVariable String requestId) {
        try {
            List<CitizenRequestUpdate> updates = requestService.getRequestUpdates(requestId);
            return ResponseEntity.ok(ApiResponse.success("Request updates retrieved", updates));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to retrieve request updates"));
        }
    }

    @PutMapping("/{requestId}/cancel")
    public ResponseEntity<ApiResponse<String>> cancelRequest(
            @PathVariable String requestId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String citizenId = getUserCitizenId(userDetails);
            boolean cancelled = requestService.cancelRequest(requestId, citizenId);
            if (!cancelled) {
                throw new RuntimeException("Failed to cancel request");
            }
            return ResponseEntity.ok(ApiResponse.success("Request cancelled successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to cancel request"));
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CitizenWasteDisposalRequest>> createRequest(
            @Valid @ModelAttribute CitizenWasteDisposalMultipartDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String citizenId = getUserCitizenId(userDetails);
            CitizenWasteDisposalRequest request = requestService.createRequest(citizenId, requestDTO);
            return ResponseEntity.ok(ApiResponse.success("Request created successfully", request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to create request"));
        }
    }

    private String getUserCitizenId(UserDetails userDetails) {
        if (userDetails == null) {
            throw new RuntimeException("User not authenticated");
        }
        
        // Get the citizen by email to retrieve the actual user ID
        String email = userDetails.getUsername();
        Optional<Citizen> citizenOpt = citizenRepository.findByEmail(email);
        if (citizenOpt.isEmpty()) {
            // Improved error message for debugging
            throw new RuntimeException("Citizen not found with email: " + email + ". Current user details username: " + userDetails.getUsername());
        }
        
        return citizenOpt.get().getUserId();
    }
}