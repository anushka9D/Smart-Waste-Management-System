package com.swms.jayathu;

import com.swms.service.jayathu.*;
import com.swms.dto.jayathu.MapBinDto;
import com.swms.model.GPSLocation;
import com.swms.model.jayathu.SmartBinCityAuth;
import com.swms.repository.jayathu.MapBinRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class MapBinServiceTest {

    @Mock
    private MapBinRepository mapBinRepository;

    @InjectMocks
    private MapBinService mapBinService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllBinsForMap_Success() {
       
        SmartBinCityAuth bin1 = createSmartBin("bin1", "loc1", 12.34, 56.78, 50.0, 100.0, "active", "plastic");
        SmartBinCityAuth bin2 = createSmartBin("bin2", "loc2", 98.76, 54.32, 75.0, 150.0, "full", "organic");
        when(mapBinRepository.findAll()).thenReturn(Arrays.asList(bin1, bin2));

       
        List<MapBinDto> result = mapBinService.getAllBinsForMap();

       
        assertEquals(2, result.size());
        assertEquals("bin1", result.get(0).getId());
        assertEquals("loc1", result.get(0).getLocation());
        assertEquals(12.34, result.get(0).getLatitude());
        assertEquals(56.78, result.get(0).getLongitude());
        assertEquals(Integer.valueOf(50), result.get(0).getCurrentLevel());
        assertEquals(Integer.valueOf(100), result.get(0).getCapacity());
        assertEquals("active", result.get(0).getStatus());
        assertEquals("plastic", result.get(0).getWasteType());
        verify(mapBinRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllBinsForMap_NullCoordinates() {
       
        SmartBinCityAuth bin1 = new SmartBinCityAuth();
        bin1.setBinId("bin1");
        bin1.setLocation("loc1");
        bin1.setCoordinates(null); 
        bin1.setCurrentLevel(50.0);
        bin1.setCapacity(100.0);
        bin1.setStatus("active");
        bin1.setWasteType("plastic");
        when(mapBinRepository.findAll()).thenReturn(Collections.singletonList(bin1));

       
        List<MapBinDto> result = mapBinService.getAllBinsForMap();

      
        assertEquals(0, result.size());
        verify(mapBinRepository, times(1)).findAll();
    }

    @Test
    public void testGetBinsByWasteTypeForMap_Success() {
        
        String wasteType = "plastic";
        SmartBinCityAuth bin = createSmartBin("bin1", "loc1", 12.34, 56.78, 50.0, 100.0, "active", "plastic");
        when(mapBinRepository.findByWasteType(wasteType)).thenReturn(Collections.singletonList(bin));

       
        List<MapBinDto> result = mapBinService.getBinsByWasteTypeForMap(wasteType);

      
        assertEquals(1, result.size());
        assertEquals("bin1", result.get(0).getId());
        assertEquals("plastic", result.get(0).getWasteType());
        verify(mapBinRepository, times(1)).findByWasteType(wasteType);
    }

    @Test
    public void testGetBinsByStatusForMap_Success() {
       
        String status = "active";
        SmartBinCityAuth bin = createSmartBin("bin1", "loc1", 12.34, 56.78, 50.0, 100.0, "active", "plastic");
        when(mapBinRepository.findByStatus(status)).thenReturn(Collections.singletonList(bin));

        
        List<MapBinDto> result = mapBinService.getBinsByStatusForMap(status);

        
        assertEquals(1, result.size());
        assertEquals("bin1", result.get(0).getId());
        assertEquals("active", result.get(0).getStatus());
        verify(mapBinRepository, times(1)).findByStatus(status);
    }

    @Test
    public void testGetBinsByLocationForMap_Success() {
      
        String location = "loc1";
        SmartBinCityAuth bin = createSmartBin("bin1", "loc1", 12.34, 56.78, 50.0, 100.0, "active", "plastic");
        when(mapBinRepository.findByLocation(location)).thenReturn(Collections.singletonList(bin));

        
        List<MapBinDto> result = mapBinService.getBinsByLocationForMap(location);

       
        assertEquals(1, result.size());
        assertEquals("bin1", result.get(0).getId());
        assertEquals("loc1", result.get(0).getLocation());
        verify(mapBinRepository, times(1)).findByLocation(location);
    }

    @Test
    public void testGetBinsByLocationForMap_NullList() {
        
        String location = "loc1";
        when(mapBinRepository.findByLocation(location)).thenReturn(null);

       
        List<MapBinDto> result = mapBinService.getBinsByLocationForMap(location);

       
        assertTrue(result.isEmpty());
        verify(mapBinRepository, times(1)).findByLocation(location);
    }

   
    private SmartBinCityAuth createSmartBin(String id, String location, double latitude, double longitude, Double currentLevel, Double capacity, String status, String wasteType) {
        SmartBinCityAuth bin = new SmartBinCityAuth();
        bin.setBinId(id);
        bin.setLocation(location);
        GPSLocation coords = new GPSLocation(latitude, longitude);
        bin.setCoordinates(coords);
        bin.setCurrentLevel(currentLevel);
        bin.setCapacity(capacity);
        bin.setStatus(status);
        bin.setWasteType(wasteType);
        return bin;
    }
}