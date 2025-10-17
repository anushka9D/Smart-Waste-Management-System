package com.swms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.swms.dto.ApiResponse;
import com.swms.model.Truck;
import com.swms.service.TruckService;

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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TruckControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TruckService truckService;

    @InjectMocks
    private TruckController truckController;

    private ObjectMapper objectMapper;

    private Truck testTruck;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(truckController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

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

    // Test createTruck endpoint
    @Test
    void testCreateTruck_Positive() throws Exception {
        // Arrange
        when(truckService.saveTruck(any(Truck.class)))
                .thenReturn("Truck created with ID: truck1");

        // Act & Assert
        mockMvc.perform(post("/api/trucks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTruck)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("Truck created with ID: truck1")));

        verify(truckService, times(1)).saveTruck(any(Truck.class));
    }

    @Test
    void testCreateTruck_Exception() throws Exception {
        // Arrange
        when(truckService.saveTruck(any(Truck.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(post("/api/trucks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTruck)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Failed to create truck: Database error")));

        verify(truckService, times(1)).saveTruck(any(Truck.class));
    }

    // Test createTrucks endpoint
    @Test
    void testCreateTrucks_Positive() throws Exception {
        // Arrange
        List<Truck> trucks = Arrays.asList(testTruck);
        when(truckService.saveTruck(any(Truck.class)))
                .thenReturn("Truck created with ID: truck1");

        // Act & Assert
        mockMvc.perform(post("/api/trucks/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(trucks)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("1 trucks created successfully")));

        verify(truckService, times(1)).saveTruck(any(Truck.class));
    }

    @Test
    void testCreateTrucks_Exception() throws Exception {
        // Arrange
        List<Truck> trucks = Arrays.asList(testTruck);
        when(truckService.saveTruck(any(Truck.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(post("/api/trucks/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(trucks)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Failed to create trucks: Database error")));

        verify(truckService, times(1)).saveTruck(any(Truck.class));
    }

    // Test getAllTrucks endpoint
    @Test
    void testGetAllTrucks_Positive() throws Exception {
        // Arrange
        List<Truck> trucks = Arrays.asList(testTruck);
        when(truckService.getAllTrucks()).thenReturn(trucks);

        // Act & Assert
        mockMvc.perform(get("/api/trucks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Trucks retrieved successfully")))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].truckId", is("truck1")))
                .andExpect(jsonPath("$.data[0].registrationNumber", is("TRK001")));

        verify(truckService, times(1)).getAllTrucks();
    }

    @Test
    void testGetAllTrucks_EmptyList() throws Exception {
        // Arrange
        when(truckService.getAllTrucks()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/trucks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Trucks retrieved successfully")))
                .andExpect(jsonPath("$.data", hasSize(0)));

        verify(truckService, times(1)).getAllTrucks();
    }

    @Test
    void testGetAllTrucks_Exception() throws Exception {
        // Arrange
        when(truckService.getAllTrucks()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get("/api/trucks"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Failed to retrieve trucks: Database error")));

        verify(truckService, times(1)).getAllTrucks();
    }

    // Test getTruckById endpoint
    @Test
    void testGetTruckById_Positive() throws Exception {
        // Arrange
        when(truckService.getTruckById("truck1")).thenReturn(Optional.of(testTruck));

        // Act & Assert
        mockMvc.perform(get("/api/trucks/{truckId}", "truck1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Truck found")))
                .andExpect(jsonPath("$.data.truckId", is("truck1")))
                .andExpect(jsonPath("$.data.registrationNumber", is("TRK001")));

        verify(truckService, times(1)).getTruckById("truck1");
    }

    @Test
    void testGetTruckById_NotFound() throws Exception {
        // Arrange
        when(truckService.getTruckById("invalidTruck")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/trucks/{truckId}", "invalidTruck"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Truck not found with id: invalidTruck")));

        verify(truckService, times(1)).getTruckById("invalidTruck");
    }

    @Test
    void testGetTruckById_Exception() throws Exception {
        // Arrange
        when(truckService.getTruckById("truck1")).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get("/api/trucks/{truckId}", "truck1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Failed to retrieve truck: Database error")));

        verify(truckService, times(1)).getTruckById("truck1");
    }

    // Test updateTruck endpoint
    @Test
    void testUpdateTruck_Positive() throws Exception {
        // Arrange
        when(truckService.updateTruck(anyString(), any(Truck.class)))
                .thenReturn("Truck updated successfully");

        // Act & Assert
        mockMvc.perform(put("/api/trucks/{truckId}", "truck1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTruck)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Truck updated successfully")));

        verify(truckService, times(1)).updateTruck(anyString(), any(Truck.class));
    }

    @Test
    void testUpdateTruck_Exception() throws Exception {
        // Arrange
        when(truckService.updateTruck(anyString(), any(Truck.class)))
                .thenThrow(new RuntimeException("Truck not found with id: invalidTruck"));

        // Act & Assert
        mockMvc.perform(put("/api/trucks/{truckId}", "invalidTruck")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTruck)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Failed to update truck: Truck not found with id: invalidTruck")));

        verify(truckService, times(1)).updateTruck(anyString(), any(Truck.class));
    }

    // Test deleteTruck endpoint
    @Test
    void testDeleteTruck_Positive() throws Exception {
        // Arrange
        when(truckService.deleteTruck("truck1")).thenReturn("Truck deleted successfully");

        // Act & Assert
        mockMvc.perform(delete("/api/trucks/{truckId}", "truck1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Truck deleted successfully")));

        verify(truckService, times(1)).deleteTruck("truck1");
    }

    @Test
    void testDeleteTruck_Exception() throws Exception {
        // Arrange
        when(truckService.deleteTruck("invalidTruck"))
                .thenThrow(new RuntimeException("Truck not found with id: invalidTruck"));

        // Act & Assert
        mockMvc.perform(delete("/api/trucks/{truckId}", "invalidTruck"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Failed to delete truck: Truck not found with id: invalidTruck")));

        verify(truckService, times(1)).deleteTruck("invalidTruck");
    }

    // Edge case tests
    @Test
    void testGetTruckById_InvalidId() throws Exception {
        // Act & Assert
        // When ID is empty, Spring returns 404 before even calling the controller method
        mockMvc.perform(get("/api/trucks/{truckId}", ""))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateTruck_InvalidId() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/trucks/{truckId}", "")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTruck)))
                .andExpect(status().isNotFound());
        // Note: When ID is empty, Spring returns 404 before even calling the controller method
    }

    @Test
    void testDeleteTruck_InvalidId() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/trucks/{truckId}", ""))
                .andExpect(status().isNotFound());
        // Note: When ID is empty, Spring returns 404 before even calling the controller method
    }
}