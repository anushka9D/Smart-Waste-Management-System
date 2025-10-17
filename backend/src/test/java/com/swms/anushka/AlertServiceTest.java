package com.swms.anushka;

import com.swms.dto.AlertDTO;
import com.swms.model.Alert;
import com.swms.model.SmartBin;
import com.swms.repository.AlertRepository;
import com.swms.service.anushka.AlertService;

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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AlertService Unit Tests")
class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private AlertService alertService;

    private Alert testAlert;
    private SmartBin testSmartBin;

    @BeforeEach
    void setUp() {
        testAlert = new Alert();
        testAlert.setAlertId("ALERT-12345678");
        testAlert.setBinId("B001");
        testAlert.setLocation("Colombo Central");
        testAlert.setAlertTitle("Bin Full Alert");
        testAlert.setMessage("Bin B001 at Colombo Central is full and needs collection");
        testAlert.setCreatedAt(LocalDateTime.now());
        testAlert.setIsReviewed("NO");

        testSmartBin = new SmartBin();
        testSmartBin.setBinId("B001");
        testSmartBin.setLocation("Colombo Central");
    }

    @Test
    @DisplayName("Should create alert successfully")
    void testCreateAlert_Success() {
        // Arrange
        when(alertRepository.save(any(Alert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        alertService.createAlert(testSmartBin);

        // Assert
        ArgumentCaptor<Alert> alertCaptor = ArgumentCaptor.forClass(Alert.class);
        verify(alertRepository, times(1)).save(alertCaptor.capture());

        Alert savedAlert = alertCaptor.getValue();
        assertThat(savedAlert.getBinId()).isEqualTo("B001");
        assertThat(savedAlert.getLocation()).isEqualTo("Colombo Central");
        assertThat(savedAlert.getAlertTitle()).isEqualTo("Bin Full Alert");
        assertThat(savedAlert.getMessage()).contains("B001");
        assertThat(savedAlert.getMessage()).contains("Colombo Central");
        assertThat(savedAlert.getIsReviewed()).isEqualTo("NO");
        assertThat(savedAlert.getAlertId()).startsWith("ALERT-");
    }

    @Test
    @DisplayName("Should get all unreviewed alerts successfully")
    void testGetAllUnreviewedAlerts_Success() {
        // Arrange
        Alert alert2 = new Alert();
        alert2.setAlertId("ALERT-87654321");
        alert2.setBinId("B002");
        alert2.setLocation("Kandy");
        alert2.setAlertTitle("Bin Full Alert");
        alert2.setMessage("Bin B002 at Kandy is full and needs collection");
        alert2.setCreatedAt(LocalDateTime.now());
        alert2.setIsReviewed("NO");

        when(alertRepository.findUnreviewedAlerts()).thenReturn(Arrays.asList(testAlert, alert2));

        // Act
        java.util.List<AlertDTO> alerts = alertService.getAllUnreviewedAlerts();

        // Assert
        assertThat(alerts).hasSize(2);
        assertThat(alerts.get(0).getBinId()).isEqualTo("B001");
        assertThat(alerts.get(1).getBinId()).isEqualTo("B002");
        verify(alertRepository, times(1)).findUnreviewedAlerts();
    }

    @Test
    @DisplayName("Should get all alerts successfully")
    void testGetAllAlerts_Success() {
        // Arrange
        Alert alert2 = new Alert();
        alert2.setAlertId("ALERT-87654321");
        alert2.setBinId("B002");
        alert2.setLocation("Kandy");
        alert2.setAlertTitle("Bin Full Alert");
        alert2.setMessage("Bin B002 at Kandy is full and needs collection");
        alert2.setCreatedAt(LocalDateTime.now());
        alert2.setIsReviewed("YES");

        when(alertRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Arrays.asList(testAlert, alert2));

        // Act
        java.util.List<AlertDTO> alerts = alertService.getAllAlerts();

        // Assert
        assertThat(alerts).hasSize(2);
        assertThat(alerts.get(0).getBinId()).isEqualTo("B001");
        assertThat(alerts.get(1).getBinId()).isEqualTo("B002");
        verify(alertRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("Should mark alert as reviewed successfully")
    void testMarkAlertAsReviewed_Success() {
        // Arrange
        String binId = "B001";
        when(alertRepository.findByBinId(binId)).thenReturn(Optional.of(testAlert));
        when(alertRepository.save(any(Alert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        alertService.markAlertAsReviewed(binId);

        // Assert
        ArgumentCaptor<Alert> alertCaptor = ArgumentCaptor.forClass(Alert.class);
        verify(alertRepository, times(1)).save(alertCaptor.capture());

        Alert updatedAlert = alertCaptor.getValue();
        assertThat(updatedAlert.getIsReviewed()).isEqualTo("YES");
    }

    @Test
    @DisplayName("Should resolve alert for bin successfully")
    void testResolveAlertForBin_Success() {
        // Arrange
        String binId = "B001";
        when(alertRepository.findByBinId(binId)).thenReturn(Optional.of(testAlert));

        // Act
        alertService.resolveAlertForBin(binId);

        // Assert
        verify(alertRepository, times(1)).findByBinId(binId);
        verify(alertRepository, times(1)).delete(testAlert);
    }

    @Test
    @DisplayName("Should delete alerts by bin ID successfully")
    void testDeleteAlertsByBinId_Success() {
        // Arrange
        String binId = "B001";
        when(alertRepository.findByBinId(binId)).thenReturn(Optional.of(testAlert));

        // Act
        alertService.deleteAlertsByBinId(binId);

        // Assert
        verify(alertRepository, times(1)).findByBinId(binId);
        verify(alertRepository, times(1)).delete(testAlert);
    }

    @Test
    @DisplayName("Should handle resolve alert for non-existent bin")
    void testResolveAlertForBin_AlertNotFound() {
        // Arrange
        String binId = "INVALID";
        when(alertRepository.findByBinId(binId)).thenReturn(Optional.empty());

        // Act
        alertService.resolveAlertForBin(binId);

        // Assert
        verify(alertRepository, times(1)).findByBinId(binId);
        verify(alertRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should handle delete alerts for non-existent bin")
    void testDeleteAlertsByBinId_AlertNotFound() {
        // Arrange
        String binId = "INVALID";
        when(alertRepository.findByBinId(binId)).thenReturn(Optional.empty());

        // Act
        alertService.deleteAlertsByBinId(binId);

        // Assert
        verify(alertRepository, times(1)).findByBinId(binId);
        verify(alertRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should throw exception when marking non-existent alert as reviewed")
    void testMarkAlertAsReviewed_AlertNotFound() {
        // Arrange
        String binId = "INVALID";
        when(alertRepository.findByBinId(binId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> alertService.markAlertAsReviewed(binId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Alert not found for bin: " + binId);

        verify(alertRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return empty list when no unreviewed alerts exist")
    void testGetAllUnreviewedAlerts_EmptyList() {
        // Arrange
        when(alertRepository.findUnreviewedAlerts()).thenReturn(Collections.emptyList());

        // Act
        java.util.List<AlertDTO> alerts = alertService.getAllUnreviewedAlerts();

        // Assert
        assertThat(alerts).isEmpty();
        verify(alertRepository, times(1)).findUnreviewedAlerts();
    }

    @Test
    @DisplayName("Should return empty list when no alerts exist")
    void testGetAllAlerts_EmptyList() {
        // Arrange
        when(alertRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Collections.emptyList());

        // Act
        java.util.List<AlertDTO> alerts = alertService.getAllAlerts();

        // Assert
        assertThat(alerts).isEmpty();
        verify(alertRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }
}