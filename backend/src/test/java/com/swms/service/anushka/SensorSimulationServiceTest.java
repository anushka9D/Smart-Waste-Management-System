package com.swms.service.anushka;

import com.swms.model.BinSensor;
import com.swms.model.SmartBin;
import com.swms.repository.BinSensorRepository;
import com.swms.repository.SmartBinRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SensorSimulationServiceTest {

    @Mock
    private BinSensorRepository binSensorRepository;

    @Mock
    private SmartBinRepository smartBinRepository;

    @Mock
    private AlertService alertService;

    @InjectMocks
    private SensorSimulationService sensorSimulationService;

    private BinSensor testSensor;
    private SmartBin testBin;

    @BeforeEach
    void setUp() {
        testSensor = new BinSensor();
        testSensor.setSensorId("SENSOR-12345678");
        testSensor.setBinId("BIN001");
        testSensor.setType("WORKING");
        testSensor.setColor("GREEN");
        testSensor.setMeasurement(0.0);
        testSensor.setLastReading(LocalDateTime.now().minusMinutes(5));
        testSensor.setCreatedAt(LocalDateTime.now().minusDays(1));

        testBin = new SmartBin();
        testBin.setBinId("BIN001");
        testBin.setCurrentLevel(0.0);
        testBin.setCapacity(100.0);
        testBin.setStatus("ACTIVE");
    }

    // Test simulateSensorReadings method - Positive Cases
    @Test
    void testSimulateSensorReadings_WorkingSensors() {
        // Arrange
        List<BinSensor> sensors = Arrays.asList(testSensor);
        when(binSensorRepository.findAll()).thenReturn(sensors);
        when(binSensorRepository.save(any(BinSensor.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(smartBinRepository.findByBinId(anyString())).thenReturn(Optional.of(testBin));
        when(smartBinRepository.save(any(SmartBin.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        sensorSimulationService.simulateSensorReadings();

        // Assert
        verify(binSensorRepository, times(1)).findAll();
        verify(binSensorRepository, atLeastOnce()).save(any(BinSensor.class));
    }

    @Test
    void testSimulateSensorReadings_MultipleSensors() {
        // Arrange
        BinSensor sensor2 = new BinSensor();
        sensor2.setSensorId("SENSOR-87654321");
        sensor2.setBinId("BIN002");
        sensor2.setType("WORKING");
        sensor2.setColor("GREEN");
        sensor2.setMeasurement(25.0);
        sensor2.setLastReading(LocalDateTime.now().minusMinutes(3));
        sensor2.setCreatedAt(LocalDateTime.now().minusHours(2));

        List<BinSensor> sensors = Arrays.asList(testSensor, sensor2);
        when(binSensorRepository.findAll()).thenReturn(sensors);
        when(binSensorRepository.save(any(BinSensor.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(smartBinRepository.findByBinId(anyString())).thenReturn(Optional.of(testBin));
        when(smartBinRepository.save(any(SmartBin.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        sensorSimulationService.simulateSensorReadings();

        // Assert
        verify(binSensorRepository, times(1)).findAll();
        verify(binSensorRepository, atLeastOnce()).save(any(BinSensor.class));
    }

    // Test simulateSensorReadings method - Edge Cases
    @Test
    void testSimulateSensorReadings_NoSensors() {
        // Arrange
        when(binSensorRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        sensorSimulationService.simulateSensorReadings();

        // Assert
        verify(binSensorRepository, times(1)).findAll();
        verify(binSensorRepository, never()).save(any(BinSensor.class));
        verify(smartBinRepository, never()).save(any(SmartBin.class));
    }

    @Test
    void testSimulateSensorReadings_FaultySensors() {
        // Arrange
        testSensor.setType("FAULTY");
        List<BinSensor> sensors = Arrays.asList(testSensor);
        when(binSensorRepository.findAll()).thenReturn(sensors);

        // Act
        sensorSimulationService.simulateSensorReadings();

        // Assert
        verify(binSensorRepository, times(1)).findAll();
        verify(binSensorRepository, never()).save(any(BinSensor.class));
        verify(smartBinRepository, never()).save(any(SmartBin.class));
    }

    // Test simulateSensorReadings method - Error Cases
    @Test
    void testSimulateSensorReadings_RepositoryException() {
        // Arrange
        when(binSensorRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            sensorSimulationService.simulateSensorReadings();
        });

        verify(binSensorRepository, times(1)).findAll();
        verify(binSensorRepository, never()).save(any(BinSensor.class));
        verify(smartBinRepository, never()).save(any(SmartBin.class));
    }

    // Test updateSmartBinFromSensor behavior - Positive Cases
    @Test
    void testUpdateSmartBinFromSensor_Success() {
        // Arrange
        String binId = "BIN001";
        Double measurement = 75.0;
        when(smartBinRepository.findByBinId(binId)).thenReturn(Optional.of(testBin));
        when(smartBinRepository.save(any(SmartBin.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act - We can't directly call the private method, but we can test the behavior through simulateSensorReadings
        testSensor.setMeasurement(75.0);
        when(binSensorRepository.findAll()).thenReturn(Arrays.asList(testSensor));
        when(binSensorRepository.save(any(BinSensor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        sensorSimulationService.simulateSensorReadings();

        // Assert
        verify(smartBinRepository, times(1)).findByBinId(binId);
        verify(smartBinRepository, times(1)).save(any(SmartBin.class));
    }

    // Test updateSmartBinFromSensor behavior - Edge Cases
    @Test
    void testUpdateSmartBinFromSensor_BinNotFound() {
        // Arrange
        String binId = "NONEXISTENT";
        Double measurement = 75.0;
        when(smartBinRepository.findByBinId(binId)).thenReturn(Optional.empty());

        // Act - We can't directly call the private method, but we can test the behavior through simulateSensorReadings
        BinSensor sensor = new BinSensor();
        sensor.setSensorId("SENSOR-11111111");
        sensor.setBinId("NONEXISTENT");
        sensor.setType("WORKING");
        sensor.setColor("GREEN");
        sensor.setMeasurement(75.0);
        sensor.setLastReading(LocalDateTime.now().minusMinutes(5));
        sensor.setCreatedAt(LocalDateTime.now().minusDays(1));

        when(binSensorRepository.findAll()).thenReturn(Arrays.asList(sensor));

        // This should not throw an exception even if the bin is not found
        sensorSimulationService.simulateSensorReadings();

        // Assert
        verify(smartBinRepository, times(1)).findByBinId(binId);
        verify(smartBinRepository, never()).save(any(SmartBin.class));
    }

    // Test handleBinStatusChangeAlerts behavior - Positive Cases
    @Test
    void testHandleBinStatusChangeAlerts_CreateAlert() {
        // Arrange
        SmartBin bin = new SmartBin();
        bin.setBinId("BIN001");
        bin.setStatus("FULL");
        
        String previousStatus = "HALF_FULL";
        String newStatus = "FULL";

        // We can't directly test the private method, but we can test its behavior indirectly
        // by setting up a sensor that will trigger the alert condition
        testSensor.setMeasurement(85.0); // This should trigger a FULL status
        testBin.setStatus("HALF_FULL"); // Previous status
        
        when(binSensorRepository.findAll()).thenReturn(Arrays.asList(testSensor));
        when(binSensorRepository.save(any(BinSensor.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(smartBinRepository.findByBinId("BIN001")).thenReturn(Optional.of(testBin));
        when(smartBinRepository.save(any(SmartBin.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        sensorSimulationService.simulateSensorReadings();

        // Assert - An alert should be created when status changes from HALF_FULL to FULL
        verify(alertService, times(1)).createAlert(any(SmartBin.class));
    }

    // Test handleBinStatusChangeAlerts behavior - Edge Cases
    @Test
    void testHandleBinStatusChangeAlerts_NoAlert() {
        // Arrange - No status change that would trigger an alert
        testSensor.setMeasurement(65.0); // This should trigger HALF_FULL status
        testBin.setStatus("EMPTY"); // Previous status
        
        when(binSensorRepository.findAll()).thenReturn(Arrays.asList(testSensor));
        when(binSensorRepository.save(any(BinSensor.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(smartBinRepository.findByBinId("BIN001")).thenReturn(Optional.of(testBin));
        when(smartBinRepository.save(any(SmartBin.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        sensorSimulationService.simulateSensorReadings();

        // Assert - No alert should be created for EMPTY to HALF_FULL transition
        verify(alertService, never()).createAlert(any(SmartBin.class));
    }

    @Test
    void testHandleBinStatusChangeAlerts_AlreadyFull() {
        // Arrange - Status is already FULL, no change
        testSensor.setMeasurement(85.0); // This should trigger FULL status
        testBin.setStatus("FULL"); // Already FULL
        
        when(binSensorRepository.findAll()).thenReturn(Arrays.asList(testSensor));
        when(binSensorRepository.save(any(BinSensor.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(smartBinRepository.findByBinId("BIN001")).thenReturn(Optional.of(testBin));
        when(smartBinRepository.save(any(SmartBin.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        sensorSimulationService.simulateSensorReadings();

        // Assert - No alert should be created when already FULL
        verify(alertService, never()).createAlert(any(SmartBin.class));
    }

    // Test determineSensorColor method - Positive Cases
    @Test
    void testDetermineSensorColor_Red() {
        // Act
        String color = sensorSimulationService.determineSensorColor(85.0);

        // Assert
        assertEquals("RED", color);
    }

    @Test
    void testDetermineSensorColor_Blue() {
        // Act
        String color = sensorSimulationService.determineSensorColor(65.0);

        // Assert
        assertEquals("BLUE", color);
    }

    @Test
    void testDetermineSensorColor_Green() {
        // Act
        String color = sensorSimulationService.determineSensorColor(30.0);

        // Assert
        assertEquals("GREEN", color);
    }

    // Test determineSensorColor method - Edge Cases
    @Test
    void testDetermineSensorColor_BoundaryValues() {
        // Test exactly at thresholds
        assertEquals("RED", sensorSimulationService.determineSensorColor(80.0));
        assertEquals("BLUE", sensorSimulationService.determineSensorColor(50.0));
        assertEquals("GREEN", sensorSimulationService.determineSensorColor(0.0));
    }

    // Test calculateBinStatus method - Positive Cases
    @Test
    void testCalculateBinStatus_Full() {
        // Act
        String status = sensorSimulationService.calculateBinStatus(85.0);

        // Assert
        assertEquals("FULL", status);
    }

    @Test
    void testCalculateBinStatus_HalfFull() {
        // Act
        String status = sensorSimulationService.calculateBinStatus(65.0);

        // Assert
        assertEquals("HALF_FULL", status);
    }

    @Test
    void testCalculateBinStatus_Empty() {
        // Act
        String status = sensorSimulationService.calculateBinStatus(30.0);

        // Assert
        assertEquals("EMPTY", status);
    }

    // Test calculateBinStatus method - Edge Cases
    @Test
    void testCalculateBinStatus_BoundaryValues() {
        // Test exactly at thresholds
        assertEquals("FULL", sensorSimulationService.calculateBinStatus(80.0));
        assertEquals("HALF_FULL", sensorSimulationService.calculateBinStatus(50.0));
        assertEquals("EMPTY", sensorSimulationService.calculateBinStatus(0.0));
    }

    // Test constructor
    @Test
    void testConstructor() {
        // Act
        SensorSimulationService service = new SensorSimulationService(binSensorRepository, smartBinRepository, alertService);

        // Assert
        assertNotNull(service);
    }
}