package com.swms.service;

import com.swms.dto.CityAuthorityRequest;
import com.swms.model.CityAuthority;
import com.swms.repository.CityAuthorityRepository;
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
class CityAuthorityServiceTest {

    @Mock
    private CityAuthorityRepository cityAuthorityRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CityAuthorityService cityAuthorityService;

    private CityAuthority testCityAuthority;
    private CityAuthorityRequest testCityAuthorityRequest;

    @BeforeEach
    void setUp() {
        // Initialize test data
        testCityAuthority = new CityAuthority();
        testCityAuthority.setUserId("ca1");
        testCityAuthority.setName("John Smith");
        testCityAuthority.setEmail("john.smith@example.com");
        testCityAuthority.setPhone("1234567890");
        testCityAuthority.setPassword("encodedPassword");
        testCityAuthority.setUserType("CITY_AUTHORITY");
        testCityAuthority.setEmployeeId("EMP001");
        testCityAuthority.setDepartment("Waste Management");
        testCityAuthority.setCreatedAt(LocalDateTime.now());
        testCityAuthority.setUpdatedAt(LocalDateTime.now());
        testCityAuthority.setEnabled(true);

        testCityAuthorityRequest = new CityAuthorityRequest();
        testCityAuthorityRequest.setName("John Smith");
        testCityAuthorityRequest.setEmail("john.smith@example.com");
        testCityAuthorityRequest.setPhone("1234567890");
        testCityAuthorityRequest.setPassword("password123");
        testCityAuthorityRequest.setEmployeeId("EMP001");
        testCityAuthorityRequest.setDepartment("Waste Management");
    }

    // Test saveCityAuthority method - Positive Cases
    @Test
    void testSaveCityAuthority_Positive() {
        // Arrange
        when(cityAuthorityRepository.existsByEmail(testCityAuthorityRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(testCityAuthorityRequest.getPassword())).thenReturn("encodedPassword");
        when(cityAuthorityRepository.save(any(CityAuthority.class))).thenReturn(testCityAuthority);

        // Act
        String result = cityAuthorityService.saveCityAuthority(testCityAuthorityRequest);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("City Authority created with ID:"));
        verify(cityAuthorityRepository).existsByEmail(testCityAuthorityRequest.getEmail());
        verify(passwordEncoder).encode(testCityAuthorityRequest.getPassword());
        verify(cityAuthorityRepository).save(any(CityAuthority.class));
    }

    // Test saveCityAuthority method - Negative Cases
    @Test
    void testSaveCityAuthority_EmailAlreadyExists() {
        // Arrange
        when(cityAuthorityRepository.existsByEmail(testCityAuthorityRequest.getEmail())).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cityAuthorityService.saveCityAuthority(testCityAuthorityRequest);
        });

        assertEquals("Email is already registered", exception.getMessage());
        verify(cityAuthorityRepository).existsByEmail(testCityAuthorityRequest.getEmail());
        verify(cityAuthorityRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    // Test saveCityAuthority method - Edge Cases
    @Test
    void testSaveCityAuthority_NullRequest() {
        // Act & Assert
        assertThrows(Exception.class, () -> {
            cityAuthorityService.saveCityAuthority(null);
        });
    }

    // Test getAllCityAuthorities method - Positive Cases
    @Test
    void testGetAllCityAuthorities_Positive() {
        // Arrange
        List<CityAuthority> cityAuthorities = Arrays.asList(testCityAuthority);
        when(cityAuthorityRepository.findAll()).thenReturn(cityAuthorities);

        // Act
        List<CityAuthority> result = cityAuthorityService.getAllCityAuthorities();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCityAuthority.getUserId(), result.get(0).getUserId());
        verify(cityAuthorityRepository).findAll();
    }

    // Test getAllCityAuthorities method - Edge Cases
    @Test
    void testGetAllCityAuthorities_EmptyList() {
        // Arrange
        when(cityAuthorityRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<CityAuthority> result = cityAuthorityService.getAllCityAuthorities();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(cityAuthorityRepository).findAll();
    }

    // Test getCityAuthorityById method - Positive Cases
    @Test
    void testGetCityAuthorityById_Positive() {
        // Arrange
        when(cityAuthorityRepository.findById("ca1")).thenReturn(Optional.of(testCityAuthority));

        // Act
        Optional<CityAuthority> result = cityAuthorityService.getCityAuthorityById("ca1");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testCityAuthority.getUserId(), result.get().getUserId());
        verify(cityAuthorityRepository).findById("ca1");
    }

    // Test getCityAuthorityById method - Negative Cases
    @Test
    void testGetCityAuthorityById_NotFound() {
        // Arrange
        when(cityAuthorityRepository.findById("invalidCA")).thenReturn(Optional.empty());

        // Act
        Optional<CityAuthority> result = cityAuthorityService.getCityAuthorityById("invalidCA");

        // Assert
        assertFalse(result.isPresent());
        verify(cityAuthorityRepository).findById("invalidCA");
    }

    // Test getCityAuthorityById method - Edge Cases
    @Test
    void testGetCityAuthorityById_NullId() {
        // Arrange
        when(cityAuthorityRepository.findById(null)).thenReturn(Optional.empty());

        // Act
        Optional<CityAuthority> result = cityAuthorityService.getCityAuthorityById(null);

        // Assert
        assertFalse(result.isPresent());
        verify(cityAuthorityRepository).findById(null);
    }

    // Test getCityAuthorityByEmail method - Positive Cases
    @Test
    void testGetCityAuthorityByEmail_Positive() {
        // Arrange
        when(cityAuthorityRepository.findByEmail("john.smith@example.com")).thenReturn(Optional.of(testCityAuthority));

        // Act
        Optional<CityAuthority> result = cityAuthorityService.getCityAuthorityByEmail("john.smith@example.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testCityAuthority.getEmail(), result.get().getEmail());
        verify(cityAuthorityRepository).findByEmail("john.smith@example.com");
    }

    // Test getCityAuthorityByEmail method - Negative Cases
    @Test
    void testGetCityAuthorityByEmail_NotFound() {
        // Arrange
        when(cityAuthorityRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act
        Optional<CityAuthority> result = cityAuthorityService.getCityAuthorityByEmail("nonexistent@example.com");

        // Assert
        assertFalse(result.isPresent());
        verify(cityAuthorityRepository).findByEmail("nonexistent@example.com");
    }

    // Test getCityAuthorityByEmployeeId method - Positive Cases
    @Test
    void testGetCityAuthorityByEmployeeId_Positive() {
        // Arrange
        when(cityAuthorityRepository.findByEmployeeId("EMP001")).thenReturn(Optional.of(testCityAuthority));

        // Act
        Optional<CityAuthority> result = cityAuthorityService.getCityAuthorityByEmployeeId("EMP001");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testCityAuthority.getEmployeeId(), result.get().getEmployeeId());
        verify(cityAuthorityRepository).findByEmployeeId("EMP001");
    }

    // Test getCityAuthorityByEmployeeId method - Negative Cases
    @Test
    void testGetCityAuthorityByEmployeeId_NotFound() {
        // Arrange
        when(cityAuthorityRepository.findByEmployeeId("INVALID")).thenReturn(Optional.empty());

        // Act
        Optional<CityAuthority> result = cityAuthorityService.getCityAuthorityByEmployeeId("INVALID");

        // Assert
        assertFalse(result.isPresent());
        verify(cityAuthorityRepository).findByEmployeeId("INVALID");
    }

    // Test updateCityAuthority method - Positive Cases
    @Test
    void testUpdateCityAuthority_Positive() {
        // Arrange
        CityAuthority existingCityAuthority = new CityAuthority();
        existingCityAuthority.setUserId("ca1");
        existingCityAuthority.setName("Old Name");
        existingCityAuthority.setEmail("old@example.com");
        existingCityAuthority.setPhone("0987654321");
        existingCityAuthority.setEmployeeId("OLD001");
        existingCityAuthority.setDepartment("Old Department");
        existingCityAuthority.setUpdatedAt(LocalDateTime.now().minusDays(1));

        when(cityAuthorityRepository.findById("ca1")).thenReturn(Optional.of(existingCityAuthority));
        when(cityAuthorityRepository.save(any(CityAuthority.class))).thenReturn(existingCityAuthority);

        // Act
        String result = cityAuthorityService.updateCityAuthority("ca1", testCityAuthorityRequest);

        // Assert
        assertEquals("City Authority updated successfully", result);
        assertEquals(testCityAuthorityRequest.getName(), existingCityAuthority.getName());
        assertEquals(testCityAuthorityRequest.getEmail(), existingCityAuthority.getEmail());
        assertEquals(testCityAuthorityRequest.getPhone(), existingCityAuthority.getPhone());
        assertEquals(testCityAuthorityRequest.getEmployeeId(), existingCityAuthority.getEmployeeId());
        assertEquals(testCityAuthorityRequest.getDepartment(), existingCityAuthority.getDepartment());
        assertNotNull(existingCityAuthority.getUpdatedAt());
        verify(cityAuthorityRepository).findById("ca1");
        verify(cityAuthorityRepository).save(existingCityAuthority);
    }

    // Test updateCityAuthority method - Negative Cases
    @Test
    void testUpdateCityAuthority_NotFound() {
        // Arrange
        when(cityAuthorityRepository.findById("invalidCA")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cityAuthorityService.updateCityAuthority("invalidCA", testCityAuthorityRequest);
        });

        assertEquals("City Authority not found with id: invalidCA", exception.getMessage());
        verify(cityAuthorityRepository).findById("invalidCA");
        verify(cityAuthorityRepository, never()).save(any());
    }

    // Test updateCityAuthority method - Edge Cases
    @Test
    void testUpdateCityAuthority_NullId() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cityAuthorityService.updateCityAuthority(null, testCityAuthorityRequest);
        });

        assertTrue(exception.getMessage().contains("City Authority not found with id:"));
        verify(cityAuthorityRepository).findById(null);
        verify(cityAuthorityRepository, never()).save(any());
    }

    @Test
    void testUpdateCityAuthority_NullRequest() {
        // Arrange
        when(cityAuthorityRepository.findById("ca1")).thenReturn(Optional.of(testCityAuthority));

        // Act & Assert
        assertThrows(Exception.class, () -> {
            cityAuthorityService.updateCityAuthority("ca1", null);
        });
    }

    // Test deleteCityAuthority method - Positive Cases
    @Test
    void testDeleteCityAuthority_Positive() {
        // Arrange
        when(cityAuthorityRepository.existsById("ca1")).thenReturn(true);

        // Act
        String result = cityAuthorityService.deleteCityAuthority("ca1");

        // Assert
        assertEquals("City Authority deleted successfully", result);
        verify(cityAuthorityRepository).existsById("ca1");
        verify(cityAuthorityRepository).deleteById("ca1");
    }

    // Test deleteCityAuthority method - Negative Cases
    @Test
    void testDeleteCityAuthority_NotFound() {
        // Arrange
        when(cityAuthorityRepository.existsById("invalidCA")).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cityAuthorityService.deleteCityAuthority("invalidCA");
        });

        assertEquals("City Authority not found with id: invalidCA", exception.getMessage());
        verify(cityAuthorityRepository).existsById("invalidCA");
        verify(cityAuthorityRepository, never()).deleteById(anyString());
    }

    // Test deleteCityAuthority method - Edge Cases
    @Test
    void testDeleteCityAuthority_NullId() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cityAuthorityService.deleteCityAuthority(null);
        });

        assertTrue(exception.getMessage().contains("City Authority not found with id:"));
        verify(cityAuthorityRepository).existsById(null);
        verify(cityAuthorityRepository, never()).deleteById(anyString());
    }

    // Error case tests
    @Test
    void testSaveCityAuthority_RepositoryException() {
        // Arrange
        when(cityAuthorityRepository.existsByEmail(testCityAuthorityRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(testCityAuthorityRequest.getPassword())).thenReturn("encodedPassword");
        when(cityAuthorityRepository.save(any(CityAuthority.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            cityAuthorityService.saveCityAuthority(testCityAuthorityRequest);
        });

        verify(cityAuthorityRepository).existsByEmail(testCityAuthorityRequest.getEmail());
        verify(passwordEncoder).encode(testCityAuthorityRequest.getPassword());
        verify(cityAuthorityRepository).save(any(CityAuthority.class));
    }

    @Test
    void testGetAllCityAuthorities_RepositoryException() {
        // Arrange
        when(cityAuthorityRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            cityAuthorityService.getAllCityAuthorities();
        });

        verify(cityAuthorityRepository).findAll();
    }

    @Test
    void testUpdateCityAuthority_RepositoryException() {
        // Arrange
        when(cityAuthorityRepository.findById("ca1")).thenReturn(Optional.of(testCityAuthority));
        when(cityAuthorityRepository.save(any(CityAuthority.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            cityAuthorityService.updateCityAuthority("ca1", testCityAuthorityRequest);
        });

        verify(cityAuthorityRepository).findById("ca1");
        verify(cityAuthorityRepository).save(any(CityAuthority.class));
    }

    @Test
    void testDeleteCityAuthority_RepositoryException() {
        // Arrange
        when(cityAuthorityRepository.existsById("ca1")).thenReturn(true);
        doThrow(new RuntimeException("Database error")).when(cityAuthorityRepository).deleteById("ca1");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            cityAuthorityService.deleteCityAuthority("ca1");
        });

        verify(cityAuthorityRepository).existsById("ca1");
        verify(cityAuthorityRepository).deleteById("ca1");
    }
}