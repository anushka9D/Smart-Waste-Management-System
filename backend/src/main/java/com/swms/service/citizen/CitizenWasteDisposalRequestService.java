package com.swms.service.citizen;

import com.swms.dto.citizen.CitizenWasteDisposalMultipartDTO;
import com.swms.dto.citizen.CitizenRequestStatusUpdateDTO;
import com.swms.model.GPSLocation;
import com.swms.model.citizen.*;
import com.swms.service.CloudinaryService;
import com.swms.repository.citizen.CitizenWasteDisposalRequestRepository;
import com.swms.repository.citizen.CitizenRequestUpdateRepository;
import com.swms.repository.citizen.CitizenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CitizenWasteDisposalRequestService {

    @Autowired
    private CitizenWasteDisposalRequestRepository requestRepository;

    @Autowired
    private CitizenRequestUpdateRepository updateRepository;

    @Autowired
    private CitizenRepository citizenRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    public CitizenWasteDisposalRequest createRequest(String citizenEmail, CitizenWasteDisposalMultipartDTO requestDTO) {
        // Validate citizen exists by email
        Optional<Citizen> citizenOpt = citizenRepository.findByEmail(citizenEmail);
        if (citizenOpt.isEmpty()) {
            throw new RuntimeException("Citizen not found with email: " + citizenEmail);
        }

        Citizen citizen = citizenOpt.get();

        // Upload photo if provided (MultipartFile handling)
        String photoUrl = null;
        if (requestDTO.getPhoto() != null && !requestDTO.getPhoto().isEmpty()) {
            try {
                photoUrl = cloudinaryService.uploadImage(requestDTO.getPhoto());
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload photo: " + e.getMessage());
            }
        }

        // Create GPS location from separate latitude/longitude
        GPSLocation coordinates = new GPSLocation();
        coordinates.setLatitude(requestDTO.getLatitude());
        coordinates.setLongitude(requestDTO.getLongitude());

        // Create request
        CitizenWasteDisposalRequest request = new CitizenWasteDisposalRequest();
        request.setRequestId(generateRequestId());
        request.setCitizenId(citizen.getUserId());
        request.setCitizenName(citizen.getName());
        request.setCitizenEmail(citizen.getEmail());
        request.setCategory(requestDTO.getCategory());
        request.setDescription(requestDTO.getDescription());
        request.setBinId(requestDTO.getBinId());
        request.setCoordinates(coordinates);
        request.setAddress(requestDTO.getAddress());
        request.setPhotoUrl(photoUrl);
        request.setStatus(CitizenRequestStatus.SUBMITTED);
        request.setSubmittedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());

        CitizenWasteDisposalRequest savedRequest = requestRepository.save(request);

        // Create initial status update
        createStatusUpdate(savedRequest.getRequestId(), CitizenRequestStatus.SUBMITTED,
                "Request submitted successfully", "system");

        return savedRequest;
    }

    // This method should use citizenId (from JWT token's userId)
    public Page<CitizenWasteDisposalRequest> getRequestsByCitizen(String citizenId, Pageable pageable) {
        return requestRepository.findByCitizenId(citizenId, pageable);
    }

    // This should also use citizenId for security
    public Optional<CitizenWasteDisposalRequest> getRequestById(String requestId, String citizenId) {
        return requestRepository.findByRequestIdAndCitizenId(requestId, citizenId);
    }

    public List<CitizenRequestUpdate> getRequestUpdates(String requestId) {
        return updateRepository.findByRequestIdOrderByTimestampAsc(requestId);
    }

    public CitizenWasteDisposalRequest updateRequestStatus(String requestId, CitizenRequestStatusUpdateDTO statusUpdate,
            String updatedBy) {
        Optional<CitizenWasteDisposalRequest> requestOpt = requestRepository.findById(requestId);

        if (requestOpt.isEmpty()) {
            throw new RuntimeException("Request not found with ID: " + requestId);
        }

        CitizenWasteDisposalRequest request = requestOpt.get();
        CitizenRequestStatus oldStatus = request.getStatus();
        CitizenRequestStatus newStatus = statusUpdate.getStatus();

        // Validate status transition
        if (!isValidStatusTransition(oldStatus, newStatus)) {
            throw new RuntimeException("Invalid status transition from " + oldStatus + " to " + newStatus);
        }

        // Update request
        request.setStatus(newStatus);
        request.setUpdatedAt(LocalDateTime.now());
        CitizenWasteDisposalRequest updatedRequest = requestRepository.save(request);

        // Create status update
        createStatusUpdate(requestId, newStatus, statusUpdate.getNote(), updatedBy);

        return updatedRequest;
    }

    public boolean cancelRequest(String requestId, String citizenId) {
        Optional<CitizenWasteDisposalRequest> requestOpt = requestRepository.findByRequestIdAndCitizenId(requestId,
                citizenId);

        if (requestOpt.isEmpty()) {
            throw new RuntimeException("Request not found or access denied");
        }

        CitizenWasteDisposalRequest request = requestOpt.get();

        // Only allow cancellation if request is not already in progress
        if (request.getStatus() == CitizenRequestStatus.SUBMITTED
                || request.getStatus() == CitizenRequestStatus.ASSIGNED) {
            request.setStatus(CitizenRequestStatus.CANCELLED);
            request.setUpdatedAt(LocalDateTime.now());
            requestRepository.save(request);

            createStatusUpdate(requestId, CitizenRequestStatus.CANCELLED, "Request cancelled by citizen", citizenId);
            return true;
        }

        throw new RuntimeException("Cannot cancel request in current status: " + request.getStatus());
    }

    private String generateRequestId() {
        return "REQ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void createStatusUpdate(String requestId, CitizenRequestStatus status, String note, String updatedBy) {
        CitizenRequestUpdate update = new CitizenRequestUpdate(requestId, status, note, updatedBy);
        updateRepository.save(update);
    }

    private boolean isValidStatusTransition(CitizenRequestStatus from, CitizenRequestStatus to) {
        // Define valid status transitions
        return switch (from) {
            case SUBMITTED -> to == CitizenRequestStatus.ASSIGNED || to == CitizenRequestStatus.CANCELLED;
            case ASSIGNED -> to == CitizenRequestStatus.COLLECTION_SCHEDULED || to == CitizenRequestStatus.CANCELLED;
            case COLLECTION_SCHEDULED -> to == CitizenRequestStatus.OUT_FOR_COLLECTION;
            case OUT_FOR_COLLECTION -> to == CitizenRequestStatus.COLLECTED;
            case COLLECTED -> to == CitizenRequestStatus.RESOLVED;
            default -> false;
        };
    }

}