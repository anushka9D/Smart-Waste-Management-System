package com.swms.service;

import com.swms.dto.WasteCollectionStaffRequest;
import com.swms.model.WasteCollectionStaff;
import com.swms.repository.WasteCollectionStaffRepository;
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
class WasteCollectionStaffServiceTest {

    @Mock
    private WasteCollectionStaffRepository wasteCollectionStaffRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private WasteCollectionStaffService wasteCollectionStaffService;

    private WasteCollectionStaff testStaff;
    private WasteCollectionStaffRequest testStaffRequest;

    @BeforeEach
    void setUp() {
        // Initialize test data
        testStaff = new WasteCollectionStaff();
        testStaff.setUserId("staff1");
        testStaff.setName("John Staff");
        testStaff.setEmail("john.staff@example.com");
        testStaff.setPhone("1234567890");
        testStaff.setPassword("encodedPassword");
        testStaff.setUserType("WASTE_COLLECTION_STAFF");
        testStaff.setEmployeeId("EMP001");
        testStaff.setRouteArea("North Area");
        testStaff.setCreatedAt(LocalDateTime.now());
        testStaff.setUpdatedAt(LocalDateTime.now());
        testStaff.setEnabled(true);

        testStaffRequest = new WasteCollectionStaffRequest();
        testStaffRequest.setName("John Staff");
        testStaffRequest.setEmail("john.staff@example.com");
        testStaffRequest.setPhone("1234567890");
        testStaffRequest.setPassword("password123");
        testStaffRequest.setEmployeeId("EMP001");
        testStaffRequest.setRouteArea("North Area");
    }

    // Test saveWasteCollectionStaff method - Positive Cases
    @Test
    void testSaveWasteCollectionStaff_Positive() {
        // Arrange
        when(wasteCollectionStaffRepository.existsByEmail(testStaffRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(testStaffRequest.getPassword())).thenReturn("encodedPassword");
        when(wasteCollectionStaffRepository.save(any(WasteCollectionStaff.class))).thenReturn(testStaff);

        // Act
        String result = wasteCollectionStaffService.saveWasteCollectionStaff(testStaffRequest);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Waste Collection Staff created with ID:"));
        verify(wasteCollectionStaffRepository).existsByEmail(testStaffRequest.getEmail());
        verify(passwordEncoder).encode(testStaffRequest.getPassword());
        verify(wasteCollectionStaffRepository).save(any(WasteCollectionStaff.class));
    }

    // Test saveWasteCollectionStaff method - Negative Cases
    @Test
    void testSaveWasteCollectionStaff_EmailAlreadyExists() {
        // Arrange
        when(wasteCollectionStaffRepository.existsByEmail(testStaffRequest.getEmail())).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            wasteCollectionStaffService.saveWasteCollectionStaff(testStaffRequest);
        });

        assertEquals("Email is already registered", exception.getMessage());
        verify(wasteCollectionStaffRepository).existsByEmail(testStaffRequest.getEmail());
        verify(wasteCollectionStaffRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    // Test saveWasteCollectionStaff method - Edge Cases
    @Test
    void testSaveWasteCollectionStaff_NullRequest() {
        // Act & Assert
        assertThrows(Exception.class, () -> {
            wasteCollectionStaffService.saveWasteCollectionStaff(null);
        });
    }

    // Test getAllWasteCollectionStaff method - Positive Cases
    @Test
    void testGetAllWasteCollectionStaff_Positive() {
        // Arrange
        List<WasteCollectionStaff> staffList = Arrays.asList(testStaff);
        when(wasteCollectionStaffRepository.findAll()).thenReturn(staffList);

        // Act
        List<WasteCollectionStaff> result = wasteCollectionStaffService.getAllWasteCollectionStaff();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testStaff.getUserId(), result.get(0).getUserId());
        verify(wasteCollectionStaffRepository).findAll();
    }

    // Test getAllWasteCollectionStaff method - Edge Cases
    @Test
    void testGetAllWasteCollectionStaff_EmptyList() {
        // Arrange
        when(wasteCollectionStaffRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<WasteCollectionStaff> result = wasteCollectionStaffService.getAllWasteCollectionStaff();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(wasteCollectionStaffRepository).findAll();
    }

    // Test getWasteCollectionStaffById method - Positive Cases
    @Test
    void testGetWasteCollectionStaffById_Positive() {
        // Arrange
        when(wasteCollectionStaffRepository.findById("staff1")).thenReturn(Optional.of(testStaff));

        // Act
        Optional<WasteCollectionStaff> result = wasteCollectionStaffService.getWasteCollectionStaffById("staff1");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testStaff.getUserId(), result.get().getUserId());
        verify(wasteCollectionStaffRepository).findById("staff1");
    }

    // Test getWasteCollectionStaffById method - Negative Cases
    @Test
    void testGetWasteCollectionStaffById_NotFound() {
        // Arrange
        when(wasteCollectionStaffRepository.findById("invalidStaff")).thenReturn(Optional.empty());

        // Act
        Optional<WasteCollectionStaff> result = wasteCollectionStaffService.getWasteCollectionStaffById("invalidStaff");

        // Assert
        assertFalse(result.isPresent());
        verify(wasteCollectionStaffRepository).findById("invalidStaff");
    }

    // Test getWasteCollectionStaffById method - Edge Cases
    @Test
    void testGetWasteCollectionStaffById_NullId() {
        // Arrange
        when(wasteCollectionStaffRepository.findById(null)).thenReturn(Optional.empty());

        // Act
        Optional<WasteCollectionStaff> result = wasteCollectionStaffService.getWasteCollectionStaffById(null);

        // Assert
        assertFalse(result.isPresent());
        verify(wasteCollectionStaffRepository).findById(null);
    }

    // Test getWasteCollectionStaffByEmail method - Positive Cases
    @Test
    void testGetWasteCollectionStaffByEmail_Positive() {
        // Arrange
        when(wasteCollectionStaffRepository.findByEmail("john.staff@example.com")).thenReturn(Optional.of(testStaff));

        // Act
        Optional<WasteCollectionStaff> result = wasteCollectionStaffService.getWasteCollectionStaffByEmail("john.staff@example.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testStaff.getEmail(), result.get().getEmail());
        verify(wasteCollectionStaffRepository).findByEmail("john.staff@example.com");
    }

    // Test getWasteCollectionStaffByEmail method - Negative Cases
    @Test
    void testGetWasteCollectionStaffByEmail_NotFound() {
        // Arrange
        when(wasteCollectionStaffRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act
        Optional<WasteCollectionStaff> result = wasteCollectionStaffService.getWasteCollectionStaffByEmail("nonexistent@example.com");

        // Assert
        assertFalse(result.isPresent());
        verify(wasteCollectionStaffRepository).findByEmail("nonexistent@example.com");
    }

    // Test getWasteCollectionStaffByEmployeeId method - Positive Cases
    @Test
    void testGetWasteCollectionStaffByEmployeeId_Positive() {
        // Arrange
        when(wasteCollectionStaffRepository.findByEmployeeId("EMP001")).thenReturn(Optional.of(testStaff));

        // Act
        Optional<WasteCollectionStaff> result = wasteCollectionStaffService.getWasteCollectionStaffByEmployeeId("EMP001");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testStaff.getEmployeeId(), result.get().getEmployeeId());
        verify(wasteCollectionStaffRepository).findByEmployeeId("EMP001");
    }

    // Test getWasteCollectionStaffByEmployeeId method - Negative Cases
    @Test
    void testGetWasteCollectionStaffByEmployeeId_NotFound() {
        // Arrange
        when(wasteCollectionStaffRepository.findByEmployeeId("INVALID")).thenReturn(Optional.empty());

        // Act
        Optional<WasteCollectionStaff> result = wasteCollectionStaffService.getWasteCollectionStaffByEmployeeId("INVALID");

        // Assert
        assertFalse(result.isPresent());
        verify(wasteCollectionStaffRepository).findByEmployeeId("INVALID");
    }

    // Test updateWasteCollectionStaff method - Positive Cases
    @Test
    void testUpdateWasteCollectionStaff_Positive() {
        // Arrange
        WasteCollectionStaff existingStaff = new WasteCollectionStaff();
        existingStaff.setUserId("staff1");
        existingStaff.setName("Old Name");
        existingStaff.setEmail("old@example.com");
        existingStaff.setPhone("0987654321");
        existingStaff.setEmployeeId("OLD001");
        existingStaff.setRouteArea("Old Area");
        existingStaff.setUpdatedAt(LocalDateTime.now().minusDays(1));

        when(wasteCollectionStaffRepository.findById("staff1")).thenReturn(Optional.of(existingStaff));
        when(wasteCollectionStaffRepository.save(any(WasteCollectionStaff.class))).thenReturn(existingStaff);

        // Act
        String result = wasteCollectionStaffService.updateWasteCollectionStaff("staff1", testStaffRequest);

        // Assert
        assertEquals("Waste Collection Staff updated successfully", result);
        assertEquals(testStaffRequest.getName(), existingStaff.getName());
        assertEquals(testStaffRequest.getEmail(), existingStaff.getEmail());
        assertEquals(testStaffRequest.getPhone(), existingStaff.getPhone());
        assertEquals(testStaffRequest.getEmployeeId(), existingStaff.getEmployeeId());
        assertEquals(testStaffRequest.getRouteArea(), existingStaff.getRouteArea());
        assertNotNull(existingStaff.getUpdatedAt());
        verify(wasteCollectionStaffRepository).findById("staff1");
        verify(wasteCollectionStaffRepository).save(existingStaff);
    }

    // Test updateWasteCollectionStaff method - Negative Cases
    @Test
    void testUpdateWasteCollectionStaff_NotFound() {
        // Arrange
        when(wasteCollectionStaffRepository.findById("invalidStaff")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            wasteCollectionStaffService.updateWasteCollectionStaff("invalidStaff", testStaffRequest);
        });

        assertEquals("Waste Collection Staff not found with id: invalidStaff", exception.getMessage());
        verify(wasteCollectionStaffRepository).findById("invalidStaff");
        verify(wasteCollectionStaffRepository, never()).save(any());
    }

    // Test updateWasteCollectionStaff method - Edge Cases
    @Test
    void testUpdateWasteCollectionStaff_NullId() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            wasteCollectionStaffService.updateWasteCollectionStaff(null, testStaffRequest);
        });

        assertTrue(exception.getMessage().contains("Waste Collection Staff not found with id:"));
        verify(wasteCollectionStaffRepository).findById(null);
        verify(wasteCollectionStaffRepository, never()).save(any());
    }

    @Test
    void testUpdateWasteCollectionStaff_NullRequest() {
        // Arrange
        when(wasteCollectionStaffRepository.findById("staff1")).thenReturn(Optional.of(testStaff));

        // Act & Assert
        assertThrows(Exception.class, () -> {
            wasteCollectionStaffService.updateWasteCollectionStaff("staff1", null);
        });
    }

    // Test deleteWasteCollectionStaff method - Positive Cases
    @Test
    void testDeleteWasteCollectionStaff_Positive() {
        // Arrange
        when(wasteCollectionStaffRepository.existsById("staff1")).thenReturn(true);

        // Act
        String result = wasteCollectionStaffService.deleteWasteCollectionStaff("staff1");

        // Assert
        assertEquals("Waste Collection Staff deleted successfully", result);
        verify(wasteCollectionStaffRepository).existsById("staff1");
        verify(wasteCollectionStaffRepository).deleteById("staff1");
    }

    // Test deleteWasteCollectionStaff method - Negative Cases
    @Test
    void testDeleteWasteCollectionStaff_NotFound() {
        // Arrange
        when(wasteCollectionStaffRepository.existsById("invalidStaff")).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            wasteCollectionStaffService.deleteWasteCollectionStaff("invalidStaff");
        });

        assertEquals("Waste Collection Staff not found with id: invalidStaff", exception.getMessage());
        verify(wasteCollectionStaffRepository).existsById("invalidStaff");
        verify(wasteCollectionStaffRepository, never()).deleteById(anyString());
    }

    // Test deleteWasteCollectionStaff method - Edge Cases
    @Test
    void testDeleteWasteCollectionStaff_NullId() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            wasteCollectionStaffService.deleteWasteCollectionStaff(null);
        });

        assertTrue(exception.getMessage().contains("Waste Collection Staff not found with id:"));
        verify(wasteCollectionStaffRepository).existsById(null);
        verify(wasteCollectionStaffRepository, never()).deleteById(anyString());
    }

    // Error case tests
    @Test
    void testSaveWasteCollectionStaff_RepositoryException() {
        // Arrange
        when(wasteCollectionStaffRepository.existsByEmail(testStaffRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(testStaffRequest.getPassword())).thenReturn("encodedPassword");
        when(wasteCollectionStaffRepository.save(any(WasteCollectionStaff.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            wasteCollectionStaffService.saveWasteCollectionStaff(testStaffRequest);
        });

        verify(wasteCollectionStaffRepository).existsByEmail(testStaffRequest.getEmail());
        verify(passwordEncoder).encode(testStaffRequest.getPassword());
        verify(wasteCollectionStaffRepository).save(any(WasteCollectionStaff.class));
    }

    @Test
    void testGetAllWasteCollectionStaff_RepositoryException() {
        // Arrange
        when(wasteCollectionStaffRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            wasteCollectionStaffService.getAllWasteCollectionStaff();
        });

        verify(wasteCollectionStaffRepository).findAll();
    }

    @Test
    void testUpdateWasteCollectionStaff_RepositoryException() {
        // Arrange
        when(wasteCollectionStaffRepository.findById("staff1")).thenReturn(Optional.of(testStaff));
        when(wasteCollectionStaffRepository.save(any(WasteCollectionStaff.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            wasteCollectionStaffService.updateWasteCollectionStaff("staff1", testStaffRequest);
        });

        verify(wasteCollectionStaffRepository).findById("staff1");
        verify(wasteCollectionStaffRepository).save(any(WasteCollectionStaff.class));
    }

    @Test
    void testDeleteWasteCollectionStaff_RepositoryException() {
        // Arrange
        when(wasteCollectionStaffRepository.existsById("staff1")).thenReturn(true);
        doThrow(new RuntimeException("Database error")).when(wasteCollectionStaffRepository).deleteById("staff1");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            wasteCollectionStaffService.deleteWasteCollectionStaff("staff1");
        });

        verify(wasteCollectionStaffRepository).existsById("staff1");
        verify(wasteCollectionStaffRepository).deleteById("staff1");
    }
}