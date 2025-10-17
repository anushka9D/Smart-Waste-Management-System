package com.swms.jayathu;

import com.swms.controller.jayathu.SmartBinControllerCityAuth;
import com.swms.service.jayathu.SmartBinServiceCityAuth;
import com.swms.model.jayathu.SmartBinCityAuth;
import com.swms.model.GPSLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SmartBinControllerCityAuthTest {

    @Mock
    private SmartBinServiceCityAuth smartBinService;

    @InjectMocks
    private SmartBinControllerCityAuth smartBinController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateSmartBin_Success() {
        
        SmartBinCityAuth inputBin = new SmartBinCityAuth();
        inputBin.setWasteType("plastic");
        inputBin.setLocation("loc1");
        inputBin.setCoordinates(new GPSLocation(12.34, 56.78));
        inputBin.setCurrentLevel(50.0);
        inputBin.setCapacity(100.0);
        inputBin.setStatus("active");

        SmartBinCityAuth savedBin = new SmartBinCityAuth();
        savedBin.setBinId("bin1");
        savedBin.setWasteType("plastic");
        savedBin.setLocation("loc1");
        savedBin.setCoordinates(new GPSLocation(12.34, 56.78));
        savedBin.setCurrentLevel(50.0);
        savedBin.setCapacity(100.0);
        savedBin.setStatus("active");
        savedBin.setCreatedAt(LocalDateTime.now());
        savedBin.setLastUpdated(LocalDateTime.now());

        when(smartBinService.createSmartBin(inputBin)).thenReturn(savedBin);

        
        ResponseEntity<?> response = smartBinController.createSmartBin(inputBin);

        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedBin, response.getBody());
        verify(smartBinService, times(1)).createSmartBin(inputBin);
    }

    @Test
    public void testCreateSmartBin_InvalidWasteType() {
      
        SmartBinCityAuth inputBin = new SmartBinCityAuth();
        inputBin.setWasteType("invalid");

        when(smartBinService.createSmartBin(inputBin)).thenThrow(new IllegalArgumentException("Invalid waste type. Allowed types are: plastic, organic, metal"));

       
        ResponseEntity<?> response = smartBinController.createSmartBin(inputBin);

       
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Error creating smart bin: Invalid waste type"));
        verify(smartBinService, times(1)).createSmartBin(inputBin);
    }

    @Test
    public void testCreateSmartBin_InternalError() {
     
        SmartBinCityAuth inputBin = new SmartBinCityAuth();
        inputBin.setWasteType("plastic");

        when(smartBinService.createSmartBin(inputBin)).thenThrow(new RuntimeException("Database error"));

      
        ResponseEntity<?> response = smartBinController.createSmartBin(inputBin);

      
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Error creating smart bin: Database error"));
        verify(smartBinService, times(1)).createSmartBin(inputBin);
    }

    @Test
    public void testGetAllSmartBins_Success() {
      
        SmartBinCityAuth bin1 = new SmartBinCityAuth();
        bin1.setBinId("bin1");
        bin1.setWasteType("plastic");
        bin1.setLocation("loc1");
        bin1.setCoordinates(new GPSLocation(12.34, 56.78));
        bin1.setCurrentLevel(50.0);
        bin1.setCapacity(100.0);
        bin1.setStatus("active");

        SmartBinCityAuth bin2 = new SmartBinCityAuth();
        bin2.setBinId("bin2");
        bin2.setWasteType("organic");
        bin2.setLocation("loc2");
        bin2.setCoordinates(new GPSLocation(98.76, 54.32));
        bin2.setCurrentLevel(75.0);
        bin2.setCapacity(150.0);
        bin2.setStatus("full");

        List<SmartBinCityAuth> mockBins = Arrays.asList(bin1, bin2);
        when(smartBinService.getAllSmartBins()).thenReturn(mockBins);

       
        ResponseEntity<List<SmartBinCityAuth>> response = smartBinController.getAllSmartBins();

      
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockBins, response.getBody());
        verify(smartBinService, times(1)).getAllSmartBins();
    }

    @Test
    public void testGetAllSmartBins_InternalError() {
        
        when(smartBinService.getAllSmartBins()).thenThrow(new RuntimeException("Database error"));

      
        ResponseEntity<List<SmartBinCityAuth>> response = smartBinController.getAllSmartBins();

       
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(smartBinService, times(1)).getAllSmartBins();
    }

    @Test
    public void testGetSmartBinById_Success() {
       
        String id = "bin1";
        SmartBinCityAuth bin = new SmartBinCityAuth();
        bin.setBinId(id);
        bin.setWasteType("plastic");
        bin.setLocation("loc1");
        bin.setCoordinates(new GPSLocation(12.34, 56.78));
        bin.setCurrentLevel(50.0);
        bin.setCapacity(100.0);
        bin.setStatus("active");

        when(smartBinService.getSmartBinById(id)).thenReturn(Optional.of(bin));

       
        ResponseEntity<?> response = smartBinController.getSmartBinById(id);

      
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bin, response.getBody());
        verify(smartBinService, times(1)).getSmartBinById(id);
    }

    @Test
    public void testGetSmartBinById_NotFound() {
        
        String id = "bin1";
        when(smartBinService.getSmartBinById(id)).thenReturn(Optional.empty());

       
        ResponseEntity<?> response = smartBinController.getSmartBinById(id);

        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Smart bin not found with id: " + id));
        verify(smartBinService, times(1)).getSmartBinById(id);
    }

    @Test
    public void testGetSmartBinById_InternalError() {
      
        String id = "bin1";
        when(smartBinService.getSmartBinById(id)).thenThrow(new RuntimeException("Database error"));

        
        ResponseEntity<?> response = smartBinController.getSmartBinById(id);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Error retrieving smart bin: Database error"));
        verify(smartBinService, times(1)).getSmartBinById(id);
    }

    @Test
    public void testUpdateSmartBin_Success() {
      
        String id = "bin1";
        SmartBinCityAuth updateDetails = new SmartBinCityAuth();
        updateDetails.setWasteType("organic");
        updateDetails.setLocation("loc2");
        updateDetails.setCoordinates(new GPSLocation(98.76, 54.32));
        updateDetails.setCurrentLevel(75.0);
        updateDetails.setCapacity(150.0);
        updateDetails.setStatus("full");

        SmartBinCityAuth updatedBin = new SmartBinCityAuth();
        updatedBin.setBinId(id);
        updatedBin.setWasteType("organic");
        updatedBin.setLocation("loc2");
        updatedBin.setCoordinates(new GPSLocation(98.76, 54.32));
        updatedBin.setCurrentLevel(75.0);
        updatedBin.setCapacity(150.0);
        updatedBin.setStatus("full");
        updatedBin.setLastUpdated(LocalDateTime.now());

        when(smartBinService.updateSmartBin(id, updateDetails)).thenReturn(updatedBin);

       
        ResponseEntity<?> response = smartBinController.updateSmartBin(id, updateDetails);

       
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedBin, response.getBody());
        verify(smartBinService, times(1)).updateSmartBin(id, updateDetails);
    }

    @Test
    public void testUpdateSmartBin_InvalidWasteType() {
       
        String id = "bin1";
        SmartBinCityAuth updateDetails = new SmartBinCityAuth();
        updateDetails.setWasteType("invalid");

        when(smartBinService.updateSmartBin(id, updateDetails)).thenThrow(new IllegalArgumentException("Invalid waste type. Allowed types are: plastic, organic, metal"));

       
        ResponseEntity<?> response = smartBinController.updateSmartBin(id, updateDetails);

       
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Error updating smart bin: Invalid waste type"));
        verify(smartBinService, times(1)).updateSmartBin(id, updateDetails);
    }

    @Test
    public void testUpdateSmartBin_NotFound() {
        
        String id = "bin1";
        SmartBinCityAuth updateDetails = new SmartBinCityAuth();
        updateDetails.setWasteType("organic");

        when(smartBinService.updateSmartBin(id, updateDetails)).thenThrow(new RuntimeException("Smart bin not found with id: " + id));

       
        ResponseEntity<?> response = smartBinController.updateSmartBin(id, updateDetails);

       
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Smart bin not found with id: " + id, response.getBody());
        verify(smartBinService, times(1)).updateSmartBin(id, updateDetails);
    }

    @Test
    public void testUpdateSmartBin_InternalError() {
      
        String id = "bin1";
        SmartBinCityAuth updateDetails = new SmartBinCityAuth();
        updateDetails.setWasteType("organic");

        when(smartBinService.updateSmartBin(id, updateDetails)).thenThrow(new RuntimeException("Database error"));

        
        ResponseEntity<?> response = smartBinController.updateSmartBin(id, updateDetails);

      
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Database error"));
        verify(smartBinService, times(1)).updateSmartBin(id, updateDetails);
    }

    @Test
    public void testDeleteSmartBin_Success() {
       
        String id = "bin1";
        doNothing().when(smartBinService).deleteSmartBin(id);

       
        ResponseEntity<?> response = smartBinController.deleteSmartBin(id);

      
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Smart bin deleted successfully", response.getBody());
        verify(smartBinService, times(1)).deleteSmartBin(id);
    }

    @Test
    public void testDeleteSmartBin_InternalError() {
       
        String id = "bin1";
        doThrow(new RuntimeException("Database error")).when(smartBinService).deleteSmartBin(id);

      
        ResponseEntity<?> response = smartBinController.deleteSmartBin(id);

       
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Error deleting smart bin: Database error"));
        verify(smartBinService, times(1)).deleteSmartBin(id);
    }

    @Test
    public void testTestEndpoint() {
     
        ResponseEntity<String> response = smartBinController.test();

      
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("SmartBin service is working correctly", response.getBody());
    }
}