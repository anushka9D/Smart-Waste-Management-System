package com.swms.service.anushka;

import com.swms.model.BinSensor;
import com.swms.repository.BinSensorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class BinSensorService {

    private final BinSensorRepository binSensorRepository;

    public BinSensorService(BinSensorRepository binSensorRepository) {
        this.binSensorRepository = binSensorRepository;
    }

    @Transactional
    public void createBinSensor(String binId) {

        String sensorId = generateSensorId();
        BinSensor binSensor = new BinSensor();
        binSensor.setSensorId(sensorId);
        binSensor.setBinId(binId);
        binSensor.setType("WORKING");
        binSensor.setColor("GREEN");
        binSensor.setMeasurement(0.0);
        binSensor.setLastReading(LocalDateTime.now());
        binSensor.setCreatedAt(LocalDateTime.now());

        binSensorRepository.save(binSensor);
    }


    @Transactional
    public void updateSensorMeasurement(String binId, Double measurement) {

        BinSensor sensor = binSensorRepository.findByBinId(binId)
                .orElseThrow(() -> new RuntimeException("Sensor not found for bin: " + binId));

        sensor.setMeasurement(measurement);
        sensor.setLastReading(LocalDateTime.now());
        sensor.setType("WORKING");
        sensor.setColor(determineSensorColor(measurement));

        binSensorRepository.save(sensor);
    }

    @Transactional
    public void markSensorAsFaulty(String binId) {

        BinSensor sensor = binSensorRepository.findByBinId(binId)
                .orElseThrow(() -> new RuntimeException("Sensor not found for bin: " + binId));

        sensor.setType("FAULTY");
        sensor.setColor("GRAY");
        binSensorRepository.save(sensor);

    }

    @Transactional
    public void resetSensorMeasurement(String binId) {
        BinSensor sensor = binSensorRepository.findByBinId(binId)
                .orElseThrow(() -> new RuntimeException("Sensor not found for bin: " + binId));

        sensor.setMeasurement(0.0);
        sensor.setLastReading(LocalDateTime.now());
        sensor.setType("WORKING");
        sensor.setColor("GREEN");

        binSensorRepository.save(sensor);
    }

    private String determineSensorColor(Double measurement) {
        if (measurement >= 80.0) {
            return "RED";
        } else if (measurement >= 50.0) {
            return "BLUE";
        } else {
            return "GREEN";
        }
    }

    private String generateSensorId() {
        return "SENSOR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
