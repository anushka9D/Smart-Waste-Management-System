package com.swms.anushka;

import com.swms.controller.anushka.SmartBinController;
import com.swms.dto.*;
import com.swms.service.anushka.SmartBinService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SmartBinControllerTest {

    @Mock
    private SmartBinService smartBinService;

    @InjectMocks
    private SmartBinController smartBinController;

    private SmartBinDTO testSmartBinDTO;
    private CreateSmartBinRequest createRequest;
    private UpdateBinLevelRequest updateRequest;

    @BeforeEach
    void setUp() {
        // Initialize test data
        GPSLocationDTO coordinates = new GPSLocationDTO();
        coordinates.setLatitude(6.9271);
        coordinates.setLongitude(79.8612);

        testSmartBinDTO = new SmartBinDTO();
        testSmartBinDTO.setBinId("B001");
        testSmartBinDTO.setLocation("Colombo Fort");
        testSmartBinDTO.setCoordinates(coordinates);
        testSmartBinDTO.setCurrentLevel(30.0);
        testSmartBinDTO.setCapacity(100.0);

        testSmartBinDTO.setStatus("EMPTY");
        testSmartBinDTO.setLastCollected(LocalDateTime.now());
        testSmartBinDTO.setBinColor("GREEN");

        createRequest = new CreateSmartBinRequest();
        createRequest.setLocation("Colombo Fort");
        createRequest.setLatitude(6.9271);
        createRequest.setLongitude(79.8612);
        createRequest.setCapacity(100.0);


        updateRequest = new UpdateBinLevelRequest();
        updateRequest.setBinId("B001");
        updateRequest.setCurrentLevel(75.0);
    }

    @Test
    void testCreateSmartBin_Success() {
        // Arrange
        when(smartBinService.createSmartBin(any(CreateSmartBinRequest.class)))
                .thenReturn(testSmartBinDTO);

        // Act
        ResponseEntity<SmartBinDTO> response = smartBinController.createSmartBin(createRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("B001", response.getBody().getBinId());
        assertEquals("Colombo Fort", response.getBody().getLocation());
        verify(smartBinService, times(1)).createSmartBin(any(CreateSmartBinRequest.class));
    }

    @Test
    void testGetSmartBin_Success() {
        // Arrange
        String binId = "B001";
        when(smartBinService.getSmartBinById(binId)).thenReturn(testSmartBinDTO);

        // Act
        ResponseEntity<SmartBinDTO> response = smartBinController.getSmartBin(binId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(binId, response.getBody().getBinId());
        verify(smartBinService, times(1)).getSmartBinById(binId);
    }

    @Test
    void testGetSmartBin_NotFound() {
        // Arrange
        String binId = "B999";
        when(smartBinService.getSmartBinById(binId)).thenReturn(null);

        // Act
        ResponseEntity<SmartBinDTO> response = smartBinController.getSmartBin(binId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
        verify(smartBinService, times(1)).getSmartBinById(binId);
    }

    @Test
    void testGetAllSmartBins_Success() {
        // Arrange
        SmartBinDTO bin2 = new SmartBinDTO();
        bin2.setBinId("B002");
        bin2.setLocation("Pettah");
        bin2.setStatus("FULL");

        List<SmartBinDTO> bins = Arrays.asList(testSmartBinDTO, bin2);
        when(smartBinService.getAllSmartBins()).thenReturn(bins);

        // Act
        ResponseEntity<List<SmartBinDTO>> response = smartBinController.getAllSmartBins();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(smartBinService, times(1)).getAllSmartBins();
    }

    @Test
    void testGetAllSmartBins_EmptyList() {
        // Arrange
        when(smartBinService.getAllSmartBins()).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<SmartBinDTO>> response = smartBinController.getAllSmartBins();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(smartBinService, times(1)).getAllSmartBins();
    }

    @Test
    void testGetFullBins_Success() {
        // Arrange
        testSmartBinDTO.setStatus("FULL");
        testSmartBinDTO.setBinColor("RED");
        List<SmartBinDTO> fullBins = Collections.singletonList(testSmartBinDTO);
        when(smartBinService.getFullBins()).thenReturn(fullBins);

        // Act
        ResponseEntity<List<SmartBinDTO>> response = smartBinController.getFullBins();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("FULL", response.getBody().get(0).getStatus());
        verify(smartBinService, times(1)).getFullBins();
    }

    @Test
    void testGetEmptyBins_Success() {
        // Arrange
        List<SmartBinDTO> emptyBins = Collections.singletonList(testSmartBinDTO);
        when(smartBinService.getEmptyBins()).thenReturn(emptyBins);

        // Act
        ResponseEntity<List<SmartBinDTO>> response = smartBinController.getEmptyBins();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("EMPTY", response.getBody().get(0).getStatus());
        verify(smartBinService, times(1)).getEmptyBins();
    }

    @Test
    void testGetHalfFullBins_Success() {
        // Arrange
        testSmartBinDTO.setStatus("HALF_FULL");
        testSmartBinDTO.setBinColor("BLUE");
        List<SmartBinDTO> halfFullBins = Collections.singletonList(testSmartBinDTO);
        when(smartBinService.getHalfFullBins()).thenReturn(halfFullBins);

        // Act
        ResponseEntity<List<SmartBinDTO>> response = smartBinController.getHalfFullBins();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("HALF_FULL", response.getBody().get(0).getStatus());
        verify(smartBinService, times(1)).getHalfFullBins();
    }

    @Test
    void testUpdateBinLevel_Success() {
        // Arrange
        testSmartBinDTO.setCurrentLevel(75.0);
        testSmartBinDTO.setStatus("HALF_FULL");
        when(smartBinService.updateBinCurrentLevel(any(UpdateBinLevelRequest.class)))
                .thenReturn(testSmartBinDTO);

        // Act
        ResponseEntity<SmartBinDTO> response = smartBinController.updateBinLevel(updateRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(75.0, response.getBody().getCurrentLevel());
        assertEquals("HALF_FULL", response.getBody().getStatus());
        verify(smartBinService, times(1)).updateBinCurrentLevel(any(UpdateBinLevelRequest.class));
    }

    @Test
    void testUpdateBinLevel_BinNotFound() {
        // Arrange
        when(smartBinService.updateBinCurrentLevel(any(UpdateBinLevelRequest.class)))
                .thenReturn(null);

        // Act
        ResponseEntity<SmartBinDTO> response = smartBinController.updateBinLevel(updateRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
        verify(smartBinService, times(1)).updateBinCurrentLevel(any(UpdateBinLevelRequest.class));
    }

    @Test
    void testMarkBinAsCollected_Success() {
        // Arrange
        String binId = "B001";
        testSmartBinDTO.setCurrentLevel(0.0);
        testSmartBinDTO.setStatus("EMPTY");
        testSmartBinDTO.setBinColor("GREEN");
        when(smartBinService.markBinAsCollected(binId)).thenReturn(testSmartBinDTO);

        // Act
        ResponseEntity<SmartBinDTO> response = smartBinController.markBinAsCollected(binId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0.0, response.getBody().getCurrentLevel());
        assertEquals("EMPTY", response.getBody().getStatus());
        verify(smartBinService, times(1)).markBinAsCollected(binId);
    }

    @Test
    void testMarkBinAsCollected_BinNotFound() {
        // Arrange
        String binId = "B999";
        when(smartBinService.markBinAsCollected(binId)).thenReturn(null);

        // Act
        ResponseEntity<SmartBinDTO> response = smartBinController.markBinAsCollected(binId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
        verify(smartBinService, times(1)).markBinAsCollected(binId);
    }

    @Test
    void testDeleteSmartBin_Success() {
        // Arrange
        String binId = "B001";
        doNothing().when(smartBinService).deleteSmartBin(binId);

        // Act
        ResponseEntity<Void> response = smartBinController.deleteSmartBin(binId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(smartBinService, times(1)).deleteSmartBin(binId);
    }

    @Test
    void testGetBinsByLocation_Success() {
        // Arrange
        String location = "Colombo Fort";
        List<SmartBinDTO> bins = Collections.singletonList(testSmartBinDTO);
        when(smartBinService.getBinsByLocation(location)).thenReturn(bins);

        // Act
        ResponseEntity<List<SmartBinDTO>> response = smartBinController.getBinsByLocation(location);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(location, response.getBody().get(0).getLocation());
        verify(smartBinService, times(1)).getBinsByLocation(location);
    }

    @Test
    void testGetBinsByLocation_EmptyList() {
        // Arrange
        String location = "Unknown Location";
        when(smartBinService.getBinsByLocation(location)).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<SmartBinDTO>> response = smartBinController.getBinsByLocation(location);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(smartBinService, times(1)).getBinsByLocation(location);
    }
}