package com.swms.controller.citizen;

import com.swms.dto.ApiResponse;
import com.swms.dto.citizen.CitizenWasteDisposalMultipartDTO;
import com.swms.model.citizen.Citizen;
import com.swms.model.citizen.CitizenWasteDisposalRequest;
import com.swms.model.citizen.CitizenRequestUpdate;
import com.swms.repository.citizen.CitizenRepository;
import com.swms.security.JwtUtil;
import com.swms.service.citizen.CitizenWasteDisposalRequestService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/citizen/waste-disposal-requests")
@CrossOrigin(originPatterns = "*", maxAge = 3600)
public class CitizenWasteDisposalRequestController {

    @Autowired
    private JwtUtil jwtUtil;

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

        // Extract token from SecurityContext
        String token = null;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            // Try to get token from Authorization header
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                    .getRequest();
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7).trim();
            }
        }

        if (token == null) {
            throw new RuntimeException("JWT token not found");
        }

        // First try to get userId from token claims
        String userId = jwtUtil.getUserIdFromToken(token);
        if (userId != null && !userId.isEmpty()) {
            // Validate that userId exists in database
            Optional<Citizen> citizenOpt = citizenRepository.findById(userId);
            if (citizenOpt.isPresent()) {
                return userId;
            }
        }

        // Fallback to email if userId not found or invalid
        String email = jwtUtil.getEmailFromToken(token);
        // Check if email looks like a UUID (which would indicate an error)
        if (email != null && email.contains("-") && email.length() == 36) {
            // This seems to be a UUID, try to find citizen by userId instead
            Optional<Citizen> citizenOpt = citizenRepository.findById(email);
            if (citizenOpt.isPresent()) {
                return email;
            }
        } else {
            // Normal email case
            Optional<Citizen> citizenOpt = citizenRepository.findByEmail(email);
            if (citizenOpt.isEmpty()) {
                throw new RuntimeException("Citizen not found with email: " + email);
            }
            return citizenOpt.get().getUserId();
        }

        throw new RuntimeException("Citizen not found with id: " + userId + " or email: " + email);
    }

}