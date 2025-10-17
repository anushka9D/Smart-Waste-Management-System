package com.swms.jayathu;

import com.swms.service.jayathu.*;
import com.swms.dto.jayathu.*;
import com.swms.repository.jayathu.SmartBinRepositoryCityAuth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AnalyticsServiceTest {

    @Mock
    private SmartBinRepositoryCityAuth smartBinRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetDashboardAnalytics_Success() {
       
        SmartBinRepositoryCityAuth.TotalWasteStats totalStats = mock(SmartBinRepositoryCityAuth.TotalWasteStats.class);
        when(totalStats.getTotalWaste()).thenReturn(100);
        when(totalStats.getTotalCapacity()).thenReturn(200);
        when(smartBinRepository.findTotalWasteStats()).thenReturn(totalStats);

        SmartBinRepositoryCityAuth.LocationSummary locSummary1 = mock(SmartBinRepositoryCityAuth.LocationSummary.class);
        when(locSummary1.get_id()).thenReturn("loc1");
        when(locSummary1.getTotalWaste()).thenReturn(50);
        when(locSummary1.getBinCount()).thenReturn(2);
        SmartBinRepositoryCityAuth.LocationSummary locSummary2 = mock(SmartBinRepositoryCityAuth.LocationSummary.class);
        when(locSummary2.get_id()).thenReturn("loc2");
        when(locSummary2.getTotalWaste()).thenReturn(60);
        when(locSummary2.getBinCount()).thenReturn(3);
        when(smartBinRepository.findWasteByLocation()).thenReturn(Arrays.asList(locSummary1, locSummary2));

        SmartBinRepositoryCityAuth.StatusSummary statusSummary1 = mock(SmartBinRepositoryCityAuth.StatusSummary.class);
        when(statusSummary1.get_id()).thenReturn("active");
        when(statusSummary1.getCount()).thenReturn(5);
        SmartBinRepositoryCityAuth.StatusSummary statusSummary2 = mock(SmartBinRepositoryCityAuth.StatusSummary.class);
        when(statusSummary2.get_id()).thenReturn("full");
        when(statusSummary2.getCount()).thenReturn(3);
        when(smartBinRepository.findBinStatusSummary()).thenReturn(Arrays.asList(statusSummary1, statusSummary2));

    
        DashboardSummaryDto result = analyticsService.getDashboardAnalytics();

      
        assertEquals(100, result.getTotalWaste().getTotalWaste());
        assertEquals(200, result.getTotalWaste().getTotalCapacity());
        assertEquals(2, result.getWasteByLocation().size());
        assertEquals("loc1", result.getWasteByLocation().get(0).getLocation());
        assertEquals(50, result.getWasteByLocation().get(0).getTotalWaste());
        assertEquals(2, result.getWasteByLocation().get(0).getBinCount());
        assertEquals(2, result.getBinStatus().size());
        assertEquals("active", result.getBinStatus().get(0).getStatus());
        assertEquals(5, result.getBinStatus().get(0).getCount());
       
        assertEquals(2, result.getTopLocations().size());
        verify(smartBinRepository, times(1)).findTotalWasteStats();
        verify(smartBinRepository, times(1)).findWasteByLocation();
        verify(smartBinRepository, times(1)).findBinStatusSummary();
    }

    @Test
    public void testGetWasteByType_Success() {
       
        SmartBinRepositoryCityAuth.WasteTypeSummary typeSummary1 = mock(SmartBinRepositoryCityAuth.WasteTypeSummary.class);
        when(typeSummary1.get_id()).thenReturn("plastic");
        when(typeSummary1.getTotalWaste()).thenReturn(50);
        when(typeSummary1.getBinCount()).thenReturn(2);
        SmartBinRepositoryCityAuth.WasteTypeSummary typeSummary2 = mock(SmartBinRepositoryCityAuth.WasteTypeSummary.class);
        when(typeSummary2.get_id()).thenReturn("organic");
        when(typeSummary2.getTotalWaste()).thenReturn(100);
        when(typeSummary2.getBinCount()).thenReturn(3);
        when(smartBinRepository.findWasteByType()).thenReturn(Arrays.asList(typeSummary1, typeSummary2));

       
        List<WasteTypeDto> result = analyticsService.getWasteByType();

       
        assertEquals(2, result.size());
        assertEquals("plastic", result.get(0).getWasteType());
        assertEquals(50, result.get(0).getTotalWaste());
        assertEquals(2, result.get(0).getBinCount());
      
        assertEquals(33.33, result.get(0).getPercentage(), 0.01); 
        assertEquals("organic", result.get(1).getWasteType());
        assertEquals(100, result.get(1).getTotalWaste());
        assertEquals(3, result.get(1).getBinCount());
        assertEquals(66.67, result.get(1).getPercentage(), 0.01); 
        verify(smartBinRepository, times(1)).findWasteByType();
    }

    @Test
    public void testGetWasteByTypeFiltered_Success() {
       
        SmartBinRepositoryCityAuth.WasteTypeSummary typeSummary1 = mock(SmartBinRepositoryCityAuth.WasteTypeSummary.class);
        when(typeSummary1.get_id()).thenReturn("plastic");
        when(typeSummary1.getTotalWaste()).thenReturn(50);
        when(typeSummary1.getBinCount()).thenReturn(2);
        SmartBinRepositoryCityAuth.WasteTypeSummary typeSummary2 = mock(SmartBinRepositoryCityAuth.WasteTypeSummary.class);
        when(typeSummary2.get_id()).thenReturn("organic");
        when(typeSummary2.getTotalWaste()).thenReturn(100);
        when(typeSummary2.getBinCount()).thenReturn(3);
        when(smartBinRepository.findWasteByType()).thenReturn(Arrays.asList(typeSummary1, typeSummary2));

       
        List<WasteTypeDto> result = analyticsService.getWasteByTypeFiltered("plastic");

       
        assertEquals(1, result.size());
        assertEquals("plastic", result.get(0).getWasteType());
        assertEquals(50, result.get(0).getTotalWaste());
        assertEquals(2, result.get(0).getBinCount());
        assertEquals(33.33, result.get(0).getPercentage(), 0.01); 
        verify(smartBinRepository, times(1)).findWasteByType();
    }

    @Test
    public void testGetWasteByTypeFiltered_EmptyWasteType() {
       
        List<WasteTypeDto> result = analyticsService.getWasteByTypeFiltered(null);

      
        assertEquals(0, result.size());
        verify(smartBinRepository, times(0)).findWasteByType();
        
        
        List<WasteTypeDto> result2 = analyticsService.getWasteByTypeFiltered("");
        assertEquals(0, result2.size());
        verify(smartBinRepository, times(0)).findWasteByType();
    }

    @Test
    public void testGetTotalPlasticWaste_Success() {
       
        SmartBinRepositoryCityAuth.PlasticStats plasticStats = mock(SmartBinRepositoryCityAuth.PlasticStats.class);
        when(plasticStats.getTotalPlastic()).thenReturn(75);
        when(smartBinRepository.findTotalPlasticWaste()).thenReturn(Collections.singletonList(plasticStats));

       
        int result = analyticsService.getTotalPlasticWaste();

       
        assertEquals(75, result);
        verify(smartBinRepository, times(1)).findTotalPlasticWaste();
    }

    @Test
    public void testGetTotalPlasticWaste_NoData() {
        
        when(smartBinRepository.findTotalPlasticWaste()).thenReturn(Collections.emptyList());

       
        int result = analyticsService.getTotalPlasticWaste();

       
        assertEquals(0, result);
        verify(smartBinRepository, times(1)).findTotalPlasticWaste();
    }
}