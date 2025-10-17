package com.swms.service.anushka;

import com.swms.model.BinSensor;
import com.swms.model.SmartBin;
import com.swms.repository.BinSensorRepository;
import com.swms.repository.SmartBinRepository;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class SensorSimulationService {

    private final BinSensorRepository binSensorRepository;
    private final SmartBinRepository smartBinRepository;
    private final AlertService alertService;
    private final Random random = new Random();

    private static final Double Half_full = 50.0;
    private static final Double Full = 80.0;
    private static final Double Min_increment = 0.5;
    private static final Double Max_increment = 3.0;

    public SensorSimulationService(BinSensorRepository binSensorRepository, 
                                   SmartBinRepository smartBinRepository,
                                   AlertService alertService) {
        this.binSensorRepository = binSensorRepository;
        this.smartBinRepository = smartBinRepository;
        this.alertService = alertService;
    }

    // Runs every 1 minute
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void simulateSensorReadings() {
        List<BinSensor> sensors = binSensorRepository.findAll();
        
        for (BinSensor sensor : sensors) {
            if ("WORKING".equals(sensor.getType())) {
                updateSensorReading(sensor);
            }
        }
    }

    private void updateSensorReading(BinSensor sensor) {
        Double currentMeasurement = sensor.getMeasurement();
        
        // bin already at 100 or above, not increment
        if (currentMeasurement >= 100.0) {
            if (currentMeasurement > 100.0) {
                
                sensor.setMeasurement(100.0);
                sensor.setLastReading(LocalDateTime.now());
                sensor.setColor("RED");
                binSensorRepository.save(sensor);
                updateSmartBinFromSensor(sensor.getBinId(), 100.0);
            }
            return; // stop and not increment
        }
        
        // random increment value
        Double increment = Min_increment + (random.nextDouble() * (Max_increment - Min_increment));
        
        // calculate new measurement and doesn't exceed 100.0
        Double newMeasurement = Math.min(currentMeasurement + increment, 100.0);
        
        // Update sensor
        sensor.setMeasurement(newMeasurement);
        sensor.setLastReading(LocalDateTime.now());
        sensor.setColor(determineSensorColor(newMeasurement));
        binSensorRepository.save(sensor);
        
        // update SmartBin
        updateSmartBinFromSensor(sensor.getBinId(), newMeasurement);
    }

    private void updateSmartBinFromSensor(String binId, Double newLevel) {
        smartBinRepository.findByBinId(binId).ifPresent(smartBin -> {
            String previousStatus = smartBin.getStatus();
            
            Double cappedLevel = Math.min(newLevel, 100.0);
            
            smartBin.setCurrentLevel(cappedLevel);
            smartBin.setLastUpdated(LocalDateTime.now());
            
            String newStatus = calculateBinStatus(cappedLevel);
            smartBin.setStatus(newStatus);
            
            smartBinRepository.save(smartBin);
            
            // alerts
            handleBinStatusChangeAlerts(smartBin, previousStatus, newStatus);
        });
    }

    String calculateBinStatus(Double currentLevel) {
        if (currentLevel >= Full) {
            return "FULL";
        } else if (currentLevel >= Half_full) {
            return "HALF_FULL";
        } else {
            return "EMPTY";
        }
    }

    String determineSensorColor(Double measurement) {
        if (measurement >= 80.0) {
            return "RED";
        } else if (measurement >= 50.0) {
            return "BLUE";
        } else {
            return "GREEN";
        }
    }

    void handleBinStatusChangeAlerts(SmartBin smartBin, String previousStatus, String newStatus) {
        if (newStatus.equals("FULL") && !previousStatus.equals("FULL")) {
            alertService.createAlert(smartBin);
        }
    }
}