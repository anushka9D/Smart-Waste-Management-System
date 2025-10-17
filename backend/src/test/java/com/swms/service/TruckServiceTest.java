package com.swms.service;

import com.swms.model.Truck;
import com.swms.repository.TruckRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TruckServiceTest {

    @Mock
    private TruckRepository truckRepository;

    @InjectMocks
    private TruckService truckService;

    private Truck testTruck;

    @BeforeEach
    void setUp() {
        // Initialize test data
        testTruck = new Truck();
        testTruck.setTruckId("truck1");
        testTruck.setRegistrationNumber("TRK001");
        testTruck.setCapacity(1000.0);
        testTruck.setCurrentStatus("AVAILABLE");
        testTruck.setAssignedDriverId(null);
        testTruck.setCreatedAt(LocalDateTime.now());
        testTruck.setUpdatedAt(LocalDateTime.now());
    }

    // Test getAllTrucks method - Positive Cases
    @Test
    void testGetAllTrucks_Positive() {
        // Arrange
        List<Truck> trucks = Arrays.asList(testTruck);
        when(truckRepository.findAll()).thenReturn(trucks);

        // Act
        List<Truck> result = truckService.getAllTrucks();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTruck.getTruckId(), result.get(0).getTruckId());
        verify(truckRepository).findAll();
    }

    // Test getAllTrucks method - Edge Cases
    @Test
    void testGetAllTrucks_EmptyList() {
        // Arrange
        when(truckRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Truck> result = truckService.getAllTrucks();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(truckRepository).findAll();
    }

    // Test getTruckById method - Positive Cases
    @Test
    void testGetTruckById_Positive() {
        // Arrange
        when(truckRepository.findById("truck1")).thenReturn(Optional.of(testTruck));

        // Act
        Optional<Truck> result = truckService.getTruckById("truck1");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testTruck.getTruckId(), result.get().getTruckId());
        verify(truckRepository).findById("truck1");
    }

    // Test getTruckById method - Negative Cases
    @Test
    void testGetTruckById_NotFound() {
        // Arrange
        when(truckRepository.findById("invalidTruck")).thenReturn(Optional.empty());

        // Act
        Optional<Truck> result = truckService.getTruckById("invalidTruck");

        // Assert
        assertFalse(result.isPresent());
        verify(truckRepository).findById("invalidTruck");
    }

    // Test getTruckById method - Edge Cases
    @Test
    void testGetTruckById_NullId() {
        // Arrange
        when(truckRepository.findById(null)).thenReturn(Optional.empty());

        // Act
        Optional<Truck> result = truckService.getTruckById(null);

        // Assert
        assertFalse(result.isPresent());
        verify(truckRepository).findById(null);
    }

    // Test saveTruck method - Positive Cases
    @Test
    void testSaveTruck_Positive() {
        // Arrange
        Truck newTruck = new Truck();
        newTruck.setRegistrationNumber("TRK002");
        newTruck.setCapacity(1500.0);
        newTruck.setCurrentStatus("AVAILABLE");
        newTruck.setCreatedAt(null); // Should be set by service

        Truck savedTruck = new Truck();
        savedTruck.setTruckId("truck2");
        savedTruck.setRegistrationNumber("TRK002");
        savedTruck.setCapacity(1500.0);
        savedTruck.setCurrentStatus("AVAILABLE");
        savedTruck.setCreatedAt(LocalDateTime.now());
        savedTruck.setUpdatedAt(LocalDateTime.now());

        when(truckRepository.save(any(Truck.class))).thenReturn(savedTruck);

        // Act
        String result = truckService.saveTruck(newTruck);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Truck created with ID:"));
        assertNotNull(newTruck.getCreatedAt());
        assertNotNull(newTruck.getUpdatedAt());
        verify(truckRepository).save(newTruck);
    }

    // Test saveTruck method - Edge Cases
    @Test
    void testSaveTruck_WithExistingCreatedAt() {
        // Arrange
        LocalDateTime existingCreatedAt = LocalDateTime.now().minusDays(1);
        testTruck.setCreatedAt(existingCreatedAt);

        when(truckRepository.save(any(Truck.class))).thenReturn(testTruck);

        // Act
        String result = truckService.saveTruck(testTruck);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Truck created with ID:"));
        assertEquals(existingCreatedAt, testTruck.getCreatedAt()); // Should not be changed
        assertNotNull(testTruck.getUpdatedAt());
        verify(truckRepository).save(testTruck);
    }

    @Test
    void testSaveTruck_NullTruck() {
        // Act & Assert
        assertThrows(Exception.class, () -> {
            truckService.saveTruck(null);
        });
    }

    // Test updateTruck method - Positive Cases
    @Test
    void testUpdateTruck_Positive() {
        // Arrange
        Truck updatedTruck = new Truck();
        updatedTruck.setRegistrationNumber("TRK001-UPDATED");
        updatedTruck.setCapacity(2000.0);
        updatedTruck.setCurrentStatus("IN_USE");

        when(truckRepository.findById("truck1")).thenReturn(Optional.of(testTruck));
        when(truckRepository.save(any(Truck.class))).thenReturn(testTruck);

        // Act
        String result = truckService.updateTruck("truck1", updatedTruck);

        // Assert
        assertEquals("Truck updated successfully", result);
        assertEquals("truck1", updatedTruck.getTruckId()); // ID should be preserved
        assertEquals("TRK001-UPDATED", updatedTruck.getRegistrationNumber());
        assertEquals(2000.0, updatedTruck.getCapacity());
        assertEquals("IN_USE", updatedTruck.getCurrentStatus());
        assertNotNull(updatedTruck.getUpdatedAt());
        verify(truckRepository).findById("truck1");
        verify(truckRepository).save(updatedTruck);
    }

    // Test updateTruck method - Negative Cases
    @Test
    void testUpdateTruck_NotFound() {
        // Arrange
        when(truckRepository.findById("invalidTruck")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            truckService.updateTruck("invalidTruck", testTruck);
        });

        assertEquals("Truck not found with id: invalidTruck", exception.getMessage());
        verify(truckRepository).findById("invalidTruck");
        verify(truckRepository, never()).save(any());
    }

    // Test updateTruck method - Edge Cases
    @Test
    void testUpdateTruck_NullId() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            truckService.updateTruck(null, testTruck);
        });

        assertTrue(exception.getMessage().contains("Truck not found with id:"));
        verify(truckRepository).findById(null);
        verify(truckRepository, never()).save(any());
    }

    @Test
    void testUpdateTruck_NullTruck() {
        // Arrange
        when(truckRepository.findById("truck1")).thenReturn(Optional.of(testTruck));

        // Act & Assert
        assertThrows(Exception.class, () -> {
            truckService.updateTruck("truck1", null);
        });
    }

    // Test deleteTruck method - Positive Cases
    @Test
    void testDeleteTruck_Positive() {
        // Arrange
        when(truckRepository.existsById("truck1")).thenReturn(true);

        // Act
        String result = truckService.deleteTruck("truck1");

        // Assert
        assertEquals("Truck deleted successfully", result);
        verify(truckRepository).existsById("truck1");
        verify(truckRepository).deleteById("truck1");
    }

    // Test deleteTruck method - Negative Cases
    @Test
    void testDeleteTruck_NotFound() {
        // Arrange
        when(truckRepository.existsById("invalidTruck")).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            truckService.deleteTruck("invalidTruck");
        });

        assertEquals("Truck not found with id: invalidTruck", exception.getMessage());
        verify(truckRepository).existsById("invalidTruck");
        verify(truckRepository, never()).deleteById(anyString());
    }

    // Test deleteTruck method - Edge Cases
    @Test
    void testDeleteTruck_NullId() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            truckService.deleteTruck(null);
        });

        assertTrue(exception.getMessage().contains("Truck not found with id:"));
        verify(truckRepository).existsById(null);
        verify(truckRepository, never()).deleteById(anyString());
    }

    // Error case tests
    @Test
    void testGetAllTrucks_RepositoryException() {
        // Arrange
        when(truckRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            truckService.getAllTrucks();
        });

        verify(truckRepository).findAll();
    }

    @Test
    void testSaveTruck_RepositoryException() {
        // Arrange
        when(truckRepository.save(any(Truck.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            truckService.saveTruck(testTruck);
        });

        verify(truckRepository).save(any(Truck.class));
    }

    @Test
    void testUpdateTruck_RepositoryException() {
        // Arrange
        when(truckRepository.findById("truck1")).thenReturn(Optional.of(testTruck));
        when(truckRepository.save(any(Truck.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            truckService.updateTruck("truck1", testTruck);
        });

        verify(truckRepository).findById("truck1");
        verify(truckRepository).save(any(Truck.class));
    }

    @Test
    void testDeleteTruck_RepositoryException() {
        // Arrange
        when(truckRepository.existsById("truck1")).thenReturn(true);
        doThrow(new RuntimeException("Database error")).when(truckRepository).deleteById("truck1");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            truckService.deleteTruck("truck1");
        });

        verify(truckRepository).existsById("truck1");
        verify(truckRepository).deleteById("truck1");
    }
}