package com.swms.controller.citizen;

import com.swms.dto.ApiResponse;
import com.swms.dto.citizen.CitizenFeedbackDTO;
import com.swms.model.citizen.Citizen;
import com.swms.repository.citizen.CitizenRepository;
import com.swms.security.JwtUtil;
import com.swms.service.citizen.CitizenFeedbackService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/citizen/feedback")
@CrossOrigin(originPatterns = "*", maxAge = 3600)
@Validated
public class CitizenFeedbackController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CitizenFeedbackService citizenFeedbackService;

    @Autowired
    private CitizenRepository citizenRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<CitizenFeedbackDTO>> submitFeedback(
            @RequestParam("requestId") @NotBlank String requestId,
            @RequestParam("topic") @NotBlank String topic,
            @RequestParam("rating") @NotNull @Min(1) @Max(5) Integer rating,
            @RequestParam(value = "comment", required = false) String comment,
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            String citizenId = getUserCitizenId(userDetails);
            if (citizenId == null) {
                return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));
            }

            CitizenFeedbackDTO feedback = citizenFeedbackService.createFeedback(citizenId, requestId, topic, rating, comment, photo);
            return ResponseEntity.ok(ApiResponse.success("Feedback submitted successfully", feedback));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to process photo: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to submit feedback: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CitizenFeedbackDTO>>> getMyFeedback(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String citizenId = getUserCitizenId(userDetails);
            if (citizenId == null) {
                return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));
            }

            List<CitizenFeedbackDTO> feedbackList = citizenFeedbackService.getFeedbackByCitizenId(citizenId);
            return ResponseEntity.ok(ApiResponse.success("Feedback retrieved successfully", feedbackList));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to retrieve feedback: " + e.getMessage()));
        }
    }

    @GetMapping("/request/{requestId}")
    public ResponseEntity<ApiResponse<List<CitizenFeedbackDTO>>> getFeedbackByRequest(
            @PathVariable @NotBlank String requestId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String citizenId = getUserCitizenId(userDetails);
            if (citizenId == null) {
                return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));
            }

            List<CitizenFeedbackDTO> feedbackList = citizenFeedbackService.getFeedbackByRequestId(requestId);
            return ResponseEntity.ok(ApiResponse.success("Feedback retrieved successfully", feedbackList));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to retrieve feedback: " + e.getMessage()));
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