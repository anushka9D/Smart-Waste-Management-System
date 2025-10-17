package com.swms.service.citizen;

import com.swms.dto.citizen.CitizenFeedbackDTO;
import com.swms.model.citizen.CitizenFeedback;
import com.swms.repository.citizen.CitizenFeedbackRepository;
import com.swms.service.CloudinaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CitizenFeedbackServiceTest {

    @Mock
    private CitizenFeedbackRepository citizenFeedbackRepository;

    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private CitizenFeedbackService citizenFeedbackService;

    private CitizenFeedback testFeedback;
    private CitizenFeedbackDTO testFeedbackDTO;

    @BeforeEach
    void setUp() {
        // Initialize test feedback
        testFeedback = new CitizenFeedback();
        testFeedback.setId("feedback1");
        testFeedback.setCitizenId("citizen1");
        testFeedback.setRequestId("request1");
        testFeedback.setTopic("Service Quality");
        testFeedback.setRating(5);
        testFeedback.setComment("Excellent service!");
        testFeedback.setPhotoUrl("http://example.com/photo.jpg");
        testFeedback.setSubmittedAt(LocalDateTime.now());

        // Initialize test DTO
        testFeedbackDTO = new CitizenFeedbackDTO(
                "feedback1",
                "citizen1",
                "request1",
                "Service Quality",
                5,
                "Excellent service!",
                "http://example.com/photo.jpg",
                LocalDateTime.now()
        );
    }

    // Test createFeedback method - Positive Cases (without photo)
    @Test
    void testCreateFeedback_Positive_WithoutPhoto() throws IOException {
        // Arrange
        CitizenFeedback feedbackWithoutPhoto = new CitizenFeedback();
        feedbackWithoutPhoto.setId("feedback1");
        feedbackWithoutPhoto.setCitizenId("citizen1");
        feedbackWithoutPhoto.setRequestId("request1");
        feedbackWithoutPhoto.setTopic("Service Quality");
        feedbackWithoutPhoto.setRating(5);
        feedbackWithoutPhoto.setComment("Excellent service!");
        feedbackWithoutPhoto.setPhotoUrl(null);
        feedbackWithoutPhoto.setSubmittedAt(LocalDateTime.now());
        
        when(citizenFeedbackRepository.save(any(CitizenFeedback.class))).thenReturn(feedbackWithoutPhoto);

        // Act
        CitizenFeedbackDTO result = citizenFeedbackService.createFeedback(
                "citizen1", "request1", "Service Quality", 5, "Excellent service!", null);

        // Assert
        assertNotNull(result);
        assertEquals("feedback1", result.getId());
        assertEquals("citizen1", result.getCitizenId());
        assertEquals("request1", result.getRequestId());
        assertEquals("Service Quality", result.getTopic());
        assertEquals(5, result.getRating());
        assertEquals("Excellent service!", result.getComment());
        assertNull(result.getPhotoUrl()); // No photo provided

        verify(citizenFeedbackRepository).save(any(CitizenFeedback.class));
        verify(cloudinaryService, never()).uploadImage(any(MultipartFile.class), anyString());
    }

    // Test createFeedback method - Positive Cases (with photo)
    @Test
    void testCreateFeedback_Positive_WithPhoto() throws IOException {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        when(cloudinaryService.uploadImage(mockFile, "citizen_feedback")).thenReturn("http://example.com/photo.jpg");
        when(citizenFeedbackRepository.save(any(CitizenFeedback.class))).thenReturn(testFeedback);

        // Act
        CitizenFeedbackDTO result = citizenFeedbackService.createFeedback(
                "citizen1", "request1", "Service Quality", 5, "Excellent service!", mockFile);

        // Assert
        assertNotNull(result);
        assertEquals("feedback1", result.getId());
        assertEquals("citizen1", result.getCitizenId());
        assertEquals("request1", result.getRequestId());
        assertEquals("Service Quality", result.getTopic());
        assertEquals(5, result.getRating());
        assertEquals("Excellent service!", result.getComment());
        assertEquals("http://example.com/photo.jpg", result.getPhotoUrl());

        verify(citizenFeedbackRepository).save(any(CitizenFeedback.class));
        verify(cloudinaryService).uploadImage(mockFile, "citizen_feedback");
    }

    // Test createFeedback method - Edge Cases (empty photo)
    @Test
    void testCreateFeedback_EmptyPhoto() throws IOException {
        // Arrange
        CitizenFeedback feedbackWithoutPhoto = new CitizenFeedback();
        feedbackWithoutPhoto.setId("feedback1");
        feedbackWithoutPhoto.setCitizenId("citizen1");
        feedbackWithoutPhoto.setRequestId("request1");
        feedbackWithoutPhoto.setTopic("Service Quality");
        feedbackWithoutPhoto.setRating(5);
        feedbackWithoutPhoto.setComment("Excellent service!");
        feedbackWithoutPhoto.setPhotoUrl(null);
        feedbackWithoutPhoto.setSubmittedAt(LocalDateTime.now());
        
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(true);
        when(citizenFeedbackRepository.save(any(CitizenFeedback.class))).thenReturn(feedbackWithoutPhoto);

        // Act
        CitizenFeedbackDTO result = citizenFeedbackService.createFeedback(
                "citizen1", "request1", "Service Quality", 5, "Excellent service!", mockFile);

        // Assert
        assertNotNull(result);
        assertNull(result.getPhotoUrl()); // Empty photo should not be processed

        verify(citizenFeedbackRepository).save(any(CitizenFeedback.class));
        verify(cloudinaryService, never()).uploadImage(any(MultipartFile.class), anyString());
    }

    // Test createFeedback method - Negative Cases (photo upload failure)
    @Test
    void testCreateFeedback_PhotoUploadFailure() throws IOException {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        when(cloudinaryService.uploadImage(mockFile, "citizen_feedback")).thenThrow(new IOException("Upload failed"));

        // Act & Assert
        IOException exception = assertThrows(IOException.class, () -> {
            citizenFeedbackService.createFeedback(
                    "citizen1", "request1", "Service Quality", 5, "Excellent service!", mockFile);
        });

        assertEquals("Upload failed", exception.getMessage());
        verify(cloudinaryService).uploadImage(mockFile, "citizen_feedback");
        verify(citizenFeedbackRepository, never()).save(any());
    }

    // Test getFeedbackByCitizenId method - Positive Cases
    @Test
    void testGetFeedbackByCitizenId_Positive() {
        // Arrange
        List<CitizenFeedback> feedbackList = Arrays.asList(testFeedback);
        when(citizenFeedbackRepository.findByCitizenId("citizen1")).thenReturn(feedbackList);

        // Act
        List<CitizenFeedbackDTO> result = citizenFeedbackService.getFeedbackByCitizenId("citizen1");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("feedback1", result.get(0).getId());
        assertEquals("citizen1", result.get(0).getCitizenId());
        assertEquals("request1", result.get(0).getRequestId());
        assertEquals("Service Quality", result.get(0).getTopic());
        assertEquals(5, result.get(0).getRating());

        verify(citizenFeedbackRepository).findByCitizenId("citizen1");
    }

    // Test getFeedbackByCitizenId method - Edge Cases (empty list)
    @Test
    void testGetFeedbackByCitizenId_EmptyList() {
        // Arrange
        when(citizenFeedbackRepository.findByCitizenId("citizen1")).thenReturn(Arrays.asList());

        // Act
        List<CitizenFeedbackDTO> result = citizenFeedbackService.getFeedbackByCitizenId("citizen1");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(citizenFeedbackRepository).findByCitizenId("citizen1");
    }

    // Test getFeedbackByRequestId method - Positive Cases
    @Test
    void testGetFeedbackByRequestId_Positive() {
        // Arrange
        List<CitizenFeedback> feedbackList = Arrays.asList(testFeedback);
        when(citizenFeedbackRepository.findByRequestId("request1")).thenReturn(feedbackList);

        // Act
        List<CitizenFeedbackDTO> result = citizenFeedbackService.getFeedbackByRequestId("request1");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("feedback1", result.get(0).getId());
        assertEquals("citizen1", result.get(0).getCitizenId());
        assertEquals("request1", result.get(0).getRequestId());
        assertEquals("Service Quality", result.get(0).getTopic());
        assertEquals(5, result.get(0).getRating());

        verify(citizenFeedbackRepository).findByRequestId("request1");
    }

    // Test getFeedbackByRequestId method - Edge Cases (empty list)
    @Test
    void testGetFeedbackByRequestId_EmptyList() {
        // Arrange
        when(citizenFeedbackRepository.findByRequestId("request1")).thenReturn(Arrays.asList());

        // Act
        List<CitizenFeedbackDTO> result = citizenFeedbackService.getFeedbackByRequestId("request1");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(citizenFeedbackRepository).findByRequestId("request1");
    }

    // Test mapToDTO method - Positive Cases
    @Test
    void testMapToDTO() {
        // Test using reflection
        try {
            java.lang.reflect.Method method = CitizenFeedbackService.class.getDeclaredMethod(
                    "mapToDTO", CitizenFeedback.class);
            method.setAccessible(true);

            // Act
            Object result = method.invoke(citizenFeedbackService, testFeedback);

            // Assert
            assertNotNull(result);
            assertTrue(result instanceof CitizenFeedbackDTO);
            CitizenFeedbackDTO dto = (CitizenFeedbackDTO) result;
            assertEquals("feedback1", dto.getId());
            assertEquals("citizen1", dto.getCitizenId());
            assertEquals("request1", dto.getRequestId());
            assertEquals("Service Quality", dto.getTopic());
            assertEquals(5, dto.getRating());
            assertEquals("Excellent service!", dto.getComment());
            assertEquals("http://example.com/photo.jpg", dto.getPhotoUrl());
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    // Test mapToDTO method with null values
    @Test
    void testMapToDTO_WithNullValues() {
        // Test using reflection
        try {
            java.lang.reflect.Method method = CitizenFeedbackService.class.getDeclaredMethod(
                    "mapToDTO", CitizenFeedback.class);
            method.setAccessible(true);

            // Create feedback with null values
            CitizenFeedback feedbackWithNulls = new CitizenFeedback();
            feedbackWithNulls.setId("feedback2");
            feedbackWithNulls.setCitizenId("citizen2");
            feedbackWithNulls.setRequestId("request2");
            feedbackWithNulls.setTopic("Service Quality");
            feedbackWithNulls.setRating(4);
            feedbackWithNulls.setComment(null);
            feedbackWithNulls.setPhotoUrl(null);
            feedbackWithNulls.setSubmittedAt(LocalDateTime.now());

            // Act
            Object result = method.invoke(citizenFeedbackService, feedbackWithNulls);

            // Assert
            assertNotNull(result);
            assertTrue(result instanceof CitizenFeedbackDTO);
            CitizenFeedbackDTO dto = (CitizenFeedbackDTO) result;
            assertEquals("feedback2", dto.getId());
            assertEquals("citizen2", dto.getCitizenId());
            assertEquals("request2", dto.getRequestId());
            assertEquals("Service Quality", dto.getTopic());
            assertEquals(4, dto.getRating());
            assertNull(dto.getComment());
            assertNull(dto.getPhotoUrl());
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }
}