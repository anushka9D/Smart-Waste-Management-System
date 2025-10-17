package com.swms.anushka;

import com.swms.controller.anushka.AlertController;
import com.swms.dto.AlertDTO;
import com.swms.service.anushka.AlertService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertControllerTest {

    @Mock
    private AlertService alertService;

    @InjectMocks
    private AlertController alertController;

    private AlertDTO testAlertDTO1;
    private AlertDTO testAlertDTO2;
    private AlertDTO testAlertDTO3;

    @BeforeEach
    void setUp() {
        // Initialize test data
        testAlertDTO1 = new AlertDTO();
        testAlertDTO1.setAlertId("ALERT-12345678");
        testAlertDTO1.setBinId("B001");
        testAlertDTO1.setLocation("Colombo Fort");
        testAlertDTO1.setAlertTitle("Bin Full Alert");
        testAlertDTO1.setMessage("Bin B001 at Colombo Fort is full and needs collection");
        testAlertDTO1.setCreatedAt(LocalDateTime.now().minusHours(2));
        testAlertDTO1.setIsReviewed("NO");

        testAlertDTO2 = new AlertDTO();
        testAlertDTO2.setAlertId("ALERT-87654321");
        testAlertDTO2.setBinId("B002");
        testAlertDTO2.setLocation("Pettah");
        testAlertDTO2.setAlertTitle("Bin Full Alert");
        testAlertDTO2.setMessage("Bin B002 at Pettah is full and needs collection");
        testAlertDTO2.setCreatedAt(LocalDateTime.now().minusHours(1));
        testAlertDTO2.setIsReviewed("NO");

        testAlertDTO3 = new AlertDTO();
        testAlertDTO3.setAlertId("ALERT-11223344");
        testAlertDTO3.setBinId("B003");
        testAlertDTO3.setLocation("Bambalapitiya");
        testAlertDTO3.setAlertTitle("Bin Full Alert");
        testAlertDTO3.setMessage("Bin B003 at Bambalapitiya is full and needs collection");
        testAlertDTO3.setCreatedAt(LocalDateTime.now().minusHours(3));
        testAlertDTO3.setIsReviewed("YES");
    }

    @Test
    void testGetUnreviewedAlerts_Success() {
        // Arrange
        List<AlertDTO> unreviewedAlerts = Arrays.asList(testAlertDTO1, testAlertDTO2);
        when(alertService.getAllUnreviewedAlerts()).thenReturn(unreviewedAlerts);

        // Act
        ResponseEntity<List<AlertDTO>> response = alertController.getUnreviewedAlerts();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("NO", response.getBody().get(0).getIsReviewed());
        assertEquals("NO", response.getBody().get(1).getIsReviewed());
        verify(alertService, times(1)).getAllUnreviewedAlerts();
    }

    @Test
    void testGetUnreviewedAlerts_EmptyList() {
        // Arrange
        when(alertService.getAllUnreviewedAlerts()).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<AlertDTO>> response = alertController.getUnreviewedAlerts();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(alertService, times(1)).getAllUnreviewedAlerts();
    }

    @Test
    void testGetUnreviewedAlerts_SingleAlert() {
        // Arrange
        List<AlertDTO> unreviewedAlerts = Collections.singletonList(testAlertDTO1);
        when(alertService.getAllUnreviewedAlerts()).thenReturn(unreviewedAlerts);

        // Act
        ResponseEntity<List<AlertDTO>> response = alertController.getUnreviewedAlerts();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("B001", response.getBody().get(0).getBinId());
        assertEquals("Colombo Fort", response.getBody().get(0).getLocation());
        verify(alertService, times(1)).getAllUnreviewedAlerts();
    }

    @Test
    void testGetAllAlerts_Success() {
        // Arrange
        List<AlertDTO> allAlerts = Arrays.asList(testAlertDTO1, testAlertDTO2, testAlertDTO3);
        when(alertService.getAllAlerts()).thenReturn(allAlerts);

        // Act
        ResponseEntity<List<AlertDTO>> response = alertController.getAllAlerts();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().size());
        verify(alertService, times(1)).getAllAlerts();
    }

    @Test
    void testGetAllAlerts_EmptyList() {
        // Arrange
        when(alertService.getAllAlerts()).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<AlertDTO>> response = alertController.getAllAlerts();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(alertService, times(1)).getAllAlerts();
    }

    @Test
    void testGetAllAlerts_ContainsBothReviewedAndUnreviewed() {
        // Arrange
        List<AlertDTO> allAlerts = Arrays.asList(testAlertDTO1, testAlertDTO3);
        when(alertService.getAllAlerts()).thenReturn(allAlerts);

        // Act
        ResponseEntity<List<AlertDTO>> response = alertController.getAllAlerts();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        
        // Verify it contains both reviewed and unreviewed alerts
        boolean hasReviewed = response.getBody().stream()
                .anyMatch(alert -> "YES".equals(alert.getIsReviewed()));
        boolean hasUnreviewed = response.getBody().stream()
                .anyMatch(alert -> "NO".equals(alert.getIsReviewed()));
        
        assertTrue(hasReviewed);
        assertTrue(hasUnreviewed);
        verify(alertService, times(1)).getAllAlerts();
    }

    @Test
    void testMarkAlertAsReviewed_Success() {
        // Arrange
        String binId = "B001";
        doNothing().when(alertService).markAlertAsReviewed(binId);

        // Act
        ResponseEntity<String> response = alertController.markAlertAsReviewed(binId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Alert marked as reviewed", response.getBody());
        verify(alertService, times(1)).markAlertAsReviewed(binId);
    }

    @Test
    void testMarkAlertAsReviewed_DifferentBinIds() {
        // Arrange
        String binId1 = "B001";
        String binId2 = "B002";
        String binId3 = "B003";
        doNothing().when(alertService).markAlertAsReviewed(anyString());

        // Act
        ResponseEntity<String> response1 = alertController.markAlertAsReviewed(binId1);
        ResponseEntity<String> response2 = alertController.markAlertAsReviewed(binId2);
        ResponseEntity<String> response3 = alertController.markAlertAsReviewed(binId3);

        // Assert
        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());
        
        assertEquals("Alert marked as reviewed", response1.getBody());
        assertEquals("Alert marked as reviewed", response2.getBody());
        assertEquals("Alert marked as reviewed", response3.getBody());
        
        verify(alertService, times(1)).markAlertAsReviewed(binId1);
        verify(alertService, times(1)).markAlertAsReviewed(binId2);
        verify(alertService, times(1)).markAlertAsReviewed(binId3);
    }

    @Test
    void testMarkAlertAsReviewed_ServiceThrowsException() {
        // Arrange
        String binId = "B999";
        String errorMessage = "Alert not found for bin: " + binId;
        doThrow(new RuntimeException(errorMessage))
                .when(alertService).markAlertAsReviewed(binId);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            alertController.markAlertAsReviewed(binId);
        });

        assertEquals(errorMessage, exception.getMessage());
        verify(alertService, times(1)).markAlertAsReviewed(binId);
    }

    @Test
    void testMarkAlertAsReviewed_VerifyServiceInteraction() {
        // Arrange
        String binId = "B001";
        doNothing().when(alertService).markAlertAsReviewed(binId);

        // Act
        alertController.markAlertAsReviewed(binId);

        // Assert
        verify(alertService, times(1)).markAlertAsReviewed(binId);
        verifyNoMoreInteractions(alertService);
    }

    @Test
    void testGetUnreviewedAlerts_VerifyAlertDetails() {
        // Arrange
        List<AlertDTO> unreviewedAlerts = Collections.singletonList(testAlertDTO1);
        when(alertService.getAllUnreviewedAlerts()).thenReturn(unreviewedAlerts);

        // Act
        ResponseEntity<List<AlertDTO>> response = alertController.getUnreviewedAlerts();

        // Assert
        assertNotNull(response.getBody());
        AlertDTO returnedAlert = response.getBody().get(0);
        
        assertEquals("ALERT-12345678", returnedAlert.getAlertId());
        assertEquals("B001", returnedAlert.getBinId());
        assertEquals("Colombo Fort", returnedAlert.getLocation());
        assertEquals("Bin Full Alert", returnedAlert.getAlertTitle());
        assertTrue(returnedAlert.getMessage().contains("full and needs collection"));
        assertEquals("NO", returnedAlert.getIsReviewed());
        assertNotNull(returnedAlert.getCreatedAt());
        
        verify(alertService, times(1)).getAllUnreviewedAlerts();
    }

    @Test
    void testGetAllAlerts_VerifyOrderAndContent() {
        // Arrange
        List<AlertDTO> allAlerts = Arrays.asList(testAlertDTO2, testAlertDTO1, testAlertDTO3);
        when(alertService.getAllAlerts()).thenReturn(allAlerts);

        // Act
        ResponseEntity<List<AlertDTO>> response = alertController.getAllAlerts();

        // Assert
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().size());
        
        // Verify all alert IDs are present
        List<String> alertIds = response.getBody().stream()
                .map(AlertDTO::getAlertId)
                .toList();
        
        assertTrue(alertIds.contains("ALERT-12345678"));
        assertTrue(alertIds.contains("ALERT-87654321"));
        assertTrue(alertIds.contains("ALERT-11223344"));
        
        verify(alertService, times(1)).getAllAlerts();
    }
}