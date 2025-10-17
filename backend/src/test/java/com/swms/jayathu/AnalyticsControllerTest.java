package com.swms.jayathu;


import com.swms.controller.jayathu.*;
import com.swms.dto.jayathu.*;
import com.swms.service.jayathu.AnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AnalyticsControllerTest {

    @Mock
    private AnalyticsService analyticsService;

    @InjectMocks
    private AnalyticsController analyticsController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetDashboard() {
       
        DashboardSummaryDto mockDashboard = new DashboardSummaryDto();
        mockDashboard.setTotalWaste(new TotalWasteDto(100, 200));
        mockDashboard.setWasteByLocation(Collections.singletonList(new LocationWasteDto("loc1", 50, 2)));
        mockDashboard.setBinStatus(Collections.singletonList(new BinStatusDto("full", 5, 50)));
        mockDashboard.setTopLocations(Collections.singletonList(new LocationWasteDto("loc2", 60, 3)));

       
        when(analyticsService.getDashboardAnalytics()).thenReturn(mockDashboard);

       
        ResponseEntity<DashboardSummaryDto> response = analyticsController.getDashboard();

        
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockDashboard, response.getBody());
        verify(analyticsService, times(1)).getDashboardAnalytics();
    }

    @Test
    public void testGetWasteByType() {
       
        List<WasteTypeDto> mockWasteTypes = Arrays.asList(
            new WasteTypeDto("plastic", 50, 2, 150),
            new WasteTypeDto("organic", 100, 3, 150)
        );

        
        when(analyticsService.getWasteByType()).thenReturn(mockWasteTypes);

       
        ResponseEntity<List<WasteTypeDto>> response = analyticsController.getWasteByType();

       
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockWasteTypes, response.getBody());
        verify(analyticsService, times(1)).getWasteByType();
    }

    @Test
    public void testGetWasteByTypeFiltered_Success() {
       
        String wasteType = "plastic";
        List<WasteTypeDto> mockFilteredWasteTypes = Collections.singletonList(
            new WasteTypeDto("plastic", 50, 2, 150)
        );

       
        when(analyticsService.getWasteByTypeFiltered(wasteType)).thenReturn(mockFilteredWasteTypes);

       
        ResponseEntity<List<WasteTypeDto>> response = analyticsController.getWasteByTypeFiltered(wasteType);

       
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockFilteredWasteTypes, response.getBody());
        verify(analyticsService, times(1)).getWasteByTypeFiltered(wasteType);
    }

    @Test
    public void testGetWasteByTypeFiltered_Error() {
       
        String wasteType = "plastic";
        Exception exception = new RuntimeException("Service error");

        
        when(analyticsService.getWasteByTypeFiltered(wasteType)).thenThrow(exception);

        
        ResponseEntity<List<WasteTypeDto>> response = analyticsController.getWasteByTypeFiltered(wasteType);

       
        assertEquals(500, response.getStatusCodeValue());
        assertEquals(null, response.getBody());
        verify(analyticsService, times(1)).getWasteByTypeFiltered(wasteType);
    }

    @Test
    public void testGetTotalPlasticWaste() {
       
        int mockTotalPlastic = 75;

     
        when(analyticsService.getTotalPlasticWaste()).thenReturn(mockTotalPlastic);

       
        ResponseEntity<Integer> response = analyticsController.getTotalPlasticWaste();

       
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockTotalPlastic, response.getBody());
        verify(analyticsService, times(1)).getTotalPlasticWaste();
    }
}
