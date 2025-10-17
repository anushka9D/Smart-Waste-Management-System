package com.swms.controller.jayathu;

import com.swms.dto.*;
import com.swms.dto.jayathu.AnalyticsDto;
import com.swms.dto.jayathu.BinStatusDto;
import com.swms.dto.jayathu.DashboardSummaryDto;
import com.swms.dto.jayathu.LocationWasteDto;
import com.swms.dto.jayathu.TotalWasteDto;
import com.swms.dto.jayathu.WasteTypeDto;
import com.swms.service.*;
import com.swms.service.jayathu.AnalyticsService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:5173" })
public class AnalyticsController {

     private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardSummaryDto> getDashboard() {
        return ResponseEntity.ok(analyticsService.getDashboardAnalytics());
    }

    @GetMapping("/locations/waste")
    public ResponseEntity<List<LocationWasteDto>> getWasteByLocation() {
        return ResponseEntity.ok(analyticsService.getWasteByLocation());
    }

    @GetMapping("/bins/status")
    public ResponseEntity<List<BinStatusDto>> getBinStatus() {
        return ResponseEntity.ok(analyticsService.getBinStatusSummary());
    }

    @GetMapping("/waste/total")
    public ResponseEntity<TotalWasteDto> getTotalWaste() {
        return ResponseEntity.ok(analyticsService.getTotalWasteMetrics());
    }

    
    @GetMapping("/waste/type")
    public ResponseEntity<List<WasteTypeDto>> getWasteByType() {
        return ResponseEntity.ok(analyticsService.getWasteByType());
    }

    
    @GetMapping("/waste/e-waste")
    public ResponseEntity<Integer> getTotalEWaste() {
        return ResponseEntity.ok(analyticsService.getTotalEWaste());
    }

    
    @GetMapping("/waste/recyclable")
    public ResponseEntity<Integer> getTotalRecyclableWaste() {
        return ResponseEntity.ok(analyticsService.getTotalRecyclableWaste());
    }

   
    @GetMapping("/locations/most-waste")
    public ResponseEntity<LocationWasteDto> getLocationWithMostWaste() {
        return ResponseEntity.ok(analyticsService.getLocationWithMostWaste());
    }

   
    @GetMapping("/waste/plastic")
    public ResponseEntity<Integer> getTotalPlasticWaste() {
        return ResponseEntity.ok(analyticsService.getTotalPlasticWaste());
    }

    
    @GetMapping("/waste/organic")
    public ResponseEntity<Integer> getTotalOrganicWaste() {
        return ResponseEntity.ok(analyticsService.getTotalOrganicWaste());
    }

   
    @GetMapping("/waste/metal")
    public ResponseEntity<Integer> getTotalMetalWaste() {
        return ResponseEntity.ok(analyticsService.getTotalMetalWaste());
    }

    
    @GetMapping("/waste/type/filtered/{wasteType}")
    public ResponseEntity<List<WasteTypeDto>> getWasteByTypeFiltered(@PathVariable String wasteType) {
        System.out.println("Fetching filtered waste type data for: " + wasteType);
        try {
            List<WasteTypeDto> result = analyticsService.getWasteByTypeFiltered(wasteType);
            System.out.println("Filtered waste type data result size: " + result.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("Error fetching filtered waste type data: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

   
    @GetMapping("/locations/waste/filtered/{wasteType}")
    public ResponseEntity<List<LocationWasteDto>> getWasteByLocationFiltered(@PathVariable String wasteType) {
        System.out.println("Fetching filtered location data for: " + wasteType);
        try {
            List<LocationWasteDto> result = analyticsService.getWasteByLocationFiltered(wasteType);
            System.out.println("Filtered location data result size: " + result.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("Error fetching filtered location data: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

   
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok(analyticsService.testService());
    }

  
    @GetMapping("/charts/locations")
    public ResponseEntity<List<AnalyticsDto>> getLocationChartData() {
        return ResponseEntity.ok(analyticsService.getWasteByLocationForCharts());
    }

    @GetMapping("/charts/status")
    public ResponseEntity<List<AnalyticsDto>> getStatusChartData() {
        return ResponseEntity.ok(analyticsService.getBinStatusForCharts());
    }

    
    @GetMapping("/charts/waste-type")
    public ResponseEntity<List<AnalyticsDto>> getWasteTypeChartData() {
        return ResponseEntity.ok(analyticsService.getWasteByTypeForCharts());
    }
}