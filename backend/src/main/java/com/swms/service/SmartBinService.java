package com.swms.service;

import com.swms.dto.CreateSmartBinRequest;
import com.swms.dto.GPSLocationDTO;
import com.swms.dto.SmartBinDTO;
import com.swms.dto.UpdateBinLevelRequest;
import com.swms.model.GPSLocation;
import com.swms.model.SmartBin;
import com.swms.repository.SmartBinRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SmartBinService {

    private final SmartBinRepository smartBinRepository;
    private final BinSensorService binSensorService;
    private final AlertService alertService;

    private static final Double HALF_FULL_THRESHOLD = 50.0;
    private static final Double FULL_THRESHOLD = 80.0;

    public SmartBinService(SmartBinRepository smartBinRepository, BinSensorService binSensorService, AlertService alertService) {
        this.smartBinRepository = smartBinRepository;
        this.binSensorService = binSensorService;
        this.alertService = alertService;
    }

    @Transactional
    public SmartBinDTO createSmartBin(CreateSmartBinRequest request) {

        String binId = generateBinId();

        GPSLocation coordinates = new GPSLocation();
        coordinates.setLatitude(request.getLatitude());
        coordinates.setLongitude(request.getLongitude());

        SmartBin smartBin = new SmartBin();
        smartBin.setBinId(binId);
        smartBin.setLocation(request.getLocation());
        smartBin.setCoordinates(coordinates);
        smartBin.setCurrentLevel(0.0);
        smartBin.setCapacity(request.getCapacity());
        smartBin.setStatus("EMPTY");
        smartBin.setLastCollected(LocalDateTime.now());
        smartBin.setCreatedAt(LocalDateTime.now());
        smartBin.setLastUpdated(LocalDateTime.now());

        SmartBin savedBin = smartBinRepository.save(smartBin);

        binSensorService.createBinSensor(binId);

        return mapToDTO(savedBin, "GREEN");
    }

    public SmartBinDTO getSmartBinById(String binId) {

        Optional<SmartBin> optionalSmartBin = smartBinRepository.findByBinId(binId);
        if (optionalSmartBin.isEmpty()) {
            // Return null
            return null;
        }

        SmartBin smartBin = optionalSmartBin.get();
        String color = determineBinColor(smartBin.getStatus());
        return mapToDTO(smartBin, color);
    }


    public List<SmartBinDTO> getAllSmartBins() {

        return smartBinRepository.findAll().stream()
                .map(bin -> mapToDTO(bin, determineBinColor(bin.getStatus())))
                .collect(Collectors.toList());
    }

    public List<SmartBinDTO> getFullBins() {

        return smartBinRepository.findAllFullBins().stream()
                .map(bin -> mapToDTO(bin, "RED"))
                .collect(Collectors.toList());
    }

    public List<SmartBinDTO> getEmptyBins() {

        return smartBinRepository.findAllEmptyBins().stream()
                .map(bin -> mapToDTO(bin, "GREEN"))
                .collect(Collectors.toList());
    }

    public List<SmartBinDTO> getHalfFullBins() {

        return smartBinRepository.findAllHalfFullBins().stream()
                .map(bin -> mapToDTO(bin, "BLUE"))
                .collect(Collectors.toList());
    }

    @Transactional
    public SmartBinDTO updateBinCurrentLevel(UpdateBinLevelRequest request) {

        Optional<SmartBin> optionalSmartBin = smartBinRepository.findByBinId(request.getBinId());
        if (optionalSmartBin.isEmpty()) {
            // Bin not found
            return null;
        }

        SmartBin smartBin = optionalSmartBin.get();

        Double validatedLevel = validateAndAdjustMeasurement(request.getCurrentLevel());
        smartBin.setCurrentLevel(validatedLevel);
        smartBin.setLastUpdated(LocalDateTime.now());

        String newStatus = calculateBinStatus(validatedLevel, smartBin.getCapacity());
        String previousStatus = smartBin.getStatus();
        smartBin.setStatus(newStatus);

        SmartBin updatedBin = smartBinRepository.save(smartBin);

        binSensorService.updateSensorMeasurement(smartBin.getBinId(), validatedLevel);
        handleBinStatusChangeAlerts(smartBin, previousStatus, newStatus);

        String color = determineBinColor(newStatus);
        return mapToDTO(updatedBin, color);
    }


    @Transactional
    public SmartBinDTO markBinAsCollected(String binId) {

        Optional<SmartBin> optionalSmartBin = smartBinRepository.findByBinId(binId);
        if (optionalSmartBin.isEmpty()) {
            // Bin not found
            return null;
        }

        SmartBin smartBin = optionalSmartBin.get();

        smartBin.setCurrentLevel(0.0);
        smartBin.setStatus("EMPTY");
        smartBin.setLastCollected(LocalDateTime.now());
        smartBin.setLastUpdated(LocalDateTime.now());

        SmartBin updatedBin = smartBinRepository.save(smartBin);

        alertService.resolveAlertForBin(binId);

        return mapToDTO(updatedBin, "GREEN");
    }


    @Transactional
    public void deleteSmartBin(String binId) {

        Optional<SmartBin> optionalSmartBin = smartBinRepository.findByBinId(binId);

        if (optionalSmartBin.isPresent()) {
            smartBinRepository.delete(optionalSmartBin.get());
            alertService.deleteAlertsByBinId(binId);
        }

    }

    public List<SmartBinDTO> getBinsByLocation(String location) {

        return smartBinRepository.findByLocation(location).stream()
                .map(bin -> mapToDTO(bin, determineBinColor(bin.getStatus())))
                .collect(Collectors.toList());
    }


    private String calculateBinStatus(Double currentLevel, Double capacity) {
        Double percentage = (currentLevel / capacity) * 100;
        if (percentage >= FULL_THRESHOLD) {
            return "FULL";
        } else if (percentage >= HALF_FULL_THRESHOLD) {
            return "HALF_FULL";
        } else {
            return "EMPTY";
        }
    }

    private String determineBinColor(String status) {
        switch (status) {
            case "FULL":
                return "RED";
            case "HALF_FULL":
                return "BLUE";
            case "EMPTY":
                return "GREEN";
            default:
                return "GRAY";
        }
    }

    private Double validateAndAdjustMeasurement(Double measurement) {
        if (measurement < 0.0) return 0.0;
        if (measurement > 100.0) return 100.0;
        return measurement;
    }

    private void handleBinStatusChangeAlerts(SmartBin smartBin, String previousStatus, String newStatus) {
        if (newStatus.equals("FULL") && !previousStatus.equals("FULL")) {
            alertService.createAlert(smartBin);
        }
    }

    private String generateBinId() {
        return "BIN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private SmartBinDTO mapToDTO(SmartBin smartBin, String color) {
        SmartBinDTO smartBinDTO = new SmartBinDTO();

        smartBinDTO.setBinId(smartBin.getBinId());
        smartBinDTO.setLocation(smartBin.getLocation());

        GPSLocationDTO coordinatesDTO = new GPSLocationDTO();
        coordinatesDTO.setLatitude(smartBin.getCoordinates().getLatitude());
        coordinatesDTO.setLongitude(smartBin.getCoordinates().getLongitude());

        smartBinDTO.setCoordinates(coordinatesDTO);
        smartBinDTO.setCurrentLevel(smartBin.getCurrentLevel());
        smartBinDTO.setCapacity(smartBin.getCapacity());
        smartBinDTO.setStatus(smartBin.getStatus());
        smartBinDTO.setLastCollected(smartBin.getLastCollected());
        smartBinDTO.setBinColor(color);

        return smartBinDTO;
    }

}