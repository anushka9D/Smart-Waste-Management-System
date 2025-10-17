package com.swms.anushka;

import com.swms.model.BinSensor;
import com.swms.repository.BinSensorRepository;
import com.swms.service.anushka.BinSensorService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BinSensorService Unit Tests")
class BinSensorServiceTest {

    @Mock
    private BinSensorRepository binSensorRepository;

    @InjectMocks
    private BinSensorService binSensorService;

    private BinSensor testBinSensor;

    @BeforeEach
    void setUp() {
        testBinSensor = new BinSensor();
        testBinSensor.setSensorId("SENSOR-12345678");
        testBinSensor.setBinId("B001");
        testBinSensor.setType("WORKING");
        testBinSensor.setColor("GREEN");
        testBinSensor.setMeasurement(0.0);
        testBinSensor.setLastReading(LocalDateTime.now());
        testBinSensor.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create bin sensor successfully")
    void testCreateBinSensor_Success() {
        // Arrange
        String binId = "B001";
        when(binSensorRepository.save(any(BinSensor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        binSensorService.createBinSensor(binId);

        // Assert
        ArgumentCaptor<BinSensor> sensorCaptor = ArgumentCaptor.forClass(BinSensor.class);
        verify(binSensorRepository, times(1)).save(sensorCaptor.capture());

        BinSensor savedSensor = sensorCaptor.getValue();
        assertThat(savedSensor.getBinId()).isEqualTo(binId);
        assertThat(savedSensor.getType()).isEqualTo("WORKING");
        assertThat(savedSensor.getColor()).isEqualTo("GREEN");
        assertThat(savedSensor.getMeasurement()).isEqualTo(0.0);
        assertThat(savedSensor.getSensorId()).startsWith("SENSOR-");
    }

    @Test
    @DisplayName("Should update sensor measurement successfully")
    void testUpdateSensorMeasurement_Success() {
        // Arrange
        String binId = "B001";
        Double measurement = 75.0;
        when(binSensorRepository.findByBinId(binId)).thenReturn(Optional.of(testBinSensor));
        when(binSensorRepository.save(any(BinSensor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        binSensorService.updateSensorMeasurement(binId, measurement);

        // Assert
        ArgumentCaptor<BinSensor> sensorCaptor = ArgumentCaptor.forClass(BinSensor.class);
        verify(binSensorRepository, times(1)).save(sensorCaptor.capture());

        BinSensor updatedSensor = sensorCaptor.getValue();
        assertThat(updatedSensor.getMeasurement()).isEqualTo(measurement);
        assertThat(updatedSensor.getType()).isEqualTo("WORKING");
        assertThat(updatedSensor.getColor()).isEqualTo("BLUE"); // 75.0 >= 50.0
        assertThat(updatedSensor.getLastReading()).isNotNull();
    }

    @Test
    @DisplayName("Should mark sensor as faulty successfully")
    void testMarkSensorAsFaulty_Success() {
        // Arrange
        String binId = "B001";
        when(binSensorRepository.findByBinId(binId)).thenReturn(Optional.of(testBinSensor));
        when(binSensorRepository.save(any(BinSensor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        binSensorService.markSensorAsFaulty(binId);

        // Assert
        ArgumentCaptor<BinSensor> sensorCaptor = ArgumentCaptor.forClass(BinSensor.class);
        verify(binSensorRepository, times(1)).save(sensorCaptor.capture());

        BinSensor updatedSensor = sensorCaptor.getValue();
        assertThat(updatedSensor.getType()).isEqualTo("FAULTY");
        assertThat(updatedSensor.getColor()).isEqualTo("GRAY");
    }

    @Test
    @DisplayName("Should reset sensor measurement successfully")
    void testResetSensorMeasurement_Success() {
        // Arrange
        String binId = "B001";
        testBinSensor.setMeasurement(75.0);
        testBinSensor.setType("FAULTY");
        testBinSensor.setColor("RED");
        when(binSensorRepository.findByBinId(binId)).thenReturn(Optional.of(testBinSensor));
        when(binSensorRepository.save(any(BinSensor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        binSensorService.resetSensorMeasurement(binId);

        // Assert
        ArgumentCaptor<BinSensor> sensorCaptor = ArgumentCaptor.forClass(BinSensor.class);
        verify(binSensorRepository, times(1)).save(sensorCaptor.capture());

        BinSensor updatedSensor = sensorCaptor.getValue();
        assertThat(updatedSensor.getMeasurement()).isEqualTo(0.0);
        assertThat(updatedSensor.getType()).isEqualTo("WORKING");
        assertThat(updatedSensor.getColor()).isEqualTo("GREEN");
        assertThat(updatedSensor.getLastReading()).isNotNull();
    }

    @Test
    @DisplayName("Should determine sensor color correctly for RED (>= 80)")
    void testDetermineSensorColor_Red() {
        // Arrange
        String binId = "B001";
        when(binSensorRepository.findByBinId(binId)).thenReturn(Optional.of(testBinSensor));
        when(binSensorRepository.save(any(BinSensor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        binSensorService.updateSensorMeasurement(binId, 85.0);

        // Assert
        ArgumentCaptor<BinSensor> sensorCaptor = ArgumentCaptor.forClass(BinSensor.class);
        verify(binSensorRepository, times(1)).save(sensorCaptor.capture());
        assertThat(sensorCaptor.getValue().getColor()).isEqualTo("RED");
    }

    @Test
    @DisplayName("Should determine sensor color correctly for BLUE (50-79.99)")
    void testDetermineSensorColor_Blue() {
        // Arrange
        String binId = "B001";
        when(binSensorRepository.findByBinId(binId)).thenReturn(Optional.of(testBinSensor));
        when(binSensorRepository.save(any(BinSensor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        binSensorService.updateSensorMeasurement(binId, 65.0);

        // Assert
        ArgumentCaptor<BinSensor> sensorCaptor = ArgumentCaptor.forClass(BinSensor.class);
        verify(binSensorRepository, times(1)).save(sensorCaptor.capture());
        assertThat(sensorCaptor.getValue().getColor()).isEqualTo("BLUE");
    }

    @Test
    @DisplayName("Should determine sensor color correctly for GREEN (< 50)")
    void testDetermineSensorColor_Green() {
        // Arrange
        String binId = "B001";
        when(binSensorRepository.findByBinId(binId)).thenReturn(Optional.of(testBinSensor));
        when(binSensorRepository.save(any(BinSensor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        binSensorService.updateSensorMeasurement(binId, 25.0);

        // Assert
        ArgumentCaptor<BinSensor> sensorCaptor = ArgumentCaptor.forClass(BinSensor.class);
        verify(binSensorRepository, times(1)).save(sensorCaptor.capture());
        assertThat(sensorCaptor.getValue().getColor()).isEqualTo("GREEN");
    }

    @Test
    @DisplayName("Should throw exception when updating measurement for non-existent sensor")
    void testUpdateSensorMeasurement_SensorNotFound() {
        // Arrange
        String binId = "INVALID";
        when(binSensorRepository.findByBinId(binId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> binSensorService.updateSensorMeasurement(binId, 50.0))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Sensor not found for bin: " + binId);

        verify(binSensorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when marking faulty for non-existent sensor")
    void testMarkSensorAsFaulty_SensorNotFound() {
        // Arrange
        String binId = "INVALID";
        when(binSensorRepository.findByBinId(binId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> binSensorService.markSensorAsFaulty(binId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Sensor not found for bin: " + binId);

        verify(binSensorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when resetting measurement for non-existent sensor")
    void testResetSensorMeasurement_SensorNotFound() {
        // Arrange
        String binId = "INVALID";
        when(binSensorRepository.findByBinId(binId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> binSensorService.resetSensorMeasurement(binId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Sensor not found for bin: " + binId);

        verify(binSensorRepository, never()).save(any());
    }
}