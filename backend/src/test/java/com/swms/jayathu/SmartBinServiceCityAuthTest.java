package com.swms.jayathu;

import com.swms.service.jayathu.*;
import com.swms.model.jayathu.SmartBinCityAuth;
import com.swms.repository.jayathu.SmartBinRepositoryCityAuth;
import com.swms.model.GPSLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SmartBinServiceCityAuthTest {

    @Mock
    private SmartBinRepositoryCityAuth smartBinRepositorycityauth;

    @InjectMocks
    private SmartBinServiceCityAuth smartBinServiceCityAuth;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateSmartBin_Success() {
      
        SmartBinCityAuth smartBin = new SmartBinCityAuth();
        smartBin.setWasteType("plastic");
        smartBin.setLocation("loc1");
        smartBin.setCoordinates(new GPSLocation(12.34, 56.78));
        smartBin.setCurrentLevel(50.0);
        smartBin.setCapacity(100.0);
        smartBin.setStatus("active");

       
        when(smartBinRepositorycityauth.save(smartBin)).thenReturn(smartBin);

      
        SmartBinCityAuth result = smartBinServiceCityAuth.createSmartBin(smartBin);

      
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getLastUpdated());
        assertEquals("plastic", result.getWasteType());
        assertEquals("loc1", result.getLocation());
        assertEquals(new GPSLocation(12.34, 56.78), result.getCoordinates());
        assertEquals(50.0, result.getCurrentLevel());
        assertEquals(100.0, result.getCapacity());
        assertEquals("active", result.getStatus());
        verify(smartBinRepositorycityauth, times(1)).save(smartBin);
    }

    @Test
    public void testCreateSmartBin_InvalidWasteType() {
        
        SmartBinCityAuth smartBin = new SmartBinCityAuth();
        smartBin.setWasteType("invalid");

       
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            smartBinServiceCityAuth.createSmartBin(smartBin)
        );

       
        assertEquals("Invalid waste type. Allowed types are: plastic, organic, metal", exception.getMessage());
        verify(smartBinRepositorycityauth, times(0)).save(any());
    }

    @Test
    public void testGetAllSmartBins_Success() {
       
        SmartBinCityAuth smartBin = new SmartBinCityAuth();
        smartBin.setWasteType("plastic");
        when(smartBinRepositorycityauth.findAll()).thenReturn(Collections.singletonList(smartBin));

      
        List<SmartBinCityAuth> result = smartBinServiceCityAuth.getAllSmartBins();

      
        assertEquals(1, result.size());
        assertEquals("plastic", result.get(0).getWasteType());
        verify(smartBinRepositorycityauth, times(1)).findAll();
    }

    @Test
    public void testGetSmartBinById_Success() {
      
        String id = "bin1";
        SmartBinCityAuth smartBin = new SmartBinCityAuth();
        smartBin.setWasteType("plastic");
        when(smartBinRepositorycityauth.findById(id)).thenReturn(Optional.of(smartBin));

      
        Optional<SmartBinCityAuth> result = smartBinServiceCityAuth.getSmartBinById(id);

       
        assertTrue(result.isPresent());
        assertEquals("plastic", result.get().getWasteType());
        verify(smartBinRepositorycityauth, times(1)).findById(id);
    }

    @Test
    public void testGetSmartBinById_NotFound() {
       
        String id = "bin1";
        when(smartBinRepositorycityauth.findById(id)).thenReturn(Optional.empty());

        
        Optional<SmartBinCityAuth> result = smartBinServiceCityAuth.getSmartBinById(id);

       
        assertFalse(result.isPresent());
        verify(smartBinRepositorycityauth, times(1)).findById(id);
    }

    @Test
    public void testUpdateSmartBin_Success() {
       
        String id = "bin1";
        SmartBinCityAuth existingBin = new SmartBinCityAuth();
        existingBin.setWasteType("plastic");
        existingBin.setLocation("loc1");
        when(smartBinRepositorycityauth.findById(id)).thenReturn(Optional.of(existingBin));

        SmartBinCityAuth updateDetails = new SmartBinCityAuth();
        updateDetails.setLocation("loc2");
        updateDetails.setCoordinates(new GPSLocation(98.76, 54.32));
        updateDetails.setCurrentLevel(75.0);
        updateDetails.setCapacity(150.0);
        updateDetails.setStatus("full");
        updateDetails.setWasteType("organic");

        SmartBinCityAuth updatedBin = new SmartBinCityAuth();
        updatedBin.setWasteType("organic");
        updatedBin.setLocation("loc2");
        updatedBin.setCoordinates(new GPSLocation(98.76, 54.32));
        updatedBin.setCurrentLevel(75.0);
        updatedBin.setCapacity(150.0);
        updatedBin.setStatus("full");
        updatedBin.setLastUpdated(LocalDateTime.now());
        when(smartBinRepositorycityauth.save(existingBin)).thenReturn(updatedBin);

        
        SmartBinCityAuth result = smartBinServiceCityAuth.updateSmartBin(id, updateDetails);

        
        assertEquals("organic", result.getWasteType());
        assertEquals("loc2", result.getLocation());
        assertEquals(new GPSLocation(98.76, 54.32), result.getCoordinates());
        assertEquals(75.0, result.getCurrentLevel());
        assertEquals(150.0, result.getCapacity());
        assertEquals("full", result.getStatus());
        assertNotNull(result.getLastUpdated());
        verify(smartBinRepositorycityauth, times(1)).findById(id);
        verify(smartBinRepositorycityauth, times(1)).save(existingBin);
    }

    @Test
    public void testUpdateSmartBin_InvalidWasteType() {
       
        String id = "bin1";
        SmartBinCityAuth existingBin = new SmartBinCityAuth();
        when(smartBinRepositorycityauth.findById(id)).thenReturn(Optional.of(existingBin));

        SmartBinCityAuth updateDetails = new SmartBinCityAuth();
        updateDetails.setWasteType("invalid");

       
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            smartBinServiceCityAuth.updateSmartBin(id, updateDetails)
        );

       
        assertEquals("Invalid waste type. Allowed types are: plastic, organic, metal", exception.getMessage());
        verify(smartBinRepositorycityauth, times(1)).findById(id);
        verify(smartBinRepositorycityauth, times(0)).save(any());
    }

    @Test
    public void testUpdateSmartBin_NotFound() {
       
        String id = "bin1";
        when(smartBinRepositorycityauth.findById(id)).thenReturn(Optional.empty());

       
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            smartBinServiceCityAuth.updateSmartBin(id, new SmartBinCityAuth())
        );

      
        assertEquals("Smart bin not found with id: " + id, exception.getMessage());
        verify(smartBinRepositorycityauth, times(1)).findById(id);
        verify(smartBinRepositorycityauth, times(0)).save(any());
    }

    @Test
    public void testDeleteSmartBin_Success() {
       
        String id = "bin1";

       
        smartBinServiceCityAuth.deleteSmartBin(id);

       
        verify(smartBinRepositorycityauth, times(1)).deleteById(id);
    }
}