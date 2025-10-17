package com.swms.anushka;

import com.swms.controller.anushka.BinSensorController;
import com.swms.service.anushka.BinSensorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BinSensorControllerTest {

    @Mock
    private BinSensorService binSensorService;

    @InjectMocks
    private BinSensorController binSensorController;

    private String testBinId;

    @BeforeEach
    void setUp() {
        testBinId = "B001";
    }

    @Test
    void testMarkSensorAsFaulty_Success() {
        // Arrange
        doNothing().when(binSensorService).markSensorAsFaulty(testBinId);

        // Act
        ResponseEntity<String> response = binSensorController.markSensorAsFaulty(testBinId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Sensor marked as faulty", response.getBody());
        verify(binSensorService, times(1)).markSensorAsFaulty(testBinId);
    }

    @Test
    void testMarkSensorAsFaulty_WithDifferentBinId() {
        // Arrange
        String binId = "B999";
        doNothing().when(binSensorService).markSensorAsFaulty(binId);

        // Act
        ResponseEntity<String> response = binSensorController.markSensorAsFaulty(binId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Sensor marked as faulty", response.getBody());
        verify(binSensorService, times(1)).markSensorAsFaulty(binId);
    }

    @Test
    void testMarkSensorAsFaulty_ServiceThrowsException() {
        // Arrange
        String errorMessage = "Sensor not found for bin: " + testBinId;
        doThrow(new RuntimeException(errorMessage))
                .when(binSensorService).markSensorAsFaulty(testBinId);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            binSensorController.markSensorAsFaulty(testBinId);
        });

        assertEquals(errorMessage, exception.getMessage());
        verify(binSensorService, times(1)).markSensorAsFaulty(testBinId);
    }

    @Test
    void testMarkSensorAsFaulty_VerifyServiceInteraction() {
        // Arrange
        doNothing().when(binSensorService).markSensorAsFaulty(testBinId);

        // Act
        binSensorController.markSensorAsFaulty(testBinId);

        // Assert
        verify(binSensorService, times(1)).markSensorAsFaulty(testBinId);
        verifyNoMoreInteractions(binSensorService);
    }

    @Test
    void testMarkSensorAsFaulty_MultipleCalls() {
        // Arrange
        doNothing().when(binSensorService).markSensorAsFaulty(anyString());

        // Act
        ResponseEntity<String> response1 = binSensorController.markSensorAsFaulty("B001");
        ResponseEntity<String> response2 = binSensorController.markSensorAsFaulty("B002");
        ResponseEntity<String> response3 = binSensorController.markSensorAsFaulty("B003");

        // Assert
        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());
        
        verify(binSensorService, times(1)).markSensorAsFaulty("B001");
        verify(binSensorService, times(1)).markSensorAsFaulty("B002");
        verify(binSensorService, times(1)).markSensorAsFaulty("B003");
    }
}