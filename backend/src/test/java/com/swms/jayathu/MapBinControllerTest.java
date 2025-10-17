package com.swms.jayathu;

import com.swms.controller.jayathu.*;
import com.swms.dto.jayathu.MapBinDto;
import com.swms.service.jayathu.MapBinService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class MapBinControllerTest {

    @Mock
    private MapBinService mapBinService;

    @InjectMocks
    private MapBinController mapBinController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllBinsForMap_Success() {
       
        MapBinDto bin1 = new MapBinDto("bin1", "loc1", 12.34, 56.78, 50, 100, "active", "plastic");
        MapBinDto bin2 = new MapBinDto("bin2", "loc2", 98.76, 54.32, 75, 150, "full", "organic");
        List<MapBinDto> mockBins = Arrays.asList(bin1, bin2);
        when(mapBinService.getAllBinsForMap()).thenReturn(mockBins);

        
        ResponseEntity<List<MapBinDto>> response = mapBinController.getAllBinsForMap();

       
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockBins, response.getBody());
        verify(mapBinService, times(1)).getAllBinsForMap();
    }

    @Test
    public void testGetAllBinsForMap_Error() {
       
        when(mapBinService.getAllBinsForMap()).thenThrow(new RuntimeException("Service error"));

       
        ResponseEntity<List<MapBinDto>> response = mapBinController.getAllBinsForMap();

       
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(null, response.getBody());
        verify(mapBinService, times(1)).getAllBinsForMap();
    }

    @Test
    public void testGetBinsByWasteTypeForMap_Success() {
       
        String wasteType = "plastic";
        MapBinDto bin = new MapBinDto("bin1", "loc1", 12.34, 56.78, 50, 100, "active", "plastic");
        List<MapBinDto> mockBins = Collections.singletonList(bin);
        when(mapBinService.getBinsByWasteTypeForMap(wasteType)).thenReturn(mockBins);

        
        ResponseEntity<List<MapBinDto>> response = mapBinController.getBinsByWasteTypeForMap(wasteType);

     
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockBins, response.getBody());
        verify(mapBinService, times(1)).getBinsByWasteTypeForMap(wasteType);
    }

    @Test
    public void testGetBinsByStatusForMap_Success() {
       
        String status = "active";
        MapBinDto bin = new MapBinDto("bin1", "loc1", 12.34, 56.78, 50, 100, "active", "plastic");
        List<MapBinDto> mockBins = Collections.singletonList(bin);
        when(mapBinService.getBinsByStatusForMap(status)).thenReturn(mockBins);

       
        ResponseEntity<List<MapBinDto>> response = mapBinController.getBinsByStatusForMap(status);

        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockBins, response.getBody());
        verify(mapBinService, times(1)).getBinsByStatusForMap(status);
    }

    @Test
    public void testGetBinsByLocationForMap_Success() {
       
        String location = "loc1";
        MapBinDto bin = new MapBinDto("bin1", "loc1", 12.34, 56.78, 50, 100, "active", "plastic");
        List<MapBinDto> mockBins = Collections.singletonList(bin);
        when(mapBinService.getBinsByLocationForMap(location)).thenReturn(mockBins);

       
        ResponseEntity<List<MapBinDto>> response = mapBinController.getBinsByLocationForMap(location);

        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockBins, response.getBody());
        verify(mapBinService, times(1)).getBinsByLocationForMap(location);
    }

    @Test
    public void testGetBinsWithFilters_WasteType() {
       
        String wasteType = "plastic";
        MapBinDto bin = new MapBinDto("bin1", "loc1", 12.34, 56.78, 50, 100, "active", "plastic");
        List<MapBinDto> mockBins = Collections.singletonList(bin);
        when(mapBinService.getBinsByWasteTypeForMap(wasteType)).thenReturn(mockBins);

       
        ResponseEntity<List<MapBinDto>> response = mapBinController.getBinsWithFilters(wasteType, null, null);

       
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockBins, response.getBody());
        verify(mapBinService, times(1)).getBinsByWasteTypeForMap(wasteType);
        verify(mapBinService, never()).getBinsByStatusForMap(anyString());
        verify(mapBinService, never()).getBinsByLocationForMap(anyString());
        verify(mapBinService, never()).getAllBinsForMap();
    }

    @Test
    public void testGetBinsWithFilters_Status() {
        
        String status = "active";
        MapBinDto bin = new MapBinDto("bin1", "loc1", 12.34, 56.78, 50, 100, "active", "plastic");
        List<MapBinDto> mockBins = Collections.singletonList(bin);
        when(mapBinService.getBinsByStatusForMap(status)).thenReturn(mockBins);

       
        ResponseEntity<List<MapBinDto>> response = mapBinController.getBinsWithFilters(null, status, null);

        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockBins, response.getBody());
        verify(mapBinService, times(1)).getBinsByStatusForMap(status);
        verify(mapBinService, never()).getBinsByWasteTypeForMap(anyString());
        verify(mapBinService, never()).getBinsByLocationForMap(anyString());
        verify(mapBinService, never()).getAllBinsForMap();
    }

    @Test
    public void testGetBinsWithFilters_Location() {
       
        String location = "loc1";
        MapBinDto bin = new MapBinDto("bin1", "loc1", 12.34, 56.78, 50, 100, "active", "plastic");
        List<MapBinDto> mockBins = Collections.singletonList(bin);
        when(mapBinService.getBinsByLocationForMap(location)).thenReturn(mockBins);

      
        ResponseEntity<List<MapBinDto>> response = mapBinController.getBinsWithFilters(null, null, location);

       
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockBins, response.getBody());
        verify(mapBinService, times(1)).getBinsByLocationForMap(location);
        verify(mapBinService, never()).getBinsByWasteTypeForMap(anyString());
        verify(mapBinService, never()).getBinsByStatusForMap(anyString());
        verify(mapBinService, never()).getAllBinsForMap();
    }

    @Test
    public void testGetBinsWithFilters_NoFilters() {
      
        MapBinDto bin1 = new MapBinDto("bin1", "loc1", 12.34, 56.78, 50, 100, "active", "plastic");
        MapBinDto bin2 = new MapBinDto("bin2", "loc2", 98.76, 54.32, 75, 150, "full", "organic");
        List<MapBinDto> mockBins = Arrays.asList(bin1, bin2);
        when(mapBinService.getAllBinsForMap()).thenReturn(mockBins);

       
        ResponseEntity<List<MapBinDto>> response = mapBinController.getBinsWithFilters(null, null, null);

       
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockBins, response.getBody());
        verify(mapBinService, times(1)).getAllBinsForMap();
        verify(mapBinService, never()).getBinsByWasteTypeForMap(anyString());
        verify(mapBinService, never()).getBinsByStatusForMap(anyString());
        verify(mapBinService, never()).getBinsByLocationForMap(anyString());
    }

    @Test
    public void testTestEndpoint() {
        
        ResponseEntity<String> response = mapBinController.test();

      
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Map service is working correctly", response.getBody());
        verifyNoInteractions(mapBinService); 
    }
}