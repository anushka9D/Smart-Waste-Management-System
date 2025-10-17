package com.swms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swms.dto.ApiResponse;
import com.swms.dto.CityAuthorityRequest;
import com.swms.model.CityAuthority;
import com.swms.service.CityAuthorityService;

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
class CityAuthorityControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CityAuthorityService cityAuthorityService;

    @InjectMocks
    private CityAuthorityController cityAuthorityController;

    private ObjectMapper objectMapper;

    private CityAuthority testCityAuthority;
    private CityAuthorityRequest testCityAuthorityRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cityAuthorityController).build();
        objectMapper = new ObjectMapper();

        // Initialize test data
        testCityAuthority = new CityAuthority();
        testCityAuthority.setUserId("ca1");
        testCityAuthority.setName("John Smith");
        testCityAuthority.setEmail("john.smith@example.com");
        testCityAuthority.setPhone("1234567890");
        testCityAuthority.setEmployeeId("EMP001");
        testCityAuthority.setDepartment("Waste Management");
        testCityAuthority.setCreatedAt(LocalDateTime.now());
        testCityAuthority.setUpdatedAt(LocalDateTime.now());

        testCityAuthorityRequest = new CityAuthorityRequest();
        testCityAuthorityRequest.setName("John Smith");
        testCityAuthorityRequest.setEmail("john.smith@example.com");
        testCityAuthorityRequest.setPhone("1234567890");
        testCityAuthorityRequest.setPassword("password123");
        testCityAuthorityRequest.setEmployeeId("EMP001");
        testCityAuthorityRequest.setDepartment("Waste Management");
    }

    // Test addCityAuthority endpoint
    @Test
    void testAddCityAuthority_Positive() throws Exception {
        // Arrange
        when(cityAuthorityService.saveCityAuthority(any(CityAuthorityRequest.class)))
                .thenReturn("City Authority created with ID: ca1");

        // Act & Assert
        mockMvc.perform(post("/api/city-authorities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCityAuthorityRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("City Authority added successfully")))
                .andExpect(jsonPath("$.data", is("City Authority created with ID: ca1")));

        verify(cityAuthorityService, times(1)).saveCityAuthority(any(CityAuthorityRequest.class));
    }

    @Test
    void testAddCityAuthority_RuntimeException() throws Exception {
        // Arrange
        when(cityAuthorityService.saveCityAuthority(any(CityAuthorityRequest.class)))
                .thenThrow(new RuntimeException("Email is already registered"));

        // Act & Assert
        mockMvc.perform(post("/api/city-authorities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCityAuthorityRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Failed to add city authority: Email is already registered")));

        verify(cityAuthorityService, times(1)).saveCityAuthority(any(CityAuthorityRequest.class));
    }

    @Test
    void testAddCityAuthority_InternalException() throws Exception {
        // Arrange
        when(cityAuthorityService.saveCityAuthority(any(CityAuthorityRequest.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        mockMvc.perform(post("/api/city-authorities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCityAuthorityRequest)))
                .andExpect(status().isBadRequest()) // RuntimeException is caught and converted to 400
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Failed to add city authority: Unexpected error")));

        verify(cityAuthorityService, times(1)).saveCityAuthority(any(CityAuthorityRequest.class));
    }

    // Test getAllCityAuthorities endpoint
    @Test
    void testGetAllCityAuthorities_Positive() throws Exception {
        // Arrange
        List<CityAuthority> cityAuthorities = Arrays.asList(testCityAuthority);
        when(cityAuthorityService.getAllCityAuthorities()).thenReturn(cityAuthorities);

        // Act & Assert
        mockMvc.perform(get("/api/city-authorities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("City Authorities retrieved successfully")))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].userId", is("ca1")))
                .andExpect(jsonPath("$.data[0].name", is("John Smith")));

        verify(cityAuthorityService, times(1)).getAllCityAuthorities();
    }

    @Test
    void testGetAllCityAuthorities_EmptyList() throws Exception {
        // Arrange
        when(cityAuthorityService.getAllCityAuthorities()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/city-authorities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("City Authorities retrieved successfully")))
                .andExpect(jsonPath("$.data", hasSize(0)));

        verify(cityAuthorityService, times(1)).getAllCityAuthorities();
    }

    @Test
    void testGetAllCityAuthorities_Exception() throws Exception {
        // Arrange
        when(cityAuthorityService.getAllCityAuthorities()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get("/api/city-authorities"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Failed to retrieve city authorities: Database error")));

        verify(cityAuthorityService, times(1)).getAllCityAuthorities();
    }

    // Test getCityAuthorityById endpoint
    @Test
    void testGetCityAuthorityById_Positive() throws Exception {
        // Arrange
        when(cityAuthorityService.getCityAuthorityById("ca1")).thenReturn(Optional.of(testCityAuthority));

        // Act & Assert
        mockMvc.perform(get("/api/city-authorities/{id}", "ca1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("City Authority found")))
                .andExpect(jsonPath("$.data.userId", is("ca1")))
                .andExpect(jsonPath("$.data.name", is("John Smith")));

        verify(cityAuthorityService, times(1)).getCityAuthorityById("ca1");
    }

    @Test
    void testGetCityAuthorityById_NotFound() throws Exception {
        // Arrange
        when(cityAuthorityService.getCityAuthorityById("invalidCA")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/city-authorities/{id}", "invalidCA"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("City Authority not found with id: invalidCA")));

        verify(cityAuthorityService, times(1)).getCityAuthorityById("invalidCA");
    }

    @Test
    void testGetCityAuthorityById_Exception() throws Exception {
        // Arrange
        when(cityAuthorityService.getCityAuthorityById("ca1")).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get("/api/city-authorities/{id}", "ca1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Failed to retrieve city authority: Database error")));

        verify(cityAuthorityService, times(1)).getCityAuthorityById("ca1");
    }

    // Test getCityAuthorityByEmail endpoint
    @Test
    void testGetCityAuthorityByEmail_Positive() throws Exception {
        // Arrange
        when(cityAuthorityService.getCityAuthorityByEmail("john.smith@example.com")).thenReturn(Optional.of(testCityAuthority));

        // Act & Assert
        mockMvc.perform(get("/api/city-authorities/email/{email}", "john.smith@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("City Authority found")))
                .andExpect(jsonPath("$.data.userId", is("ca1")))
                .andExpect(jsonPath("$.data.email", is("john.smith@example.com")));

        verify(cityAuthorityService, times(1)).getCityAuthorityByEmail("john.smith@example.com");
    }

    @Test
    void testGetCityAuthorityByEmail_NotFound() throws Exception {
        // Arrange
        when(cityAuthorityService.getCityAuthorityByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/city-authorities/email/{email}", "nonexistent@example.com"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("City Authority not found with email: nonexistent@example.com")));

        verify(cityAuthorityService, times(1)).getCityAuthorityByEmail("nonexistent@example.com");
    }

    @Test
    void testGetCityAuthorityByEmail_Exception() throws Exception {
        // Arrange
        when(cityAuthorityService.getCityAuthorityByEmail("john.smith@example.com")).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get("/api/city-authorities/email/{email}", "john.smith@example.com"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Failed to retrieve city authority: Database error")));

        verify(cityAuthorityService, times(1)).getCityAuthorityByEmail("john.smith@example.com");
    }

    // Test getCityAuthorityByEmployeeId endpoint
    @Test
    void testGetCityAuthorityByEmployeeId_Positive() throws Exception {
        // Arrange
        when(cityAuthorityService.getCityAuthorityByEmployeeId("EMP001")).thenReturn(Optional.of(testCityAuthority));

        // Act & Assert
        mockMvc.perform(get("/api/city-authorities/employee-id/{employeeId}", "EMP001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("City Authority found")))
                .andExpect(jsonPath("$.data.userId", is("ca1")))
                .andExpect(jsonPath("$.data.employeeId", is("EMP001")));

        verify(cityAuthorityService, times(1)).getCityAuthorityByEmployeeId("EMP001");
    }

    @Test
    void testGetCityAuthorityByEmployeeId_NotFound() throws Exception {
        // Arrange
        when(cityAuthorityService.getCityAuthorityByEmployeeId("INVALID")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/city-authorities/employee-id/{employeeId}", "INVALID"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("City Authority not found with employee ID: INVALID")));

        verify(cityAuthorityService, times(1)).getCityAuthorityByEmployeeId("INVALID");
    }

    @Test
    void testGetCityAuthorityByEmployeeId_Exception() throws Exception {
        // Arrange
        when(cityAuthorityService.getCityAuthorityByEmployeeId("EMP001")).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get("/api/city-authorities/employee-id/{employeeId}", "EMP001"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Failed to retrieve city authority: Database error")));

        verify(cityAuthorityService, times(1)).getCityAuthorityByEmployeeId("EMP001");
    }

    // Test updateCityAuthority endpoint
    @Test
    void testUpdateCityAuthority_Positive() throws Exception {
        // Arrange
        when(cityAuthorityService.updateCityAuthority(anyString(), any(CityAuthorityRequest.class)))
                .thenReturn("City Authority updated successfully");

        // Act & Assert
        mockMvc.perform(put("/api/city-authorities/{id}", "ca1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCityAuthorityRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("City Authority updated successfully")));

        verify(cityAuthorityService, times(1)).updateCityAuthority(anyString(), any(CityAuthorityRequest.class));
    }

    @Test
    void testUpdateCityAuthority_RuntimeException() throws Exception {
        // Arrange
        when(cityAuthorityService.updateCityAuthority(anyString(), any(CityAuthorityRequest.class)))
                .thenThrow(new RuntimeException("City Authority not found with id: invalidCA"));

        // Act & Assert
        mockMvc.perform(put("/api/city-authorities/{id}", "invalidCA")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCityAuthorityRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Failed to update city authority: City Authority not found with id: invalidCA")));

        verify(cityAuthorityService, times(1)).updateCityAuthority(anyString(), any(CityAuthorityRequest.class));
    }

    @Test
    void testUpdateCityAuthority_InternalException() throws Exception {
        // Arrange
        when(cityAuthorityService.updateCityAuthority(anyString(), any(CityAuthorityRequest.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        mockMvc.perform(put("/api/city-authorities/{id}", "ca1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCityAuthorityRequest)))
                .andExpect(status().isBadRequest()) // RuntimeException is caught and converted to 400
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Failed to update city authority: Unexpected error")));

        verify(cityAuthorityService, times(1)).updateCityAuthority(anyString(), any(CityAuthorityRequest.class));
    }

    // Test deleteCityAuthority endpoint
    @Test
    void testDeleteCityAuthority_Positive() throws Exception {
        // Arrange
        when(cityAuthorityService.deleteCityAuthority("ca1")).thenReturn("City Authority deleted successfully");

        // Act & Assert
        mockMvc.perform(delete("/api/city-authorities/{id}", "ca1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("City Authority deleted successfully")));

        verify(cityAuthorityService, times(1)).deleteCityAuthority("ca1");
    }

    @Test
    void testDeleteCityAuthority_RuntimeException() throws Exception {
        // Arrange
        when(cityAuthorityService.deleteCityAuthority("invalidCA"))
                .thenThrow(new RuntimeException("City Authority not found with id: invalidCA"));

        // Act & Assert
        mockMvc.perform(delete("/api/city-authorities/{id}", "invalidCA"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Failed to delete city authority: City Authority not found with id: invalidCA")));

        verify(cityAuthorityService, times(1)).deleteCityAuthority("invalidCA");
    }

    @Test
    void testDeleteCityAuthority_InternalException() throws Exception {
        // Arrange
        when(cityAuthorityService.deleteCityAuthority("ca1"))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        mockMvc.perform(delete("/api/city-authorities/{id}", "ca1"))
                .andExpect(status().isBadRequest()) // RuntimeException is caught and converted to 400
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Failed to delete city authority: Unexpected error")));

        verify(cityAuthorityService, times(1)).deleteCityAuthority("ca1");
    }

    // Edge case tests
    @Test
    void testGetCityAuthorityById_InvalidId() throws Exception {
        // Act & Assert
        // When ID is empty, Spring returns 404 before even calling the controller method
        mockMvc.perform(get("/api/city-authorities/{id}", ""))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateCityAuthority_InvalidId() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/city-authorities/{id}", "")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCityAuthorityRequest)))
                .andExpect(status().isNotFound());
        // Note: When ID is empty, Spring returns 404 before even calling the controller method
    }

    @Test
    void testDeleteCityAuthority_InvalidId() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/city-authorities/{id}", ""))
                .andExpect(status().isNotFound());
        // Note: When ID is empty, Spring returns 404 before even calling the controller method
    }
}