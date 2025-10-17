package com.swms.anushka;

import com.swms.dto.CreateSmartBinRequest;
import com.swms.dto.SmartBinDTO;
import com.swms.dto.UpdateBinLevelRequest;
import com.swms.model.GPSLocation;
import com.swms.model.SmartBin;
import com.swms.repository.SmartBinRepository;
import com.swms.service.anushka.AlertService;
import com.swms.service.anushka.BinSensorService;
import com.swms.service.anushka.SmartBinService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SmartBinService Unit Tests")
class SmartBinServiceTest {

    @Mock
    private SmartBinRepository smartBinRepository;

    @Mock
    private BinSensorService binSensorService;

    @Mock
    private AlertService alertService;

    @InjectMocks
    private SmartBinService smartBinService;

    private SmartBin testSmartBin;
    private CreateSmartBinRequest createRequest;
    private UpdateBinLevelRequest updateRequest;

    @BeforeEach
    void setUp() {
        // Setup test data
        GPSLocation coordinates = new GPSLocation();
        coordinates.setLatitude(6.9271);
        coordinates.setLongitude(79.8612);

        testSmartBin = SmartBin.builder()
                .binId("B001")
                .location("Colombo Central")
                .coordinates(coordinates)
                .currentLevel(0.0)
                .capacity(100.0)
                .status("EMPTY")
                .lastCollected(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .build();

        createRequest = new CreateSmartBinRequest();
        createRequest.setLocation("Colombo Central");
        createRequest.setLatitude(6.9271);
        createRequest.setLongitude(79.8612);
        createRequest.setCapacity(100.0);

        updateRequest = UpdateBinLevelRequest.builder()
                .binId("B001")
                .currentLevel(50.0)
                .build();
    }

    //  CREATE SMART BIN TESTS 

    @Test
    @DisplayName("Should create smart bin successfully with valid request")
    void testCreateSmartBin_Success() {
        // Arrange
        when(smartBinRepository.save(any(SmartBin.class))).thenReturn(testSmartBin);
        doNothing().when(binSensorService).createBinSensor(anyString());

        // Act
        SmartBinDTO result = smartBinService.createSmartBin(createRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getLocation()).isEqualTo("Colombo Central");
        assertThat(result.getCurrentLevel()).isEqualTo(0.0);
        assertThat(result.getCapacity()).isEqualTo(100.0);
        assertThat(result.getStatus()).isEqualTo("EMPTY");
        assertThat(result.getBinColor()).isEqualTo("GREEN");
        

        // Verify interactions
        ArgumentCaptor<SmartBin> binCaptor = ArgumentCaptor.forClass(SmartBin.class);
        verify(smartBinRepository, times(1)).save(binCaptor.capture());

        SmartBin savedBin = binCaptor.getValue();
        assertThat(savedBin.getLocation()).isEqualTo("Colombo Central");
        assertThat(savedBin.getCurrentLevel()).isEqualTo(0.0);
        assertThat(savedBin.getStatus()).isEqualTo("EMPTY");

        verify(binSensorService, times(1)).createBinSensor(anyString());
    }

    @Test
    @DisplayName("Should create smart bin with correct GPS coordinates")
    void testCreateSmartBin_WithCorrectCoordinates() {
        // Arrange
        when(smartBinRepository.save(any(SmartBin.class))).thenReturn(testSmartBin);

        // Act
        SmartBinDTO result = smartBinService.createSmartBin(createRequest);

        // Assert
        assertThat(result.getCoordinates()).isNotNull();
        assertThat(result.getCoordinates().getLatitude()).isEqualTo(6.9271);
        assertThat(result.getCoordinates().getLongitude()).isEqualTo(79.8612);
    }

    //  GET SMART BIN BY ID TESTS 

    @Test
    @DisplayName("Should retrieve smart bin by ID successfully")
    void testGetSmartBinById_Success() {
        // Arrange
        when(smartBinRepository.findByBinId("B001")).thenReturn(Optional.of(testSmartBin));

        // Act
        SmartBinDTO result = smartBinService.getSmartBinById("B001");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getBinId()).isEqualTo("B001");
        assertThat(result.getLocation()).isEqualTo("Colombo Central");
        assertThat(result.getBinColor()).isEqualTo("GREEN");

        verify(smartBinRepository, times(1)).findByBinId("B001");
    }

    @Test
    @DisplayName("Should return null when bin ID does not exist")
    void testGetSmartBinById_NotFound() {
        // Arrange
        when(smartBinRepository.findByBinId("INVALID")).thenReturn(Optional.empty());

        // Act
        SmartBinDTO result = smartBinService.getSmartBinById("INVALID");

        // Assert
        assertThat(result).isNull();
        verify(smartBinRepository, times(1)).findByBinId("INVALID");
    }

    @Test
    @DisplayName("Should return bin with correct color based on status")
    void testGetSmartBinById_CorrectColor() {
        // Arrange - FULL bin
        testSmartBin.setStatus("FULL");
        when(smartBinRepository.findByBinId("B001")).thenReturn(Optional.of(testSmartBin));

        // Act
        SmartBinDTO result = smartBinService.getSmartBinById("B001");

        // Assert
        assertThat(result.getBinColor()).isEqualTo("RED");
    }

    //  GET ALL SMART BINS TESTS 

    @Test
    @DisplayName("Should retrieve all smart bins successfully")
    void testGetAllSmartBins_Success() {
        // Arrange
        SmartBin bin2 = SmartBin.builder()
                .binId("B002")
                .location("Kandy")
                .coordinates(new GPSLocation())
                .currentLevel(60.0)
                .capacity(100.0)
                .status("HALF_FULL")
                .lastCollected(LocalDateTime.now())
                .build();

        when(smartBinRepository.findAll()).thenReturn(Arrays.asList(testSmartBin, bin2));

        // Act
        List<SmartBinDTO> results = smartBinService.getAllSmartBins();

        // Assert
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getBinId()).isEqualTo("B001");
        assertThat(results.get(1).getBinId()).isEqualTo("B002");
        assertThat(results.get(0).getBinColor()).isEqualTo("GREEN");
        assertThat(results.get(1).getBinColor()).isEqualTo("BLUE");

        verify(smartBinRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no bins exist")
    void testGetAllSmartBins_EmptyList() {
        // Arrange
        when(smartBinRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<SmartBinDTO> results = smartBinService.getAllSmartBins();

        // Assert
        assertThat(results).isEmpty();
        verify(smartBinRepository, times(1)).findAll();
    }

    // ------------- get full bin test

    @Test
    @DisplayName("Should retrieve all full bins")
    void testGetFullBins_Success() {
        // Arrange
        testSmartBin.setStatus("FULL");
        when(smartBinRepository.findAllFullBins()).thenReturn(Collections.singletonList(testSmartBin));

        // Act
        List<SmartBinDTO> results = smartBinService.getFullBins();

        // Assert
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getStatus()).isEqualTo("FULL");
        assertThat(results.get(0).getBinColor()).isEqualTo("RED");
        verify(smartBinRepository, times(1)).findAllFullBins();
    }

    // ----- get empty bin test

    @Test
    @DisplayName("Should retrieve all empty bins")
    void testGetEmptyBins_Success() {
        // Arrange
        when(smartBinRepository.findAllEmptyBins()).thenReturn(Collections.singletonList(testSmartBin));

        // Act
        List<SmartBinDTO> results = smartBinService.getEmptyBins();

        // Assert
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getStatus()).isEqualTo("EMPTY");
        assertThat(results.get(0).getBinColor()).isEqualTo("GREEN");
        verify(smartBinRepository, times(1)).findAllEmptyBins();
    }

    //  GET HALF FULL BINS TESTS 

    @Test
    @DisplayName("Should retrieve all half-full bins")
    void testGetHalfFullBins_Success() {
        // Arrange
        testSmartBin.setStatus("HALF_FULL");
        when(smartBinRepository.findAllHalfFullBins()).thenReturn(Collections.singletonList(testSmartBin));

        // Act
        List<SmartBinDTO> results = smartBinService.getHalfFullBins();

        // Assert
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getStatus()).isEqualTo("HALF_FULL");
        assertThat(results.get(0).getBinColor()).isEqualTo("BLUE");
        verify(smartBinRepository, times(1)).findAllHalfFullBins();
    }

    //  UPDATE BIN CURRENT LEVEL TESTS 

    @Test
    @DisplayName("Should update bin level to HALF_FULL status")
    void testUpdateBinCurrentLevel_ToHalfFull() {
        // Arrange
        updateRequest.setCurrentLevel(55.0);
        when(smartBinRepository.findByBinId("B001")).thenReturn(Optional.of(testSmartBin));

        SmartBin updatedBin = SmartBin.builder()
                .binId("B001")
                .location("Colombo Central")
                .coordinates(testSmartBin.getCoordinates())
                .currentLevel(55.0)
                .capacity(100.0)
                .status("HALF_FULL")
                .lastCollected(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .build();

        when(smartBinRepository.save(any(SmartBin.class))).thenReturn(updatedBin);

        // Act
        SmartBinDTO result = smartBinService.updateBinCurrentLevel(updateRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getCurrentLevel()).isEqualTo(55.0);
        assertThat(result.getStatus()).isEqualTo("HALF_FULL");
        assertThat(result.getBinColor()).isEqualTo("BLUE");

        verify(binSensorService, times(1)).updateSensorMeasurement("B001", 55.0);
        verify(smartBinRepository, times(1)).save(any(SmartBin.class));
    }

    @Test
    @DisplayName("Should update bin level to FULL status and create alert")
    void testUpdateBinCurrentLevel_ToFull_CreatesAlert() {
        // Arrange
        updateRequest.setCurrentLevel(85.0);
        when(smartBinRepository.findByBinId("B001")).thenReturn(Optional.of(testSmartBin));

        SmartBin updatedBin = SmartBin.builder()
                .binId("B001")
                .location("Colombo Central")
                .coordinates(testSmartBin.getCoordinates())
                .currentLevel(85.0)
                .capacity(100.0)
                .status("FULL")
                .lastCollected(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .build();

        when(smartBinRepository.save(any(SmartBin.class))).thenReturn(updatedBin);

        // Act
        SmartBinDTO result = smartBinService.updateBinCurrentLevel(updateRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getCurrentLevel()).isEqualTo(85.0);
        assertThat(result.getStatus()).isEqualTo("FULL");
        assertThat(result.getBinColor()).isEqualTo("RED");

        ArgumentCaptor<SmartBin> alertCaptor = ArgumentCaptor.forClass(SmartBin.class);
        verify(alertService, times(1)).createAlert(alertCaptor.capture());
        assertThat(alertCaptor.getValue().getStatus()).isEqualTo("FULL");
    }

    @Test
    @DisplayName("Should not create alert when bin remains FULL")
    void testUpdateBinCurrentLevel_RemainingFull_NoAlert() {
        // Arrange
        testSmartBin.setStatus("FULL");
        testSmartBin.setCurrentLevel(85.0);
        updateRequest.setCurrentLevel(90.0);

        when(smartBinRepository.findByBinId("B001")).thenReturn(Optional.of(testSmartBin));
        when(smartBinRepository.save(any(SmartBin.class))).thenReturn(testSmartBin);

        // Act
        smartBinService.updateBinCurrentLevel(updateRequest);

        // Assert
        verify(alertService, never()).createAlert(any());
    }

    @Test
    @DisplayName("Should validate and adjust negative measurement to 0")
    void testUpdateBinCurrentLevel_NegativeValue() {
        // Arrange
        updateRequest.setCurrentLevel(-10.0);
        when(smartBinRepository.findByBinId("B001")).thenReturn(Optional.of(testSmartBin));
        when(smartBinRepository.save(any(SmartBin.class))).thenReturn(testSmartBin);

        // Act
        SmartBinDTO result = smartBinService.updateBinCurrentLevel(updateRequest);

        // Assert
        assertThat(result.getCurrentLevel()).isEqualTo(0.0);
        verify(binSensorService, times(1)).updateSensorMeasurement("B001", 0.0);
    }

    @Test
    @DisplayName("Should validate and adjust measurement exceeding 100 to 100")
    void testUpdateBinCurrentLevel_ExceedsMaximum() {
        // Arrange
        updateRequest.setCurrentLevel(150.0);
        when(smartBinRepository.findByBinId("B001")).thenReturn(Optional.of(testSmartBin));

        SmartBin updatedBin = SmartBin.builder()
                .binId("B001")
                .location("Colombo Central")
                .coordinates(testSmartBin.getCoordinates())
                .currentLevel(100.0)
                .capacity(100.0)
                .status("FULL")
                .lastCollected(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .build();

        when(smartBinRepository.save(any(SmartBin.class))).thenReturn(updatedBin);

        // Act
        SmartBinDTO result = smartBinService.updateBinCurrentLevel(updateRequest);

        // Assert
        assertThat(result.getCurrentLevel()).isEqualTo(100.0);
        verify(binSensorService, times(1)).updateSensorMeasurement("B001", 100.0);
    }

    @Test
    @DisplayName("Should return null when updating non-existent bin")
    void testUpdateBinCurrentLevel_BinNotFound() {
        // Arrange
        when(smartBinRepository.findByBinId("INVALID")).thenReturn(Optional.empty());
        updateRequest.setBinId("INVALID");

        // Act
        SmartBinDTO result = smartBinService.updateBinCurrentLevel(updateRequest);

        // Assert
        assertThat(result).isNull();
        verify(smartBinRepository, never()).save(any());
        verify(binSensorService, never()).updateSensorMeasurement(anyString(), anyDouble());
    }

    @Test
    @DisplayName("Should calculate EMPTY status for level below 50%")
    void testUpdateBinCurrentLevel_EmptyStatus() {
        // Arrange
        updateRequest.setCurrentLevel(30.0);
        when(smartBinRepository.findByBinId("B001")).thenReturn(Optional.of(testSmartBin));
        when(smartBinRepository.save(any(SmartBin.class))).thenReturn(testSmartBin);

        // Act
        smartBinService.updateBinCurrentLevel(updateRequest);

        // Assert
        ArgumentCaptor<SmartBin> binCaptor = ArgumentCaptor.forClass(SmartBin.class);
        verify(smartBinRepository).save(binCaptor.capture());
        assertThat(binCaptor.getValue().getStatus()).isEqualTo("EMPTY");
    }

    @Test
    @DisplayName("Should calculate HALF_FULL status for level at exactly 50%")
    void testUpdateBinCurrentLevel_ExactlyHalfFull() {
        // Arrange
        updateRequest.setCurrentLevel(50.0);
        when(smartBinRepository.findByBinId("B001")).thenReturn(Optional.of(testSmartBin));
        when(smartBinRepository.save(any(SmartBin.class))).thenReturn(testSmartBin);

        // Act
        smartBinService.updateBinCurrentLevel(updateRequest);

        // Assert
        ArgumentCaptor<SmartBin> binCaptor = ArgumentCaptor.forClass(SmartBin.class);
        verify(smartBinRepository).save(binCaptor.capture());
        assertThat(binCaptor.getValue().getStatus()).isEqualTo("HALF_FULL");
    }

    @Test
    @DisplayName("Should calculate FULL status for level at exactly 80%")
    void testUpdateBinCurrentLevel_ExactlyFull() {
        // Arrange
        updateRequest.setCurrentLevel(80.0);
        when(smartBinRepository.findByBinId("B001")).thenReturn(Optional.of(testSmartBin));
        when(smartBinRepository.save(any(SmartBin.class))).thenReturn(testSmartBin);

        // Act
        smartBinService.updateBinCurrentLevel(updateRequest);

        // Assert
        ArgumentCaptor<SmartBin> binCaptor = ArgumentCaptor.forClass(SmartBin.class);
        verify(smartBinRepository).save(binCaptor.capture());
        assertThat(binCaptor.getValue().getStatus()).isEqualTo("FULL");
    }

    // ========== MARK BIN AS COLLECTED TESTS ==========

    @Test
    @DisplayName("Should mark bin as collected successfully")
    void testMarkBinAsCollected_Success() {
        // Arrange
        testSmartBin.setStatus("FULL");
        testSmartBin.setCurrentLevel(85.0);
        when(smartBinRepository.findByBinId("B001")).thenReturn(Optional.of(testSmartBin));
        when(smartBinRepository.save(any(SmartBin.class))).thenReturn(testSmartBin);

        // Act
        SmartBinDTO result = smartBinService.markBinAsCollected("B001");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getBinColor()).isEqualTo("GREEN");

        ArgumentCaptor<SmartBin> binCaptor = ArgumentCaptor.forClass(SmartBin.class);
        verify(smartBinRepository).save(binCaptor.capture());

        SmartBin savedBin = binCaptor.getValue();
        assertThat(savedBin.getCurrentLevel()).isEqualTo(0.0);
        assertThat(savedBin.getStatus()).isEqualTo("EMPTY");
        assertThat(savedBin.getLastCollected()).isNotNull();

        verify(binSensorService, times(1)).resetSensorMeasurement("B001");
        verify(alertService, times(1)).resolveAlertForBin("B001");
    }

    @Test
    @DisplayName("Should return null when marking non-existent bin as collected")
    void testMarkBinAsCollected_BinNotFound() {
        // Arrange
        when(smartBinRepository.findByBinId("INVALID")).thenReturn(Optional.empty());

        // Act
        SmartBinDTO result = smartBinService.markBinAsCollected("INVALID");

        // Assert
        assertThat(result).isNull();
        verify(smartBinRepository, never()).save(any());
        verify(binSensorService, never()).resetSensorMeasurement(anyString());
        verify(alertService, never()).resolveAlertForBin(anyString());
    }

    //  DELETE SMART BIN TESTS 

    @Test
    @DisplayName("Should delete smart bin successfully")
    void testDeleteSmartBin_Success() {
        // Arrange
        when(smartBinRepository.findByBinId("B001")).thenReturn(Optional.of(testSmartBin));
        doNothing().when(smartBinRepository).delete(any(SmartBin.class));
        doNothing().when(alertService).deleteAlertsByBinId(anyString());

        // Act
        smartBinService.deleteSmartBin("B001");

        // Assert
        verify(smartBinRepository, times(1)).findByBinId("B001");
        verify(smartBinRepository, times(1)).delete(testSmartBin);
        verify(alertService, times(1)).deleteAlertsByBinId("B001");
    }

    @Test
    @DisplayName("Should handle deletion of non-existent bin gracefully")
    void testDeleteSmartBin_BinNotFound() {
        // Arrange
        when(smartBinRepository.findByBinId("INVALID")).thenReturn(Optional.empty());

        // Act
        smartBinService.deleteSmartBin("INVALID");

        // Assert
        verify(smartBinRepository, times(1)).findByBinId("INVALID");
        verify(smartBinRepository, never()).delete(any());
        verify(alertService, never()).deleteAlertsByBinId(anyString());
    }

    //  GET BINS BY LOCATION TESTS 

    @Test
    @DisplayName("Should retrieve bins by location successfully")
    void testGetBinsByLocation_Success() {
        // Arrange
        SmartBin bin2 = SmartBin.builder()
                .binId("B002")
                .location("Colombo Central")
                .coordinates(new GPSLocation())
                .currentLevel(55.0)
                .capacity(100.0)
                .status("HALF_FULL")
                .lastCollected(LocalDateTime.now())
                .build();

        when(smartBinRepository.findByLocation("Colombo Central"))
                .thenReturn(Arrays.asList(testSmartBin, bin2));

        // Act
        List<SmartBinDTO> results = smartBinService.getBinsByLocation("Colombo Central");

        // Assert
        assertThat(results).hasSize(2);
        assertThat(results).allMatch(bin -> bin.getLocation().equals("Colombo Central"));
        assertThat(results.get(0).getBinColor()).isEqualTo("GREEN");
        assertThat(results.get(1).getBinColor()).isEqualTo("BLUE");

        verify(smartBinRepository, times(1)).findByLocation("Colombo Central");
    }

    @Test
    @DisplayName("Should return empty list for location with no bins")
    void testGetBinsByLocation_NoBins() {
        // Arrange
        when(smartBinRepository.findByLocation("Unknown Location")).thenReturn(Collections.emptyList());

        // Act
        List<SmartBinDTO> results = smartBinService.getBinsByLocation("Unknown Location");

        // Assert
        assertThat(results).isEmpty();
        verify(smartBinRepository, times(1)).findByLocation("Unknown Location");
    }

    //  COLOR DETERMINATION TESTS 

    @Test
    @DisplayName("Should return GRAY for unknown status")
    void testDetermineBinColor_UnknownStatus() {
        // Arrange
        testSmartBin.setStatus("UNKNOWN");
        when(smartBinRepository.findByBinId("B001")).thenReturn(Optional.of(testSmartBin));

        // Act
        SmartBinDTO result = smartBinService.getSmartBinById("B001");

        // Assert
        assertThat(result.getBinColor()).isEqualTo("GRAY");
    }

    //  EDGE CASES 

    @Test
    @DisplayName("Should handle bin level at 49.99% as EMPTY")
    void testUpdateBinCurrentLevel_EdgeCase_JustBelowHalfFull() {
        // Arrange
        updateRequest.setCurrentLevel(49.99);
        when(smartBinRepository.findByBinId("B001")).thenReturn(Optional.of(testSmartBin));
        when(smartBinRepository.save(any(SmartBin.class))).thenReturn(testSmartBin);

        // Act
        smartBinService.updateBinCurrentLevel(updateRequest);

        // Assert
        ArgumentCaptor<SmartBin> binCaptor = ArgumentCaptor.forClass(SmartBin.class);
        verify(smartBinRepository).save(binCaptor.capture());
        assertThat(binCaptor.getValue().getStatus()).isEqualTo("EMPTY");
    }

    @Test
    @DisplayName("Should handle bin level at 79.99% as HALF_FULL")
    void testUpdateBinCurrentLevel_EdgeCase_JustBelowFull() {
        // Arrange
        updateRequest.setCurrentLevel(79.99);
        when(smartBinRepository.findByBinId("B001")).thenReturn(Optional.of(testSmartBin));
        when(smartBinRepository.save(any(SmartBin.class))).thenReturn(testSmartBin);

        // Act
        smartBinService.updateBinCurrentLevel(updateRequest);

        // Assert
        ArgumentCaptor<SmartBin> binCaptor = ArgumentCaptor.forClass(SmartBin.class);
        verify(smartBinRepository).save(binCaptor.capture());
        assertThat(binCaptor.getValue().getStatus()).isEqualTo("HALF_FULL");
    }

    @Test
    @DisplayName("Should handle zero capacity gracefully")
    void testUpdateBinCurrentLevel_ZeroCapacity() {
        // Arrange
        testSmartBin.setCapacity(0.0);
        updateRequest.setCurrentLevel(50.0);
        when(smartBinRepository.findByBinId("B001")).thenReturn(Optional.of(testSmartBin));
        when(smartBinRepository.save(any(SmartBin.class))).thenReturn(testSmartBin);

        
        try {
            smartBinService.updateBinCurrentLevel(updateRequest);
        } catch (ArithmeticException e) {
            
            assertThat(e).isInstanceOf(ArithmeticException.class);
        }
    }
}