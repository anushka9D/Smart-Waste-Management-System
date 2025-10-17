package com.swms.service.citizen;

import com.swms.dto.citizen.CitizenWasteDisposalMultipartDTO;
import com.swms.dto.citizen.CitizenRequestStatusUpdateDTO;
import com.swms.model.GPSLocation;
import com.swms.model.citizen.*;
import com.swms.repository.citizen.CitizenWasteDisposalRequestRepository;
import com.swms.repository.citizen.CitizenRequestUpdateRepository;
import com.swms.repository.citizen.CitizenRepository;
import com.swms.service.CloudinaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CitizenWasteDisposalRequestServiceTest {

    @Mock
    private CitizenWasteDisposalRequestRepository requestRepository;

    @Mock
    private CitizenRequestUpdateRepository updateRepository;

    @Mock
    private CitizenRepository citizenRepository;

    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private CitizenWasteDisposalRequestService requestService;

    private Citizen testCitizen;
    private CitizenWasteDisposalRequest testRequest;
    private CitizenWasteDisposalMultipartDTO testRequestDTO;

    @BeforeEach
    void setUp() {
        // Initialize test citizen
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

        // Initialize test request
        testRequest = new CitizenWasteDisposalRequest();
        testRequest.setRequestId("REQ-12345678");
        testRequest.setCitizenId("citizen1");
        testRequest.setCitizenName("John Doe");
        testRequest.setCitizenEmail("john.doe@example.com");
        testRequest.setCategory(CitizenRequestCategory.OVERFLOWING_BIN);
        testRequest.setDescription("Bin is overflowing");
        testRequest.setBinId("BIN-001");
        testRequest.setCoordinates(new GPSLocation(40.7128, -74.0060));
        testRequest.setAddress("123 Main St");
        testRequest.setPhotoUrl("http://example.com/photo.jpg");
        testRequest.setStatus(CitizenRequestStatus.SUBMITTED);
        testRequest.setSubmittedAt(LocalDateTime.now());
        testRequest.setUpdatedAt(LocalDateTime.now());

        // Initialize test DTO
        testRequestDTO = new CitizenWasteDisposalMultipartDTO();
        testRequestDTO.setCategory(CitizenRequestCategory.OVERFLOWING_BIN);
        testRequestDTO.setDescription("Bin is overflowing");
        testRequestDTO.setBinId("BIN-001");
        testRequestDTO.setAddress("123 Main St");
        testRequestDTO.setLatitude(40.7128);
        testRequestDTO.setLongitude(-74.0060);
    }

    // Test createRequest method - Positive Cases (without photo)
    @Test
    void testCreateRequest_Positive_WithoutPhoto() throws IOException {
        // Arrange
        when(citizenRepository.findById("citizen1")).thenReturn(Optional.of(testCitizen));
        when(requestRepository.save(any(CitizenWasteDisposalRequest.class))).thenReturn(testRequest);
        when(updateRepository.save(any(CitizenRequestUpdate.class))).thenReturn(new CitizenRequestUpdate());

        // Act
        CitizenWasteDisposalRequest result = requestService.createRequest("citizen1", testRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("REQ-12345678", result.getRequestId());
        assertEquals("citizen1", result.getCitizenId());
        assertEquals("John Doe", result.getCitizenName());
        assertEquals("john.doe@example.com", result.getCitizenEmail());
        assertEquals(CitizenRequestCategory.OVERFLOWING_BIN, result.getCategory());
        assertEquals("Bin is overflowing", result.getDescription());
        assertEquals("BIN-001", result.getBinId());
        assertNotNull(result.getCoordinates());
        assertEquals("123 Main St", result.getAddress());
        assertEquals(CitizenRequestStatus.SUBMITTED, result.getStatus());

        verify(citizenRepository).findById("citizen1");
        verify(requestRepository).save(any(CitizenWasteDisposalRequest.class));
        verify(updateRepository).save(any(CitizenRequestUpdate.class));
    }

    // Test createRequest method - Positive Cases (with photo)
    @Test
    void testCreateRequest_Positive_WithPhoto() throws IOException {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        testRequestDTO.setPhoto(mockFile);
        testRequest.setPhotoUrl("http://example.com/photo.jpg");
        
        when(citizenRepository.findById("citizen1")).thenReturn(Optional.of(testCitizen));
        when(cloudinaryService.uploadImage(mockFile)).thenReturn("http://example.com/photo.jpg");
        when(requestRepository.save(any(CitizenWasteDisposalRequest.class))).thenReturn(testRequest);
        when(updateRepository.save(any(CitizenRequestUpdate.class))).thenReturn(new CitizenRequestUpdate());

        // Act
        CitizenWasteDisposalRequest result = requestService.createRequest("citizen1", testRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("REQ-12345678", result.getRequestId());
        assertEquals("citizen1", result.getCitizenId());
        assertEquals("John Doe", result.getCitizenName());
        assertEquals("john.doe@example.com", result.getCitizenEmail());
        assertEquals(CitizenRequestCategory.OVERFLOWING_BIN, result.getCategory());
        assertEquals("Bin is overflowing", result.getDescription());
        assertEquals("BIN-001", result.getBinId());
        assertNotNull(result.getCoordinates());
        assertEquals("123 Main St", result.getAddress());
        assertEquals("http://example.com/photo.jpg", result.getPhotoUrl());
        assertEquals(CitizenRequestStatus.SUBMITTED, result.getStatus());

        verify(citizenRepository).findById("citizen1");
        verify(cloudinaryService).uploadImage(mockFile);
        verify(requestRepository).save(any(CitizenWasteDisposalRequest.class));
        verify(updateRepository).save(any(CitizenRequestUpdate.class));
    }

    // Test createRequest method - Negative Cases (Citizen not found)
    @Test
    void testCreateRequest_CitizenNotFound() {
        // Arrange
        when(citizenRepository.findById("citizen1")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            requestService.createRequest("citizen1", testRequestDTO);
        });

        assertEquals("Citizen not found with ID: citizen1", exception.getMessage());
        verify(citizenRepository).findById("citizen1");
        verify(requestRepository, never()).save(any());
        verify(updateRepository, never()).save(any());
    }

    // Test createRequest method - Negative Cases (Photo upload failure)
    @Test
    void testCreateRequest_PhotoUploadFailure() throws IOException {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        testRequestDTO.setPhoto(mockFile);
        when(citizenRepository.findById("citizen1")).thenReturn(Optional.of(testCitizen));
        when(cloudinaryService.uploadImage(mockFile)).thenThrow(new IOException("Upload failed"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            requestService.createRequest("citizen1", testRequestDTO);
        });

        assertTrue(exception.getMessage().contains("Failed to upload photo"));
        verify(citizenRepository).findById("citizen1");
        verify(cloudinaryService).uploadImage(mockFile);
        verify(requestRepository, never()).save(any());
        verify(updateRepository, never()).save(any());
    }

    // Test getRequestsByCitizen method - Positive Cases
    @Test
    void testGetRequestsByCitizen_Positive() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<CitizenWasteDisposalRequest> requests = Arrays.asList(testRequest);
        Page<CitizenWasteDisposalRequest> requestPage = new PageImpl<>(requests);
        when(requestRepository.findByCitizenId("citizen1", pageable)).thenReturn(requestPage);

        // Act
        Page<CitizenWasteDisposalRequest> result = requestService.getRequestsByCitizen("citizen1", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testRequest.getRequestId(), result.getContent().get(0).getRequestId());
        verify(requestRepository).findByCitizenId("citizen1", pageable);
    }

    // Test getRequestById method - Positive Cases
    @Test
    void testGetRequestById_Positive() {
        // Arrange
        when(requestRepository.findByRequestIdAndCitizenId("REQ-12345678", "citizen1"))
                .thenReturn(Optional.of(testRequest));

        // Act
        Optional<CitizenWasteDisposalRequest> result = requestService.getRequestById("REQ-12345678", "citizen1");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("REQ-12345678", result.get().getRequestId());
        assertEquals("citizen1", result.get().getCitizenId());
        verify(requestRepository).findByRequestIdAndCitizenId("REQ-12345678", "citizen1");
    }

    // Test getRequestById method - Negative Cases (Request not found)
    @Test
    void testGetRequestById_NotFound() {
        // Arrange
        when(requestRepository.findByRequestIdAndCitizenId("REQ-INVALID", "citizen1"))
                .thenReturn(Optional.empty());

        // Act
        Optional<CitizenWasteDisposalRequest> result = requestService.getRequestById("REQ-INVALID", "citizen1");

        // Assert
        assertFalse(result.isPresent());
        verify(requestRepository).findByRequestIdAndCitizenId("REQ-INVALID", "citizen1");
    }

    // Test getRequestUpdates method - Positive Cases
    @Test
    void testGetRequestUpdates_Positive() {
        // Arrange
        CitizenRequestUpdate update = new CitizenRequestUpdate();
        update.setUpdateId("update1");
        update.setRequestId("REQ-12345678");
        update.setStatus(CitizenRequestStatus.SUBMITTED);
        update.setNote("Request submitted");
        update.setUpdatedBy("system");
        update.setTimestamp(LocalDateTime.now());

        List<CitizenRequestUpdate> updates = Arrays.asList(update);
        when(updateRepository.findByRequestIdOrderByTimestampAsc("REQ-12345678")).thenReturn(updates);

        // Act
        List<CitizenRequestUpdate> result = requestService.getRequestUpdates("REQ-12345678");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("update1", result.get(0).getUpdateId());
        verify(updateRepository).findByRequestIdOrderByTimestampAsc("REQ-12345678");
    }

    // Test updateRequestStatus method - Positive Cases
    @Test
    void testUpdateRequestStatus_Positive() {
        // Arrange
        CitizenRequestStatusUpdateDTO statusUpdate = new CitizenRequestStatusUpdateDTO();
        statusUpdate.setStatus(CitizenRequestStatus.ASSIGNED);
        statusUpdate.setNote("Request assigned to collector");

        testRequest.setStatus(CitizenRequestStatus.SUBMITTED);
        when(requestRepository.findById("REQ-12345678")).thenReturn(Optional.of(testRequest));
        when(requestRepository.save(any(CitizenWasteDisposalRequest.class))).thenReturn(testRequest);
        when(updateRepository.save(any(CitizenRequestUpdate.class))).thenReturn(new CitizenRequestUpdate());

        // Act
        CitizenWasteDisposalRequest result = requestService.updateRequestStatus("REQ-12345678", statusUpdate, "admin");

        // Assert
        assertNotNull(result);
        assertEquals(CitizenRequestStatus.ASSIGNED, result.getStatus());
        verify(requestRepository).findById("REQ-12345678");
        verify(requestRepository).save(any(CitizenWasteDisposalRequest.class));
        verify(updateRepository).save(any(CitizenRequestUpdate.class));
    }

    // Test updateRequestStatus method - Negative Cases (Request not found)
    @Test
    void testUpdateRequestStatus_RequestNotFound() {
        // Arrange
        CitizenRequestStatusUpdateDTO statusUpdate = new CitizenRequestStatusUpdateDTO();
        statusUpdate.setStatus(CitizenRequestStatus.ASSIGNED);
        statusUpdate.setNote("Request assigned to collector");

        when(requestRepository.findById("REQ-INVALID")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            requestService.updateRequestStatus("REQ-INVALID", statusUpdate, "admin");
        });

        assertEquals("Request not found with ID: REQ-INVALID", exception.getMessage());
        verify(requestRepository).findById("REQ-INVALID");
        verify(requestRepository, never()).save(any());
        verify(updateRepository, never()).save(any());
    }

    // Test updateRequestStatus method - Negative Cases (Invalid status transition)
    @Test
    void testUpdateRequestStatus_InvalidStatusTransition() {
        // Arrange
        CitizenRequestStatusUpdateDTO statusUpdate = new CitizenRequestStatusUpdateDTO();
        statusUpdate.setStatus(CitizenRequestStatus.RESOLVED); // Invalid transition from SUBMITTED
        statusUpdate.setNote("Request resolved");

        testRequest.setStatus(CitizenRequestStatus.SUBMITTED);
        when(requestRepository.findById("REQ-12345678")).thenReturn(Optional.of(testRequest));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            requestService.updateRequestStatus("REQ-12345678", statusUpdate, "admin");
        });

        assertTrue(exception.getMessage().contains("Invalid status transition"));
        verify(requestRepository).findById("REQ-12345678");
        verify(requestRepository, never()).save(any());
        verify(updateRepository, never()).save(any());
    }

    // Test cancelRequest method - Positive Cases
    @Test
    void testCancelRequest_Positive() {
        // Arrange
        testRequest.setStatus(CitizenRequestStatus.SUBMITTED);
        when(requestRepository.findByRequestIdAndCitizenId("REQ-12345678", "citizen1"))
                .thenReturn(Optional.of(testRequest));
        when(requestRepository.save(any(CitizenWasteDisposalRequest.class))).thenReturn(testRequest);
        when(updateRepository.save(any(CitizenRequestUpdate.class))).thenReturn(new CitizenRequestUpdate());

        // Act
        boolean result = requestService.cancelRequest("REQ-12345678", "citizen1");

        // Assert
        assertTrue(result);
        assertEquals(CitizenRequestStatus.CANCELLED, testRequest.getStatus());
        verify(requestRepository).findByRequestIdAndCitizenId("REQ-12345678", "citizen1");
        verify(requestRepository).save(any(CitizenWasteDisposalRequest.class));
        verify(updateRepository).save(any(CitizenRequestUpdate.class));
    }

    // Test cancelRequest method - Negative Cases (Request not found)
    @Test
    void testCancelRequest_RequestNotFound() {
        // Arrange
        when(requestRepository.findByRequestIdAndCitizenId("REQ-INVALID", "citizen1"))
                .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            requestService.cancelRequest("REQ-INVALID", "citizen1");
        });

        assertEquals("Request not found or access denied", exception.getMessage());
        verify(requestRepository).findByRequestIdAndCitizenId("REQ-INVALID", "citizen1");
        verify(requestRepository, never()).save(any());
        verify(updateRepository, never()).save(any());
    }

    // Test cancelRequest method - Negative Cases (Invalid status for cancellation)
    @Test
    void testCancelRequest_InvalidStatus() {
        // Arrange
        testRequest.setStatus(CitizenRequestStatus.OUT_FOR_COLLECTION);
        when(requestRepository.findByRequestIdAndCitizenId("REQ-12345678", "citizen1"))
                .thenReturn(Optional.of(testRequest));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            requestService.cancelRequest("REQ-12345678", "citizen1");
        });

        assertTrue(exception.getMessage().contains("Cannot cancel request in current status"));
        verify(requestRepository).findByRequestIdAndCitizenId("REQ-12345678", "citizen1");
        verify(requestRepository, never()).save(any());
        verify(updateRepository, never()).save(any());
    }

    // Test isValidStatusTransition method - Positive Cases
    @Test
    void testIsValidStatusTransition_Positive() {
        // Test valid transitions using reflection
        try {
            java.lang.reflect.Method method = CitizenWasteDisposalRequestService.class.getDeclaredMethod(
                    "isValidStatusTransition", CitizenRequestStatus.class, CitizenRequestStatus.class);
            method.setAccessible(true);

            // Test SUBMITTED -> ASSIGNED
            Boolean result1 = (Boolean) method.invoke(requestService, CitizenRequestStatus.SUBMITTED, CitizenRequestStatus.ASSIGNED);
            assertTrue(result1);

            // Test ASSIGNED -> COLLECTION_SCHEDULED
            Boolean result2 = (Boolean) method.invoke(requestService, CitizenRequestStatus.ASSIGNED, CitizenRequestStatus.COLLECTION_SCHEDULED);
            assertTrue(result2);

            // Test COLLECTION_SCHEDULED -> OUT_FOR_COLLECTION
            Boolean result3 = (Boolean) method.invoke(requestService, CitizenRequestStatus.COLLECTION_SCHEDULED, CitizenRequestStatus.OUT_FOR_COLLECTION);
            assertTrue(result3);

            // Test OUT_FOR_COLLECTION -> COLLECTED
            Boolean result4 = (Boolean) method.invoke(requestService, CitizenRequestStatus.OUT_FOR_COLLECTION, CitizenRequestStatus.COLLECTED);
            assertTrue(result4);

            // Test COLLECTED -> RESOLVED
            Boolean result5 = (Boolean) method.invoke(requestService, CitizenRequestStatus.COLLECTED, CitizenRequestStatus.RESOLVED);
            assertTrue(result5);
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    // Test isValidStatusTransition method - Negative Cases
    @Test
    void testIsValidStatusTransition_Negative() {
        // Test invalid transitions using reflection
        try {
            java.lang.reflect.Method method = CitizenWasteDisposalRequestService.class.getDeclaredMethod(
                    "isValidStatusTransition", CitizenRequestStatus.class, CitizenRequestStatus.class);
            method.setAccessible(true);

            // Test SUBMITTED -> RESOLVED (invalid)
            Boolean result1 = (Boolean) method.invoke(requestService, CitizenRequestStatus.SUBMITTED, CitizenRequestStatus.RESOLVED);
            assertFalse(result1);

            // Test ASSIGNED -> RESOLVED (invalid)
            Boolean result2 = (Boolean) method.invoke(requestService, CitizenRequestStatus.ASSIGNED, CitizenRequestStatus.RESOLVED);
            assertFalse(result2);

            // Test COLLECTION_SCHEDULED -> RESOLVED (invalid)
            Boolean result3 = (Boolean) method.invoke(requestService, CitizenRequestStatus.COLLECTION_SCHEDULED, CitizenRequestStatus.RESOLVED);
            assertFalse(result3);
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    // Test generateRequestId method
    @Test
    void testGenerateRequestId() {
        // Test using reflection
        try {
            java.lang.reflect.Method method = CitizenWasteDisposalRequestService.class.getDeclaredMethod("generateRequestId");
            method.setAccessible(true);

            String result1 = (String) method.invoke(requestService);
            String result2 = (String) method.invoke(requestService);

            assertNotNull(result1);
            assertNotNull(result2);
            assertTrue(result1.startsWith("REQ-"));
            assertTrue(result2.startsWith("REQ-"));
            assertNotEquals(result1, result2); // Should generate unique IDs
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    // Test createStatusUpdate method
    @Test
    void testCreateStatusUpdate() {
        // Test using reflection
        try {
            java.lang.reflect.Method method = CitizenWasteDisposalRequestService.class.getDeclaredMethod(
                    "createStatusUpdate", String.class, CitizenRequestStatus.class, String.class, String.class);
            method.setAccessible(true);

            when(updateRepository.save(any(CitizenRequestUpdate.class))).thenReturn(new CitizenRequestUpdate());

            Object result = method.invoke(requestService, "REQ-12345678", CitizenRequestStatus.SUBMITTED, "Test note", "testUser");

            assertNull(result); // Method returns void
            verify(updateRepository).save(any(CitizenRequestUpdate.class));
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }
}