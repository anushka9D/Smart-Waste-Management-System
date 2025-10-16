package com.swms.service;

import com.swms.dto.AlertDTO;
import com.swms.model.Alert;
import com.swms.model.SmartBin;
import com.swms.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AlertService {

    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @Transactional
    public void createAlert(SmartBin smartBin) {

        String alertId = generateAlertId();
        String message = String.format("Bin %s at %s is full and needs collection",
                smartBin.getBinId(), smartBin.getLocation());

        Alert alert = new Alert();
        alert.setAlertId(alertId);
        alert.setBinId(smartBin.getBinId());
        alert.setLocation(smartBin.getLocation());
        alert.setAlertTitle("Bin Full Alert");
        alert.setMessage(message);
        alert.setCreatedAt(LocalDateTime.now());
        alert.setIsReviewed("NO");

        alertRepository.save(alert);
    }

    public List<AlertDTO> getAllUnreviewedAlerts() {

        return alertRepository.findUnreviewedAlerts().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<AlertDTO> getAllAlerts() {

        return alertRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAlertAsReviewed(String binId) {


        Alert alert = alertRepository.findByBinId(binId)
                .orElseThrow(() -> new RuntimeException("Alert not found for bin: " + binId));

        alert.setIsReviewed("YES");
        alertRepository.save(alert);

    }

    @Transactional
    public void resolveAlertForBin(String binId) {

        alertRepository.findByBinId(binId).ifPresent(alertRepository::delete);
    }

    @Transactional
    public void deleteAlertsByBinId(String binId) {

        alertRepository.findByBinId(binId).ifPresent(alertRepository::delete);
    }

    private AlertDTO mapToDTO(Alert alert) {
        AlertDTO alertDTO = new AlertDTO();
        alertDTO.setAlertId(alert.getAlertId());
        alertDTO.setBinId(alert.getBinId());
        alertDTO.setLocation(alert.getLocation());
        alertDTO.setAlertTitle(alert.getAlertTitle());
        alertDTO.setMessage(alert.getMessage());
        alertDTO.setCreatedAt(alert.getCreatedAt());
        alertDTO.setIsReviewed(alert.getIsReviewed());
        return alertDTO;
    }

    private String generateAlertId() {
        return "ALERT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
