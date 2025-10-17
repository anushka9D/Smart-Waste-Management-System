package com.swms.service;

import com.swms.dto.DriverRequest;
import com.swms.model.Driver;
import com.swms.repository.DriverRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DriverService driverService;

    private Driver testDriver;
    private DriverRequest testDriverRequest;

    @BeforeEach
    void setUp() {
        // Initialize test data
        testDriver = new Driver();
        testDriver.setUserId("driver1");
        testDriver.setName("John Doe");
        testDriver.setEmail("john.doe@example.com");
        testDriver.setPhone("1234567890");
        testDriver.setPassword("encodedPassword");
        testDriver.setUserType("DRIVER");
        testDriver.setLicenseNumber("DL123456");
        testDriver.setVehicleType("Truck");
        testDriver.setAvailability(true);
        testDriver.setCreatedAt(LocalDateTime.now());
        testDriver.setUpdatedAt(LocalDateTime.now());

        testDriverRequest = new DriverRequest();
        testDriverRequest.setName("John Doe");
        testDriverRequest.setEmail("john.doe@example.com");
        testDriverRequest.setPhone("1234567890");
        testDriverRequest.setPassword("password123");
        testDriverRequest.setLicenseNumber("DL123456");
        testDriverRequest.setVehicleType("Truck");
    }

    // Test saveDriver method - Positive Cases
    @Test
    void testSaveDriver_Positive() {
        // Arrange
        when(driverRepository.existsByEmail(testDriverRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(testDriverRequest.getPassword())).thenReturn("encodedPassword");
        when(driverRepository.save(any(Driver.class))).thenReturn(testDriver);

        // Act
        String result = driverService.saveDriver(testDriverRequest);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Driver created with ID:"));
        verify(driverRepository).existsByEmail(testDriverRequest.getEmail());
        verify(passwordEncoder).encode(testDriverRequest.getPassword());
        verify(driverRepository).save(any(Driver.class));
    }

    // Test saveDriver method - Negative Cases
    @Test
    void testSaveDriver_EmailAlreadyExists() {
        // Arrange
        when(driverRepository.existsByEmail(testDriverRequest.getEmail())).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            driverService.saveDriver(testDriverRequest);
        });

        assertEquals("Email is already registered", exception.getMessage());
        verify(driverRepository).existsByEmail(testDriverRequest.getEmail());
        verify(driverRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    // Test saveDriver method - Edge Cases
    @Test
    void testSaveDriver_NullRequest() {
        // Act & Assert
        assertThrows(Exception.class, () -> {
            driverService.saveDriver(null);
        });
    }

    // Test getAllDrivers method - Positive Cases
    @Test
    void testGetAllDrivers_Positive() {
        // Arrange
        List<Driver> drivers = Arrays.asList(testDriver);
        when(driverRepository.findAll()).thenReturn(drivers);

        // Act
        List<Driver> result = driverService.getAllDrivers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDriver.getUserId(), result.get(0).getUserId());
        verify(driverRepository).findAll();
    }

    // Test getAllDrivers method - Edge Cases
    @Test
    void testGetAllDrivers_EmptyList() {
        // Arrange
        when(driverRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Driver> result = driverService.getAllDrivers();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(driverRepository).findAll();
    }

    // Test getDriverById method - Positive Cases
    @Test
    void testGetDriverById_Positive() {
        // Arrange
        when(driverRepository.findById("driver1")).thenReturn(Optional.of(testDriver));

        // Act
        Optional<Driver> result = driverService.getDriverById("driver1");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testDriver.getUserId(), result.get().getUserId());
        verify(driverRepository).findById("driver1");
    }

    // Test getDriverById method - Negative Cases
    @Test
    void testGetDriverById_NotFound() {
        // Arrange
        when(driverRepository.findById("invalidDriver")).thenReturn(Optional.empty());

        // Act
        Optional<Driver> result = driverService.getDriverById("invalidDriver");

        // Assert
        assertFalse(result.isPresent());
        verify(driverRepository).findById("invalidDriver");
    }

    // Test getDriverById method - Edge Cases
    @Test
    void testGetDriverById_NullId() {
        // Arrange
        when(driverRepository.findById(null)).thenReturn(Optional.empty());

        // Act
        Optional<Driver> result = driverService.getDriverById(null);

        // Assert
        assertFalse(result.isPresent());
        verify(driverRepository).findById(null);
    }

    // Test getDriverByEmail method - Positive Cases
    @Test
    void testGetDriverByEmail_Positive() {
        // Arrange
        when(driverRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testDriver));

        // Act
        Optional<Driver> result = driverService.getDriverByEmail("john.doe@example.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testDriver.getEmail(), result.get().getEmail());
        verify(driverRepository).findByEmail("john.doe@example.com");
    }

    // Test getDriverByEmail method - Negative Cases
    @Test
    void testGetDriverByEmail_NotFound() {
        // Arrange
        when(driverRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act
        Optional<Driver> result = driverService.getDriverByEmail("nonexistent@example.com");

        // Assert
        assertFalse(result.isPresent());
        verify(driverRepository).findByEmail("nonexistent@example.com");
    }

    // Test getDriverByLicenseNumber method - Positive Cases
    @Test
    void testGetDriverByLicenseNumber_Positive() {
        // Arrange
        when(driverRepository.findByLicenseNumber("DL123456")).thenReturn(Optional.of(testDriver));

        // Act
        Optional<Driver> result = driverService.getDriverByLicenseNumber("DL123456");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testDriver.getLicenseNumber(), result.get().getLicenseNumber());
        verify(driverRepository).findByLicenseNumber("DL123456");
    }

    // Test getDriverByLicenseNumber method - Negative Cases
    @Test
    void testGetDriverByLicenseNumber_NotFound() {
        // Arrange
        when(driverRepository.findByLicenseNumber("INVALID")).thenReturn(Optional.empty());

        // Act
        Optional<Driver> result = driverService.getDriverByLicenseNumber("INVALID");

        // Assert
        assertFalse(result.isPresent());
        verify(driverRepository).findByLicenseNumber("INVALID");
    }

    // Test updateDriver method - Positive Cases
    @Test
    void testUpdateDriver_Positive() {
        // Arrange
        Driver existingDriver = new Driver();
        existingDriver.setUserId("driver1");
        existingDriver.setName("Old Name");
        existingDriver.setEmail("old@example.com");
        existingDriver.setPhone("0987654321");
        existingDriver.setLicenseNumber("OLD123");
        existingDriver.setVehicleType("Old Vehicle");
        existingDriver.setUpdatedAt(LocalDateTime.now().minusDays(1));

        when(driverRepository.findById("driver1")).thenReturn(Optional.of(existingDriver));
        when(driverRepository.save(any(Driver.class))).thenReturn(existingDriver);

        // Act
        String result = driverService.updateDriver("driver1", testDriverRequest);

        // Assert
        assertEquals("Driver updated successfully", result);
        assertEquals(testDriverRequest.getName(), existingDriver.getName());
        assertEquals(testDriverRequest.getEmail(), existingDriver.getEmail());
        assertEquals(testDriverRequest.getPhone(), existingDriver.getPhone());
        assertEquals(testDriverRequest.getLicenseNumber(), existingDriver.getLicenseNumber());
        assertEquals(testDriverRequest.getVehicleType(), existingDriver.getVehicleType());
        assertNotNull(existingDriver.getUpdatedAt());
        verify(driverRepository).findById("driver1");
        verify(driverRepository).save(existingDriver);
    }

    // Test updateDriver method - Negative Cases
    @Test
    void testUpdateDriver_NotFound() {
        // Arrange
        when(driverRepository.findById("invalidDriver")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            driverService.updateDriver("invalidDriver", testDriverRequest);
        });

        assertEquals("Driver not found with id: invalidDriver", exception.getMessage());
        verify(driverRepository).findById("invalidDriver");
        verify(driverRepository, never()).save(any());
    }

    // Test updateDriver method - Edge Cases
    @Test
    void testUpdateDriver_NullId() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            driverService.updateDriver(null, testDriverRequest);
        });

        assertTrue(exception.getMessage().contains("Driver not found with id:"));
        verify(driverRepository).findById(null);
        verify(driverRepository, never()).save(any());
    }

    @Test
    void testUpdateDriver_NullRequest() {
        // Arrange
        when(driverRepository.findById("driver1")).thenReturn(Optional.of(testDriver));

        // Act & Assert
        assertThrows(Exception.class, () -> {
            driverService.updateDriver("driver1", null);
        });
    }

    // Test deleteDriver method - Positive Cases
    @Test
    void testDeleteDriver_Positive() {
        // Arrange
        when(driverRepository.existsById("driver1")).thenReturn(true);

        // Act
        String result = driverService.deleteDriver("driver1");

        // Assert
        assertEquals("Driver deleted successfully", result);
        verify(driverRepository).existsById("driver1");
        verify(driverRepository).deleteById("driver1");
    }

    // Test deleteDriver method - Negative Cases
    @Test
    void testDeleteDriver_NotFound() {
        // Arrange
        when(driverRepository.existsById("invalidDriver")).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            driverService.deleteDriver("invalidDriver");
        });

        assertEquals("Driver not found with id: invalidDriver", exception.getMessage());
        verify(driverRepository).existsById("invalidDriver");
        verify(driverRepository, never()).deleteById(anyString());
    }

    // Test deleteDriver method - Edge Cases
    @Test
    void testDeleteDriver_NullId() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            driverService.deleteDriver(null);
        });

        assertTrue(exception.getMessage().contains("Driver not found with id:"));
        verify(driverRepository).existsById(null);
        verify(driverRepository, never()).deleteById(anyString());
    }

    // Error case tests
    @Test
    void testSaveDriver_RepositoryException() {
        // Arrange
        when(driverRepository.existsByEmail(testDriverRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(testDriverRequest.getPassword())).thenReturn("encodedPassword");
        when(driverRepository.save(any(Driver.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            driverService.saveDriver(testDriverRequest);
        });

        verify(driverRepository).existsByEmail(testDriverRequest.getEmail());
        verify(passwordEncoder).encode(testDriverRequest.getPassword());
        verify(driverRepository).save(any(Driver.class));
    }

    @Test
    void testGetAllDrivers_RepositoryException() {
        // Arrange
        when(driverRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            driverService.getAllDrivers();
        });

        verify(driverRepository).findAll();
    }

    @Test
    void testUpdateDriver_RepositoryException() {
        // Arrange
        when(driverRepository.findById("driver1")).thenReturn(Optional.of(testDriver));
        when(driverRepository.save(any(Driver.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            driverService.updateDriver("driver1", testDriverRequest);
        });

        verify(driverRepository).findById("driver1");
        verify(driverRepository).save(any(Driver.class));
    }

    @Test
    void testDeleteDriver_RepositoryException() {
        // Arrange
        when(driverRepository.existsById("driver1")).thenReturn(true);
        doThrow(new RuntimeException("Database error")).when(driverRepository).deleteById("driver1");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            driverService.deleteDriver("driver1");
        });

        verify(driverRepository).existsById("driver1");
        verify(driverRepository).deleteById("driver1");
    }
}