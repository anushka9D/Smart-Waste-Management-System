package com.swms.controller.citizen;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swms.dto.ApiResponse;
import com.swms.dto.citizen.CitizenRequest;
import com.swms.model.citizen.Citizen;
import com.swms.service.citizen.CitizenService;
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
class CitizenControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CitizenService citizenService;

    @InjectMocks
    private CitizenController citizenController;

    private ObjectMapper objectMapper;

    private Citizen testCitizen;
    private CitizenRequest testCitizenRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(citizenController).build();
        objectMapper = new ObjectMapper();

        // Initialize test data
        testCitizen = new Citizen();
        testCitizen.setUserId("citizen1");
        testCitizen.setName("John Doe");
        testCitizen.setEmail("john.doe@example.com");
        testCitizen.setPhone("1234567890");
        testCitizen.setPassword("encodedPassword");
        testCitizen.setUserType("CITIZEN");
        testCitizen.setCreatedAt(LocalDateTime.now());
        testCitizen.setUpdatedAt(LocalDateTime.now());
        testCitizen.setEnabled(true);

        testCitizenRequest = new CitizenRequest();
        testCitizenRequest.setName("John Doe");
        testCitizenRequest.setEmail("john.doe@example.com");
        testCitizenRequest.setPhone("1234567890");
        testCitizenRequest.setPassword("password123");
    }

    // Test addCitizen endpoint - Positive Cases
    @Test
    void testAddCitizen_Positive() throws Exception {
        // Arrange
        when(citizenService.saveCitizen(any(CitizenRequest.class)))
                .thenReturn("Citizen created with ID: citizen1");

        // Act & Assert
        mockMvc.perform(post("/api/citizens")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCitizenRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Citizen added successfully")))
                .andExpect(jsonPath("$.data", is("Citizen created with ID: citizen1")));

        verify(citizenService, times(1)).saveCitizen(any(CitizenRequest.class));
    }

    // Test addCitizen endpoint - Negative Cases
    @Test
    void testAddCitizen_RuntimeException() throws Exception {
        // Arrange
        when(citizenService.saveCitizen(any(CitizenRequest.class)))
                .thenThrow(new RuntimeException("Email is already registered"));

        // Act & Assert
        mockMvc.perform(post("/api/citizens")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCitizenRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Failed to add citizen: Email is already registered")));

        verify(citizenService, times(1)).saveCitizen(any(CitizenRequest.class));
    }

    // Test addCitizen endpoint - Error Cases
    @Test
    void testAddCitizen_InternalException() throws Exception {
        // Arrange
        when(citizenService.saveCitizen(any(CitizenRequest.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        mockMvc.perform(post("/api/citizens")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCitizenRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Failed to add citizen: Unexpected error")));

        verify(citizenService, times(1)).saveCitizen(any(CitizenRequest.class));
    }

    // Test getAllCitizens endpoint - Positive Cases
    @Test
    void testGetAllCitizens_Positive() throws Exception {
        // Arrange
        List<Citizen> citizens = Arrays.asList(testCitizen);
        when(citizenService.getAllCitizens()).thenReturn(citizens);

        // Act & Assert
        mockMvc.perform(get("/api/citizens"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Citizens retrieved successfully")))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].userId", is("citizen1")))
                .andExpect(jsonPath("$.data[0].name", is("John Doe")));

        verify(citizenService, times(1)).getAllCitizens();
    }

    // Test getAllCitizens endpoint - Edge Cases
    @Test
    void testGetAllCitizens_EmptyList() throws Exception {
        // Arrange
        when(citizenService.getAllCitizens()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/citizens"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Citizens retrieved successfully")))
                .andExpect(jsonPath("$.data", hasSize(0)));

        verify(citizenService, times(1)).getAllCitizens();
    }

    // Test getAllCitizens endpoint - Error Cases
    @Test
    void testGetAllCitizens_Exception() throws Exception {
        // Arrange
        when(citizenService.getAllCitizens()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get("/api/citizens"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Failed to retrieve citizens: Database error")));

        verify(citizenService, times(1)).getAllCitizens();
    }

    // Test getCitizenById endpoint - Positive Cases
    @Test
    void testGetCitizenById_Positive() throws Exception {
        // Arrange
        when(citizenService.getCitizenById("citizen1")).thenReturn(Optional.of(testCitizen));

        // Act & Assert
        mockMvc.perform(get("/api/citizens/{id}", "citizen1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Citizen found")))
                .andExpect(jsonPath("$.data.userId", is("citizen1")))
                .andExpect(jsonPath("$.data.name", is("John Doe")));

        verify(citizenService, times(1)).getCitizenById("citizen1");
    }

    // Test getCitizenById endpoint - Negative Cases
    @Test
    void testGetCitizenById_NotFound() throws Exception {
        // Arrange
        when(citizenService.getCitizenById("invalidCitizen")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/citizens/{id}", "invalidCitizen"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Citizen not found with id: invalidCitizen")));

        verify(citizenService, times(1)).getCitizenById("invalidCitizen");
    }

    // Test getCitizenById endpoint - Error Cases
    @Test
    void testGetCitizenById_Exception() throws Exception {
        // Arrange
        when(citizenService.getCitizenById("citizen1")).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get("/api/citizens/{id}", "citizen1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Failed to retrieve citizen: Database error")));

        verify(citizenService, times(1)).getCitizenById("citizen1");
    }

    // Test getCitizenByName endpoint - Positive Cases
    @Test
    void testGetCitizenByName_Positive() throws Exception {
        // Arrange
        when(citizenService.getCitizenByName("John Doe")).thenReturn(Optional.of(testCitizen));

        // Act & Assert
        mockMvc.perform(get("/api/citizens/name/{name}", "John Doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Citizen found")))
                .andExpect(jsonPath("$.data.userId", is("citizen1")))
                .andExpect(jsonPath("$.data.name", is("John Doe")));

        verify(citizenService, times(1)).getCitizenByName("John Doe");
    }

    // Test getCitizenByName endpoint - Negative Cases
    @Test
    void testGetCitizenByName_NotFound() throws Exception {
        // Arrange
        when(citizenService.getCitizenByName("Nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/citizens/name/{name}", "Nonexistent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Citizen not found with name: Nonexistent")));

        verify(citizenService, times(1)).getCitizenByName("Nonexistent");
    }

    // Test getCitizenByName endpoint - Error Cases
    @Test
    void testGetCitizenByName_Exception() throws Exception {
        // Arrange
        when(citizenService.getCitizenByName("John Doe")).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get("/api/citizens/name/{name}", "John Doe"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Failed to retrieve citizen: Database error")));

        verify(citizenService, times(1)).getCitizenByName("John Doe");
    }

    // Test updateCitizen endpoint - Positive Cases
    @Test
    void testUpdateCitizen_Positive() throws Exception {
        // Arrange
        when(citizenService.updateCitizen(anyString(), any(CitizenRequest.class)))
                .thenReturn("Citizen updated successfully");

        // Act & Assert
        mockMvc.perform(put("/api/citizens/{id}", "citizen1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCitizenRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Citizen updated successfully")))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(citizenService, times(1)).updateCitizen(anyString(), any(CitizenRequest.class));
    }

    // Test updateCitizen endpoint - Negative Cases
    @Test
    void testUpdateCitizen_RuntimeException() throws Exception {
        // Arrange
        when(citizenService.updateCitizen(anyString(), any(CitizenRequest.class)))
                .thenThrow(new RuntimeException("Citizen not found with id: invalidCitizen"));

        // Act & Assert
        mockMvc.perform(put("/api/citizens/{id}", "invalidCitizen")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCitizenRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Failed to update citizen: Citizen not found with id: invalidCitizen")));

        verify(citizenService, times(1)).updateCitizen(anyString(), any(CitizenRequest.class));
    }

    // Test updateCitizen endpoint - Error Cases
    @Test
    void testUpdateCitizen_InternalException() throws Exception {
        // Arrange
        when(citizenService.updateCitizen(anyString(), any(CitizenRequest.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        mockMvc.perform(put("/api/citizens/{id}", "citizen1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCitizenRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Failed to update citizen: Unexpected error")));

        verify(citizenService, times(1)).updateCitizen(anyString(), any(CitizenRequest.class));
    }

    // Test deleteCitizen endpoint - Positive Cases
    @Test
    void testDeleteCitizen_Positive() throws Exception {
        // Arrange
        when(citizenService.deleteCitizen("citizen1")).thenReturn("Citizen deleted successfully");

        // Act & Assert
        mockMvc.perform(delete("/api/citizens/{id}", "citizen1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Citizen deleted successfully")))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(citizenService, times(1)).deleteCitizen("citizen1");
    }

    // Test deleteCitizen endpoint - Negative Cases
    @Test
    void testDeleteCitizen_RuntimeException() throws Exception {
        // Arrange
        when(citizenService.deleteCitizen("invalidCitizen"))
                .thenThrow(new RuntimeException("Citizen not found with id: invalidCitizen"));

        // Act & Assert
        mockMvc.perform(delete("/api/citizens/{id}", "invalidCitizen"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Failed to delete citizen: Citizen not found with id: invalidCitizen")));

        verify(citizenService, times(1)).deleteCitizen("invalidCitizen");
    }

    // Test deleteCitizen endpoint - Error Cases
    @Test
    void testDeleteCitizen_InternalException() throws Exception {
        // Arrange
        when(citizenService.deleteCitizen("citizen1"))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        mockMvc.perform(delete("/api/citizens/{id}", "citizen1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Failed to delete citizen: Unexpected error")));

        verify(citizenService, times(1)).deleteCitizen("citizen1");
    }

    // Edge case tests
    @Test
    void testGetCitizenById_InvalidId() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/citizens/{id}", ""))
                .andExpect(status().isNotFound()); // Empty ID results in 404 Not Found
    }

    @Test
    void testUpdateCitizen_InvalidId() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/citizens/{id}", "")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCitizenRequest)))
                .andExpect(status().isNotFound()); // Empty ID results in 404 Not Found
    }

    @Test
    void testDeleteCitizen_InvalidId() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/citizens/{id}", ""))
                .andExpect(status().isNotFound()); // Empty ID results in 404 Not Found
    }

    @Test
    void testAddCitizen_InvalidJson() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/citizens")
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateCitizen_InvalidJson() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/citizens/{id}", "citizen1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
                .andExpect(status().isBadRequest());
    }
}