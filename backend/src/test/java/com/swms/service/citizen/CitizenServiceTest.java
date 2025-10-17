package com.swms.service.citizen;

import com.swms.dto.citizen.CitizenRequest;
import com.swms.model.citizen.Citizen;
import com.swms.repository.citizen.CitizenRepository;
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
class CitizenServiceTest {

    @Mock
    private CitizenRepository citizenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CitizenService citizenService;

    private Citizen testCitizen;
    private CitizenRequest testCitizenRequest;

    @BeforeEach
    void setUp() {
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

    // Test saveCitizen method - Positive Cases
    @Test
    void testSaveCitizen_Positive() {
        // Arrange
        when(citizenRepository.existsByEmail(testCitizenRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(testCitizenRequest.getPassword())).thenReturn("encodedPassword");
        when(citizenRepository.save(any(Citizen.class))).thenReturn(testCitizen);

        // Act
        String result = citizenService.saveCitizen(testCitizenRequest);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Citizen created with ID:"));
        verify(citizenRepository).existsByEmail(testCitizenRequest.getEmail());
        verify(passwordEncoder).encode(testCitizenRequest.getPassword());
        verify(citizenRepository).save(any(Citizen.class));
    }

    // Test saveCitizen method - Negative Cases
    @Test
    void testSaveCitizen_EmailAlreadyExists() {
        // Arrange
        when(citizenRepository.existsByEmail(testCitizenRequest.getEmail())).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            citizenService.saveCitizen(testCitizenRequest);
        });

        assertEquals("Email is already registered", exception.getMessage());
        verify(citizenRepository).existsByEmail(testCitizenRequest.getEmail());
        verify(citizenRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    // Test saveCitizen method - Edge Cases
    @Test
    void testSaveCitizen_NullRequest() {
        // Act & Assert
        assertThrows(Exception.class, () -> {
            citizenService.saveCitizen(null);
        });
    }

    // Test getAllCitizens method - Positive Cases
    @Test
    void testGetAllCitizens_Positive() {
        // Arrange
        List<Citizen> citizens = Arrays.asList(testCitizen);
        when(citizenRepository.findAll()).thenReturn(citizens);

        // Act
        List<Citizen> result = citizenService.getAllCitizens();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCitizen.getUserId(), result.get(0).getUserId());
        verify(citizenRepository).findAll();
    }

    // Test getAllCitizens method - Edge Cases
    @Test
    void testGetAllCitizens_EmptyList() {
        // Arrange
        when(citizenRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Citizen> result = citizenService.getAllCitizens();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(citizenRepository).findAll();
    }

    // Test getCitizenById method - Positive Cases
    @Test
    void testGetCitizenById_Positive() {
        // Arrange
        when(citizenRepository.findById("citizen1")).thenReturn(Optional.of(testCitizen));

        // Act
        Optional<Citizen> result = citizenService.getCitizenById("citizen1");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testCitizen.getUserId(), result.get().getUserId());
        verify(citizenRepository).findById("citizen1");
    }

    // Test getCitizenById method - Negative Cases
    @Test
    void testGetCitizenById_NotFound() {
        // Arrange
        when(citizenRepository.findById("invalidCitizen")).thenReturn(Optional.empty());

        // Act
        Optional<Citizen> result = citizenService.getCitizenById("invalidCitizen");

        // Assert
        assertFalse(result.isPresent());
        verify(citizenRepository).findById("invalidCitizen");
    }

    // Test getCitizenById method - Edge Cases
    @Test
    void testGetCitizenById_NullId() {
        // Arrange
        when(citizenRepository.findById(null)).thenReturn(Optional.empty());

        // Act
        Optional<Citizen> result = citizenService.getCitizenById(null);

        // Assert
        assertFalse(result.isPresent());
        verify(citizenRepository).findById(null);
    }

    // Test getCitizenByName method - Positive Cases
    @Test
    void testGetCitizenByName_Positive() {
        // Arrange
        when(citizenRepository.findByName("John Doe")).thenReturn(Optional.of(testCitizen));

        // Act
        Optional<Citizen> result = citizenService.getCitizenByName("John Doe");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testCitizen.getName(), result.get().getName());
        verify(citizenRepository).findByName("John Doe");
    }

    // Test getCitizenByName method - Negative Cases
    @Test
    void testGetCitizenByName_NotFound() {
        // Arrange
        when(citizenRepository.findByName("Nonexistent")).thenReturn(Optional.empty());

        // Act
        Optional<Citizen> result = citizenService.getCitizenByName("Nonexistent");

        // Assert
        assertFalse(result.isPresent());
        verify(citizenRepository).findByName("Nonexistent");
    }

    // Test getCitizenByEmail method - Positive Cases
    @Test
    void testGetCitizenByEmail_Positive() {
        // Arrange
        when(citizenRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testCitizen));

        // Act
        Optional<Citizen> result = citizenService.getCitizenByEmail("john.doe@example.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testCitizen.getEmail(), result.get().getEmail());
        verify(citizenRepository).findByEmail("john.doe@example.com");
    }

    // Test getCitizenByEmail method - Negative Cases
    @Test
    void testGetCitizenByEmail_NotFound() {
        // Arrange
        when(citizenRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act
        Optional<Citizen> result = citizenService.getCitizenByEmail("nonexistent@example.com");

        // Assert
        assertFalse(result.isPresent());
        verify(citizenRepository).findByEmail("nonexistent@example.com");
    }

    // Test updateCitizen method - Positive Cases
    @Test
    void testUpdateCitizen_Positive() {
        // Arrange
        Citizen existingCitizen = new Citizen();
        existingCitizen.setUserId("citizen1");
        existingCitizen.setName("Old Name");
        existingCitizen.setEmail("old@example.com");
        existingCitizen.setPhone("0987654321");
        existingCitizen.setUpdatedAt(LocalDateTime.now().minusDays(1));

        when(citizenRepository.findById("citizen1")).thenReturn(Optional.of(existingCitizen));
        when(citizenRepository.save(any(Citizen.class))).thenReturn(existingCitizen);

        // Act
        String result = citizenService.updateCitizen("citizen1", testCitizenRequest);

        // Assert
        assertEquals("Citizen updated successfully", result);
        assertEquals(testCitizenRequest.getName(), existingCitizen.getName());
        assertEquals(testCitizenRequest.getEmail(), existingCitizen.getEmail());
        assertEquals(testCitizenRequest.getPhone(), existingCitizen.getPhone());
        assertNotNull(existingCitizen.getUpdatedAt());
        verify(citizenRepository).findById("citizen1");
        verify(citizenRepository).save(existingCitizen);
    }

    // Test updateCitizen method - Negative Cases
    @Test
    void testUpdateCitizen_NotFound() {
        // Arrange
        when(citizenRepository.findById("invalidCitizen")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            citizenService.updateCitizen("invalidCitizen", testCitizenRequest);
        });

        assertEquals("Citizen not found with id: invalidCitizen", exception.getMessage());
        verify(citizenRepository).findById("invalidCitizen");
        verify(citizenRepository, never()).save(any());
    }

    // Test updateCitizen method - Edge Cases
    @Test
    void testUpdateCitizen_NullId() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            citizenService.updateCitizen(null, testCitizenRequest);
        });

        assertTrue(exception.getMessage().contains("Citizen not found with id:"));
        verify(citizenRepository).findById(null);
        verify(citizenRepository, never()).save(any());
    }

    @Test
    void testUpdateCitizen_NullRequest() {
        // Arrange
        when(citizenRepository.findById("citizen1")).thenReturn(Optional.of(testCitizen));

        // Act & Assert
        assertThrows(Exception.class, () -> {
            citizenService.updateCitizen("citizen1", null);
        });
    }

    // Test deleteCitizen method - Positive Cases
    @Test
    void testDeleteCitizen_Positive() {
        // Arrange
        when(citizenRepository.existsById("citizen1")).thenReturn(true);

        // Act
        String result = citizenService.deleteCitizen("citizen1");

        // Assert
        assertEquals("Citizen deleted successfully", result);
        verify(citizenRepository).existsById("citizen1");
        verify(citizenRepository).deleteById("citizen1");
    }

    // Test deleteCitizen method - Negative Cases
    @Test
    void testDeleteCitizen_NotFound() {
        // Arrange
        when(citizenRepository.existsById("invalidCitizen")).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            citizenService.deleteCitizen("invalidCitizen");
        });

        assertEquals("Citizen not found with id: invalidCitizen", exception.getMessage());
        verify(citizenRepository).existsById("invalidCitizen");
        verify(citizenRepository, never()).deleteById(anyString());
    }

    // Test deleteCitizen method - Edge Cases
    @Test
    void testDeleteCitizen_NullId() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            citizenService.deleteCitizen(null);
        });

        assertTrue(exception.getMessage().contains("Citizen not found with id:"));
        verify(citizenRepository).existsById(null);
        verify(citizenRepository, never()).deleteById(anyString());
    }

    // Error case tests
    @Test
    void testSaveCitizen_RepositoryException() {
        // Arrange
        when(citizenRepository.existsByEmail(testCitizenRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(testCitizenRequest.getPassword())).thenReturn("encodedPassword");
        when(citizenRepository.save(any(Citizen.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            citizenService.saveCitizen(testCitizenRequest);
        });

        verify(citizenRepository).existsByEmail(testCitizenRequest.getEmail());
        verify(passwordEncoder).encode(testCitizenRequest.getPassword());
        verify(citizenRepository).save(any(Citizen.class));
    }

    @Test
    void testGetAllCitizens_RepositoryException() {
        // Arrange
        when(citizenRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            citizenService.getAllCitizens();
        });

        verify(citizenRepository).findAll();
    }

    @Test
    void testUpdateCitizen_RepositoryException() {
        // Arrange
        when(citizenRepository.findById("citizen1")).thenReturn(Optional.of(testCitizen));
        when(citizenRepository.save(any(Citizen.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            citizenService.updateCitizen("citizen1", testCitizenRequest);
        });

        verify(citizenRepository).findById("citizen1");
        verify(citizenRepository).save(any(Citizen.class));
    }

    @Test
    void testDeleteCitizen_RepositoryException() {
        // Arrange
        when(citizenRepository.existsById("citizen1")).thenReturn(true);
        doThrow(new RuntimeException("Database error")).when(citizenRepository).deleteById("citizen1");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            citizenService.deleteCitizen("citizen1");
        });

        verify(citizenRepository).existsById("citizen1");
        verify(citizenRepository).deleteById("citizen1");
    }
}