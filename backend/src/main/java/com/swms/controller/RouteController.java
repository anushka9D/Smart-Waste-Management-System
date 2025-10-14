package com.swms.controller;

import com.swms.dto.ApiResponse;
import com.swms.model.*;
import com.swms.service.RouteOptimizationService;
import com.swms.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/routes")
@CrossOrigin(originPatterns = "*", maxAge = 3600)
public class RouteController {
    
    @Autowired
    private RouteOptimizationService routeOptimizationService;
    
    @Autowired
    private RouteService routeService;
    
    /**
     * CityAuthority creates new route
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CollectionRoute>> createRoute() {
        try {
            // Get bins that need collection
            List<SmartBin> bins = routeOptimizationService.getBinsNeedingCollection();
            
            if (bins.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("No bins need collection at this time"));
            }
            
            // For demo purposes, using a fixed depot location
            // In a real implementation, this would come from configuration
            GPSLocation depot = new GPSLocation(40.7128, -74.0060); // New York City coordinates
            
            // Create optimized route
            CollectionRoute route = routeOptimizationService.createOptimizedRoute(bins, depot);
            
            // Assign available truck, driver, and staff
            routeService.assignResourcesToRoute(route);
            
            return ResponseEntity.ok(ApiResponse.success("Route created successfully", route));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to create route: " + e.getMessage()));
        }
    }
    
    /**
     * Driver gets assigned routes
     */
    @GetMapping("/driver/{driverId}")
    public ResponseEntity<ApiResponse<List<CollectionRoute>>> getRoutesForDriver(@PathVariable String driverId) {
        try {
            List<CollectionRoute> routes = routeService.getRoutesByDriverId(driverId);
            return ResponseEntity.ok(ApiResponse.success("Routes retrieved successfully", routes));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to retrieve routes: " + e.getMessage()));
        }
    }
    
    /**
     * Staff gets assigned routes
     */
    @GetMapping("/staff/{staffId}")
    public ResponseEntity<ApiResponse<List<CollectionRoute>>> getRoutesForStaff(@PathVariable String staffId) {
        try {
            List<CollectionRoute> routes = routeService.getRoutesByStaffId(staffId);
            return ResponseEntity.ok(ApiResponse.success("Routes retrieved successfully", routes));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to retrieve routes: " + e.getMessage()));
        }
    }
    
    /**
     * Update route status
     */
    @PutMapping("/{routeId}/status")
    public ResponseEntity<ApiResponse<String>> updateRouteStatus(
            @PathVariable String routeId, 
            @RequestParam String status) {
        try {
            String result = routeService.updateRouteStatus(routeId, status);
            return ResponseEntity.ok(ApiResponse.success(result, null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to update route status: " + e.getMessage()));
        }
    }
    
    /**
     * Mark bin collection as complete
     */
    @PutMapping("/stops/{stopId}/complete")
    public ResponseEntity<ApiResponse<String>> completeRouteStop(@PathVariable String stopId) {
        try {
            String result = routeService.completeRouteStop(stopId);
            return ResponseEntity.ok(ApiResponse.success(result, null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to complete route stop: " + e.getMessage()));
        }
    }
    
    /**
     * Get all routes for a specific date
     */
    @GetMapping("/date/{date}")
    public ResponseEntity<ApiResponse<List<CollectionRoute>>> getRoutesByDate(@PathVariable String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate = LocalDate.parse(date, formatter);
            
            LocalDateTime startOfDay = localDate.atStartOfDay();
            LocalDateTime endOfDay = localDate.atTime(23, 59, 59);
            
            List<CollectionRoute> routes = routeService.getRoutesByDate(startOfDay, endOfDay);
            return ResponseEntity.ok(ApiResponse.success("Routes retrieved successfully", routes));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to retrieve routes: " + e.getMessage()));
        }
    }
}