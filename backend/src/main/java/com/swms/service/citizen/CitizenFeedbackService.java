package com.swms.service.citizen;

import com.swms.dto.citizen.CitizenFeedbackDTO;
import com.swms.model.citizen.CitizenFeedback;
import com.swms.repository.citizen.CitizenFeedbackRepository;
import com.swms.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CitizenFeedbackService {

    @Autowired
    private CitizenFeedbackRepository citizenFeedbackRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    public CitizenFeedbackDTO createFeedback(String citizenId, String requestId, String topic, Integer rating, String comment, MultipartFile photo) throws IOException {
        CitizenFeedback feedback = new CitizenFeedback();
        feedback.setCitizenId(citizenId);
        feedback.setRequestId(requestId);
        feedback.setTopic(topic);
        feedback.setRating(rating);
        feedback.setComment(comment);
        feedback.setSubmittedAt(LocalDateTime.now());

        // Handle photo upload using Cloudinary
        if (photo != null && !photo.isEmpty()) {
            String photoUrl = cloudinaryService.uploadImage(photo, "citizen_feedback");
            feedback.setPhotoUrl(photoUrl);
        }

        CitizenFeedback savedFeedback = citizenFeedbackRepository.save(feedback);
        return mapToDTO(savedFeedback);
    }

    public List<CitizenFeedbackDTO> getFeedbackByCitizenId(String citizenId) {
        return citizenFeedbackRepository.findByCitizenId(citizenId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<CitizenFeedbackDTO> getFeedbackByRequestId(String requestId) {
        return citizenFeedbackRepository.findByRequestId(requestId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private CitizenFeedbackDTO mapToDTO(CitizenFeedback feedback) {
        return new CitizenFeedbackDTO(
                feedback.getId(),
                feedback.getCitizenId(),
                feedback.getRequestId(),
                feedback.getTopic(),
                feedback.getRating(),
                feedback.getComment(),
                feedback.getPhotoUrl(),
                feedback.getSubmittedAt()
        );
    }
}