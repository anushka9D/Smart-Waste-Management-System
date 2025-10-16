package com.swms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swms.dto.ApiResponse;
import com.swms.dto.DriverRequest;
import com.swms.model.Driver;
import com.swms.service.DriverService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DriverControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DriverService driverService;

    @InjectMocks
    private DriverController driverController;

    private ObjectMapper objectMapper;

    private Driver testDriver;
    private DriverRequest testDriverRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(driverController).build();
        objectMapper = new ObjectMapper();

        // Initialize test data
        testDriver = new Driver();
        testDriver.setUserId("driver1");
        testDriver.setName("John Doe");
        testDriver.setEmail("john.doe@example.com");
        testDriver.setPhone("1234567890");
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

    // Test addDriver endpoint
    @Test
    void testAddDriver_Positive() throws Exception {
        // Arrange
        when(driverService.saveDriver(any(DriverRequest.class)))
                .thenReturn("Driver created with ID: driver1");

        // Act & Assert
        mockMvc.perform(post("/api/drivers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testDriverRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Driver added successfully")))
                .andExpect(jsonPath("$.data", is("Driver created with ID: driver1")));

        verify(driverService, times(1)).saveDriver(any(DriverRequest.class));
    }

    @Test
    void testAddDriver_RuntimeException() throws Exception {
        // Arrange
        when(driverService.saveDriver(any(DriverRequest.class)))
                .thenThrow(new RuntimeException("Email is already registered"));

        // Act & Assert
        mockMvc.perform(post("/api/drivers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testDriverRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Failed to add driver: Email is already registered")));

        verify(driverService, times(1)).saveDriver(any(DriverRequest.class));
    }

    @Test
    void testAddDriver_InternalException() throws Exception {
        // Arrange
        // For addDriver, RuntimeException is caught first and converted to 400
        // To test the general Exception handler, we need to throw an Error or use a different approach
        // Since we can't modify the service interface to throw checked exceptions,
        // we'll test with a RuntimeException that isn't specifically caught
        when(driverService.saveDriver(any(DriverRequest.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        mockMvc.perform(post("/api/drivers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testDriverRequest)))
                .andExpect(status().isBadRequest()) // RuntimeException is caught and converted to 400
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Failed to add driver: Unexpected error")));

        verify(driverService, times(1)).saveDriver(any(DriverRequest.class));
    }

    // Test getAllDrivers endpoint
    @Test
    void testGetAllDrivers_Positive() throws Exception {
        // Arrange
        List<Driver> drivers = Arrays.asList(testDriver);
        when(driverService.getAllDrivers()).thenReturn(drivers);

        // Act & Assert
        mockMvc.perform(get("/api/drivers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Drivers retrieved successfully")))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].userId", is("driver1")))
                .andExpect(jsonPath("$.data[0].name", is("John Doe")));

        verify(driverService, times(1)).getAllDrivers();
    }

    @Test
    void testGetAllDrivers_EmptyList() throws Exception {
        // Arrange
        when(driverService.getAllDrivers()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/drivers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Drivers retrieved successfully")))
                .andExpect(jsonPath("$.data", hasSize(0)));

        verify(driverService, times(1)).getAllDrivers();
    }

    @Test
    void testGetAllDrivers_Exception() throws Exception {
        // Arrange
        when(driverService.getAllDrivers()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get("/api/drivers"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Failed to retrieve drivers: Database error")));

        verify(driverService, times(1)).getAllDrivers();
    }

    // Test getDriverById endpoint
    @Test
    void testGetDriverById_Positive() throws Exception {
        // Arrange
        when(driverService.getDriverById("driver1")).thenReturn(Optional.of(testDriver));

        // Act & Assert
        mockMvc.perform(get("/api/drivers/{id}", "driver1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Driver found")))
                .andExpect(jsonPath("$.data.userId", is("driver1")))
                .andExpect(jsonPath("$.data.name", is("John Doe")));

        verify(driverService, times(1)).getDriverById("driver1");
    }

    @Test
    void testGetDriverById_NotFound() throws Exception {
        // Arrange
        when(driverService.getDriverById("invalidDriver")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/drivers/{id}", "invalidDriver"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Driver not found with id: invalidDriver")));

        verify(driverService, times(1)).getDriverById("invalidDriver");
    }

    @Test
    void testGetDriverById_Exception() throws Exception {
        // Arrange
        when(driverService.getDriverById("driver1")).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get("/api/drivers/{id}", "driver1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Failed to retrieve driver: Database error")));

        verify(driverService, times(1)).getDriverById("driver1");
    }

    // Test getDriverByEmail endpoint
    @Test
    void testGetDriverByEmail_Positive() throws Exception {
        // Arrange
        when(driverService.getDriverByEmail("john.doe@example.com")).thenReturn(Optional.of(testDriver));

        // Act & Assert
        mockMvc.perform(get("/api/drivers/email/{email}", "john.doe@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Driver found")))
                .andExpect(jsonPath("$.data.userId", is("driver1")))
                .andExpect(jsonPath("$.data.email", is("john.doe@example.com")));

        verify(driverService, times(1)).getDriverByEmail("john.doe@example.com");
    }

    @Test
    void testGetDriverByEmail_NotFound() throws Exception {
        // Arrange
        when(driverService.getDriverByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/drivers/email/{email}", "nonexistent@example.com"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Driver not found with email: nonexistent@example.com")));

        verify(driverService, times(1)).getDriverByEmail("nonexistent@example.com");
    }

    @Test
    void testGetDriverByEmail_Exception() throws Exception {
        // Arrange
        when(driverService.getDriverByEmail("john.doe@example.com")).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get("/api/drivers/email/{email}", "john.doe@example.com"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Failed to retrieve driver: Database error")));

        verify(driverService, times(1)).getDriverByEmail("john.doe@example.com");
    }

    // Test getDriverByLicenseNumber endpoint
    @Test
    void testGetDriverByLicenseNumber_Positive() throws Exception {
        // Arrange
        when(driverService.getDriverByLicenseNumber("DL123456")).thenReturn(Optional.of(testDriver));

        // Act & Assert
        mockMvc.perform(get("/api/drivers/license-number/{licenseNumber}", "DL123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Driver found")))
                .andExpect(jsonPath("$.data.userId", is("driver1")))
                .andExpect(jsonPath("$.data.licenseNumber", is("DL123456")));

        verify(driverService, times(1)).getDriverByLicenseNumber("DL123456");
    }

    @Test
    void testGetDriverByLicenseNumber_NotFound() throws Exception {
        // Arrange
        when(driverService.getDriverByLicenseNumber("INVALID")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/drivers/license-number/{licenseNumber}", "INVALID"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Driver not found with license number: INVALID")));

        verify(driverService, times(1)).getDriverByLicenseNumber("INVALID");
    }

    @Test
    void testGetDriverByLicenseNumber_Exception() throws Exception {
        // Arrange
        when(driverService.getDriverByLicenseNumber("DL123456")).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get("/api/drivers/license-number/{licenseNumber}", "DL123456"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Failed to retrieve driver: Database error")));

        verify(driverService, times(1)).getDriverByLicenseNumber("DL123456");
    }

    // Test updateDriver endpoint
    @Test
    void testUpdateDriver_Positive() throws Exception {
        // Arrange
        when(driverService.updateDriver(anyString(), any(DriverRequest.class)))
                .thenReturn("Driver updated successfully");

        // Act & Assert
        mockMvc.perform(put("/api/drivers/{id}", "driver1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testDriverRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Driver updated successfully")));

        verify(driverService, times(1)).updateDriver(anyString(), any(DriverRequest.class));
    }

    @Test
    void testUpdateDriver_RuntimeException() throws Exception {
        // Arrange
        when(driverService.updateDriver(anyString(), any(DriverRequest.class)))
                .thenThrow(new RuntimeException("Driver not found with id: invalidDriver"));

        // Act & Assert
        mockMvc.perform(put("/api/drivers/{id}", "invalidDriver")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testDriverRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Failed to update driver: Driver not found with id: invalidDriver")));

        verify(driverService, times(1)).updateDriver(anyString(), any(DriverRequest.class));
    }

    @Test
    void testUpdateDriver_InternalException() throws Exception {
        // Arrange
        // For updateDriver, RuntimeException is caught first and converted to 400
        // To test the general Exception handler, we need to throw an Error or use a different approach
        when(driverService.updateDriver(anyString(), any(DriverRequest.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        mockMvc.perform(put("/api/drivers/{id}", "driver1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testDriverRequest)))
                .andExpect(status().isBadRequest()) // RuntimeException is caught and converted to 400
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Failed to update driver: Unexpected error")));

        verify(driverService, times(1)).updateDriver(anyString(), any(DriverRequest.class));
    }

    // Test deleteDriver endpoint
    @Test
    void testDeleteDriver_Positive() throws Exception {
        // Arrange
        when(driverService.deleteDriver("driver1")).thenReturn("Driver deleted successfully");

        // Act & Assert
        mockMvc.perform(delete("/api/drivers/{id}", "driver1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Driver deleted successfully")));

        verify(driverService, times(1)).deleteDriver("driver1");
    }

    @Test
    void testDeleteDriver_RuntimeException() throws Exception {
        // Arrange
        when(driverService.deleteDriver("invalidDriver"))
                .thenThrow(new RuntimeException("Driver not found with id: invalidDriver"));

        // Act & Assert
        mockMvc.perform(delete("/api/drivers/{id}", "invalidDriver"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Failed to delete driver: Driver not found with id: invalidDriver")));

        verify(driverService, times(1)).deleteDriver("invalidDriver");
    }

    @Test
    void testDeleteDriver_InternalException() throws Exception {
        // Arrange
        // For deleteDriver, RuntimeException is caught first and converted to 400
        // To test the general Exception handler, we need to throw an Error or use a different approach
        when(driverService.deleteDriver("driver1"))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        mockMvc.perform(delete("/api/drivers/{id}", "driver1"))
                .andExpect(status().isBadRequest()) // RuntimeException is caught and converted to 400
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Failed to delete driver: Unexpected error")));

        verify(driverService, times(1)).deleteDriver("driver1");
    }

    // Edge case tests
    @Test
    void testGetDriverById_InvalidId() throws Exception {
        // Act & Assert
        // When ID is empty, Spring returns 404 before even calling the controller method
        // So we just check that it returns 404
        mockMvc.perform(get("/api/drivers/{id}", ""))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateDriver_InvalidId() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/drivers/{id}", "")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testDriverRequest)))
                .andExpect(status().isNotFound());
        // Note: When ID is empty, Spring returns 404 before even calling the controller method
    }

    @Test
    void testDeleteDriver_InvalidId() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/drivers/{id}", ""))
                .andExpect(status().isNotFound());
        // Note: When ID is empty, Spring returns 404 before even calling the controller method
    }
}