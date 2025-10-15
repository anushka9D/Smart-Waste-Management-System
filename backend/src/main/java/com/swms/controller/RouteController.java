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
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/routes")
@CrossOrigin(originPatterns = "*", maxAge = 3600)
public class RouteController {
    
    @Autowired
    private RouteOptimizationService routeOptimizationService;
    
    @Autowired
    private RouteService routeService;
    
    /**
     * CityAuthority creates new route with specific resource assignments
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CollectionRoute>> createRoute(@RequestBody Map<String, Object> requestData) {
        try {
            // Extract resource assignments from request
            String truckId = (String) requestData.get("truckId");
            String driverId = (String) requestData.get("driverId");
            List<String> staffIds = new ArrayList<>();
            
            // Handle staffIds as either List or String
            Object staffIdsObj = requestData.get("staffIds");
            if (staffIdsObj instanceof List) {
                staffIds = (List<String>) staffIdsObj;
            } else if (staffIdsObj instanceof String) {
                staffIds.add((String) staffIdsObj);
            }
            
            // Get bins that need collection
            List<DummySmartBin> bins = routeOptimizationService.getBinsNeedingCollection();
            
            if (bins.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("No bins need collection at this time"));
            }
            
            // For demo purposes, using a fixed depot location
            // In a real implementation, this would come from configuration
            GPSLocation depot = new GPSLocation(40.7128, -74.0060); // New York City coordinates
            
            // Create optimized route
            CollectionRoute route = routeOptimizationService.createOptimizedRoute(bins, depot);
            
            // Assign specific truck, driver, and staff to the route
            routeService.assignSpecificResourcesToRoute(route, truckId, driverId, staffIds);
            
            return ResponseEntity.ok(ApiResponse.success("Route created successfully", route));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to create route: " + e.getMessage()));
        }
    }
    
    /**
     * CityAuthority creates a specific route by route ID with resource assignments
     */
    @PostMapping("/create/{routeIndex}")
    public ResponseEntity<ApiResponse<CollectionRoute>> createSpecificRoute(
            @PathVariable int routeIndex, 
            @RequestBody Map<String, Object> requestData) {
        try {
            // Extract resource assignments from request
            String truckId = (String) requestData.get("truckId");
            String driverId = (String) requestData.get("driverId");
            List<String> staffIds = new ArrayList<>();
            
            // Handle staffIds as either List or String
            Object staffIdsObj = requestData.get("staffIds");
            if (staffIdsObj instanceof List) {
                staffIds = (List<String>) staffIdsObj;
            } else if (staffIdsObj instanceof String) {
                staffIds.add((String) staffIdsObj);
            }
            
            // Get all smart bins with "FULL" status (case-insensitive)
            List<DummySmartBin> allBins = routeOptimizationService.getDummySmartBinRepository().findAll()
                .stream()
                .filter(bin -> "FULL".equalsIgnoreCase(bin.getStatus()))
                .collect(Collectors.toList());
            
            if (allBins.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("No bins need collection at this time"));
            }
            
            // For demo purposes, using a fixed depot location
            GPSLocation depot = new GPSLocation(40.7128, -74.0060); // New York City coordinates
            
            // Group bins by proximity to create separate routes for each geographic cluster
            List<List<DummySmartBin>> routeGroups = groupBinsByProximity(allBins, depot);
            
            // Filter groups to only include those with at least 2 bins
            List<List<DummySmartBin>> validRouteGroups = routeGroups.stream()
                .filter(group -> group.size() >= 2)
                .collect(Collectors.toList());
            
            // If no valid groups, use all bins as one group
            if (validRouteGroups.isEmpty()) {
                validRouteGroups.add(allBins);
            }
            
            // Check if routeIndex is valid
            if (routeIndex < 0 || routeIndex >= validRouteGroups.size()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid route index. Available routes: 0 to " + (validRouteGroups.size() - 1)));
            }
            
            // Get the specific group of bins for this route
            List<DummySmartBin> bins = validRouteGroups.get(routeIndex);
            
            // Create optimized route
            CollectionRoute route = routeOptimizationService.createOptimizedRoute(bins, depot);
            
            // Assign specific truck, driver, and staff to the route
            routeService.assignSpecificResourcesToRoute(route, truckId, driverId, staffIds);
            
            return ResponseEntity.ok(ApiResponse.success("Route created successfully", route));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to create route: " + e.getMessage()));
        }
    }
    
    /**
     * Get route details for display without storing in database
     * Shows all routes that can be assigned with full bin details
     */
    @GetMapping("/preview")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRoutePreview() {
        try {
            // Get all smart bins with "FULL" status (case-insensitive)
            List<DummySmartBin> allBins = routeOptimizationService.getDummySmartBinRepository().findAll()
                .stream()
                .filter(bin -> "FULL".equalsIgnoreCase(bin.getStatus()))
                .collect(Collectors.toList());
            
            if (allBins.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("No bins need collection at this time"));
            }
            
            // For demo purposes, using a fixed depot location
            GPSLocation depot = new GPSLocation(40.7128, -74.0060); // New York City coordinates
            
            // Group bins by proximity to create separate routes for each geographic cluster
            List<List<DummySmartBin>> routeGroups = groupBinsByProximity(allBins, depot);
            
            // Create routes for each group (for display only, not saved to DB)
            List<Map<String, Object>> routesData = new ArrayList<>();
            
            // Get ALL resources (not just available ones)
            List<Truck> allTrucks = getAllTrucks();
            List<Driver> allDrivers = getAllDrivers();
            List<WasteCollectionStaff> allStaff = getAllStaff();
            
            // Get available resources (excluding already assigned ones)
            List<Truck> availableTrucks = getAvailableTrucks();
            List<Driver> availableDrivers = getAvailableDrivers();
            List<WasteCollectionStaff> availableStaff = getAvailableStaff();
            
            // Filter available drivers to only include those with availability = true
            availableDrivers = availableDrivers.stream()
                .filter(Driver::isAvailability)
                .collect(Collectors.toList());
                
            // Filter available staff to only include those with availability = true
            availableStaff = availableStaff.stream()
                .filter(WasteCollectionStaff::isAvailability)
                .collect(Collectors.toList());
            
            for (List<DummySmartBin> bins : routeGroups) {
                // Skip groups with less than 2 bins
                if (bins.size() < 2) {
                    continue;
                }
                
                // Calculate total capacity
                double totalCapacity = bins.stream()
                    .mapToDouble(DummySmartBin::getCapacity)
                    .sum();
                
                // Create optimized route stops (not saved to DB)
                List<RouteStop> routeStops = routeOptimizationService.calculateNearestNeighborRoute(bins, depot);
                
                // Get stop IDs (these would be generated if saved)
                List<String> stopIds = new ArrayList<>();
                for (int i = 0; i < routeStops.size(); i++) {
                    stopIds.add("STOP_" + System.currentTimeMillis() + "_" + i);
                }
                
                // Get bin IDs for stops
                List<String> binIds = routeStops.stream()
                    .map(RouteStop::getBinId)
                    .collect(Collectors.toList());
                
                // Calculate route metrics
                Map<String, Object> metrics = routeOptimizationService.calculateTotalRouteMetrics(routeStops, depot);
                
                // Find suitable resources for this route
                String suitableTruckId = findSuitableTruck(allTrucks, totalCapacity);
                String suitableDriverId = findSuitableDriver(allDrivers);
                List<String> suitableStaffIds = findSuitableStaff(allStaff, 2); // Up to 2 staff members
                
                // Prepare route data (not saved to DB)
                Map<String, Object> routeData = new HashMap<>();
                routeData.put("routeId", "ROUTE_" + System.currentTimeMillis() + "_" + routesData.size());
                routeData.put("date", LocalDateTime.now());
                routeData.put("status", "Not assigned"); // Default status
                routeData.put("assignedTruckId", suitableTruckId);
                routeData.put("assignedDriverId", suitableDriverId);
                routeData.put("assignedStaffIds", suitableStaffIds);
                routeData.put("totalDistance", metrics.get("totalDistance"));
                routeData.put("estimatedTime", metrics.get("estimatedTime"));
                routeData.put("stopIds", stopIds);
                routeData.put("binIds", binIds);
                routeData.put("totalCapacity", totalCapacity);
                
                routesData.add(routeData);
            }
            
            // If no valid routes found, create one route with all bins
            if (routesData.isEmpty() && !allBins.isEmpty()) {
                // Calculate total capacity
                double totalCapacity = allBins.stream()
                    .mapToDouble(DummySmartBin::getCapacity)
                    .sum();
                
                // Create optimized route stops (not saved to DB)
                List<RouteStop> routeStops = routeOptimizationService.calculateNearestNeighborRoute(allBins, depot);
                
                // Get stop IDs (these would be generated if saved)
                List<String> stopIds = new ArrayList<>();
                for (int i = 0; i < routeStops.size(); i++) {
                    stopIds.add("STOP_" + System.currentTimeMillis() + "_" + i);
                }
                
                // Get bin IDs for stops
                List<String> binIds = routeStops.stream()
                    .map(RouteStop::getBinId)
                    .collect(Collectors.toList());
                
                // Calculate route metrics
                Map<String, Object> metrics = routeOptimizationService.calculateTotalRouteMetrics(routeStops, depot);
                
                // Find suitable resources for this route
                String suitableTruckId = findSuitableTruck(allTrucks, totalCapacity);
                String suitableDriverId = findSuitableDriver(allDrivers);
                List<String> suitableStaffIds = findSuitableStaff(allStaff, 2); // Up to 2 staff members
                
                // Prepare route data (not saved to DB)
                Map<String, Object> routeData = new HashMap<>();
                routeData.put("routeId", "ROUTE_" + System.currentTimeMillis());
                routeData.put("date", LocalDateTime.now());
                routeData.put("status", "Not assigned"); // Default status
                routeData.put("assignedTruckId", suitableTruckId);
                routeData.put("assignedDriverId", suitableDriverId);
                routeData.put("assignedStaffIds", suitableStaffIds);
                routeData.put("totalDistance", metrics.get("totalDistance"));
                routeData.put("estimatedTime", metrics.get("estimatedTime"));
                routeData.put("stopIds", stopIds);
                routeData.put("binIds", binIds);
                routeData.put("totalCapacity", totalCapacity);
                
                routesData.add(routeData);
            }
            
            // Prepare response data
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("routes", routesData);
            responseData.put("availableTrucks", availableTrucks);
            responseData.put("availableDrivers", availableDrivers);
            responseData.put("availableStaff", availableStaff);
            
            return ResponseEntity.ok(ApiResponse.success("Route preview retrieved successfully", responseData));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to get route preview: " + e.getMessage()));
        }
    }
    
    /**
     * Get all assigned routes
     */
    @GetMapping("/assigned")
    public ResponseEntity<ApiResponse<List<CollectionRoute>>> getAssignedRoutes() {
        try {
            List<CollectionRoute> assignedRoutes = routeService.getAssignedRoutes();
            return ResponseEntity.ok(ApiResponse.success("Assigned routes retrieved successfully", assignedRoutes));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to retrieve assigned routes: " + e.getMessage()));
        }
    }
    
    /**
     * Get ALL trucks (regardless of availability)
     */
    private List<Truck> getAllTrucks() {
        try {
            return routeOptimizationService.getTruckRepository().findAll();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    /**
     * Get ALL drivers (regardless of availability)
     */
    private List<Driver> getAllDrivers() {
        try {
            return routeOptimizationService.getDriverRepository().findAll();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    /**
     * Get ALL waste collection staff (regardless of availability)
     */
    private List<WasteCollectionStaff> getAllStaff() {
        try {
            return routeOptimizationService.getWasteCollectionStaffRepository().findAll();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    /**
     * Find a suitable truck (now checks all trucks, not just available ones)
     */
    private String findSuitableTruck(List<Truck> allTrucks, double requiredCapacity) {
        for (Truck truck : allTrucks) {
            if (truck.getCapacity() >= requiredCapacity) {
                return truck.getTruckId();
            }
        }
        return null;
    }
    
    /**
     * Find a suitable driver (now checks only available drivers)
     */
    private String findSuitableDriver(List<Driver> allDrivers) {
        // Filter to only include available drivers
        List<Driver> availableDrivers = allDrivers.stream()
            .filter(Driver::isAvailability)
            .filter(driver -> driver.getCurrentRouteId() == null)
            .collect(Collectors.toList());
        
        for (Driver driver : availableDrivers) {
            return driver.getUserId();
        }
        return null;
    }
    
    /**
     * Find suitable staff members (up to maxCount) (now checks only available staff)
     */
    private List<String> findSuitableStaff(List<WasteCollectionStaff> allStaff, int maxCount) {
        List<String> staffIds = new ArrayList<>();
        
        // Filter to only include available staff
        List<WasteCollectionStaff> availableStaff = allStaff.stream()
            .filter(WasteCollectionStaff::isAvailability)
            .filter(staff -> staff.getCurrentRouteId() == null)
            .collect(Collectors.toList());
        
        for (WasteCollectionStaff staff : availableStaff) {
            if (staffIds.size() < maxCount) {
                staffIds.add(staff.getUserId());
            }
        }
        return staffIds;
    }
    
    /**
     * Get available trucks (capacity > 0 and currentStatus = "NOT_IN_USE" or "AVAILABLE")
     */
    private List<Truck> getAvailableTrucks() {
        try {
            // Get trucks with status "NOT_IN_USE"
            List<Truck> notInUseTrucks = routeOptimizationService.getTruckRepository()
                .findByCurrentStatusAndAssignedDriverIdIsNull("NOT_IN_USE");
            
            // Get trucks with status "AVAILABLE"
            List<Truck> availableTrucks = routeOptimizationService.getTruckRepository()
                .findByCurrentStatusAndAssignedDriverIdIsNull("AVAILABLE");
            
            // Combine both lists and remove duplicates
            Set<Truck> combinedTrucks = new HashSet<>();
            combinedTrucks.addAll(notInUseTrucks);
            combinedTrucks.addAll(availableTrucks);
            
            // Filter to only include trucks with capacity > 0
            return combinedTrucks.stream()
                .filter(truck -> truck.getCapacity() > 0)
                .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    /**
     * Get available drivers (availability = true and not assigned to a route)
     */
    private List<Driver> getAvailableDrivers() {
        try {
            return routeOptimizationService.getDriverRepository()
                .findByAvailabilityTrueAndCurrentRouteIdIsNull();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    /**
     * Get available waste collection staff (availability = true and not assigned to a route)
     */
    private List<WasteCollectionStaff> getAvailableStaff() {
        try {
            return routeOptimizationService.getWasteCollectionStaffRepository()
                .findByAvailabilityTrueAndCurrentRouteIdIsNull();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    /**
     * Groups bins by proximity using a simple clustering algorithm
     */
    private List<List<DummySmartBin>> groupBinsByProximity(List<DummySmartBin> bins, GPSLocation depot) {
        List<List<DummySmartBin>> groups = new ArrayList<>();
        
        // Simple grouping by distance - bins within 3km of each other are grouped
        final double GROUPING_DISTANCE_KM = 3.0;
        
        for (DummySmartBin bin : bins) {
            boolean addedToGroup = false;
            
            // Check if bin can be added to an existing group
            for (List<DummySmartBin> group : groups) {
                // Check if bin is close to any bin in the group
                for (DummySmartBin groupBin : group) {
                    double distance = calculateDistance(
                        bin.getCoordinates(), groupBin.getCoordinates());
                    
                    if (distance <= GROUPING_DISTANCE_KM) {
                        group.add(bin);
                        addedToGroup = true;
                        break;
                    }
                }
                
                if (addedToGroup) {
                    break;
                }
            }
            
            // If not added to any group, create a new group
            if (!addedToGroup) {
                List<DummySmartBin> newGroup = new ArrayList<>();
                newGroup.add(bin);
                groups.add(newGroup);
            }
        }
        
        return groups;
    }
    
    /**
     * Calculates distance between two GPS points using Haversine formula
     */
    private double calculateDistance(GPSLocation point1, GPSLocation point2) {
        double lat1 = Math.toRadians(point1.getLatitude());
        double lon1 = Math.toRadians(point1.getLongitude());
        double lat2 = Math.toRadians(point2.getLatitude());
        double lon2 = Math.toRadians(point2.getLongitude());
        
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;
        
        double a = Math.pow(Math.sin(dLat / 2), 2) +
                   Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dLon / 2), 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return 6371 * c; // Earth radius in kilometers
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
    
    /**
     * Get suitable trucks for a route based on capacity and status
     */
    @GetMapping("/suitable-trucks")
    public ResponseEntity<ApiResponse<List<Truck>>> getSuitableTrucks(@RequestParam double requiredCapacity) {
        try {
            List<Truck> suitableTrucks = routeOptimizationService.getTruckRepository()
                .findByCurrentStatusAndCapacityGreaterThanEqual("NOT_IN_USE", requiredCapacity);
            
            // If no trucks found with "NOT_IN_USE", try "AVAILABLE"
            if (suitableTrucks.isEmpty()) {
                suitableTrucks = routeOptimizationService.getTruckRepository()
                    .findByCurrentStatusAndCapacityGreaterThanEqual("AVAILABLE", requiredCapacity);
            }
            
            return ResponseEntity.ok(ApiResponse.success("Suitable trucks retrieved successfully", suitableTrucks));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to retrieve suitable trucks: " + e.getMessage()));
        }
    }
    
    /**
     * Get all drivers
     */
    @GetMapping("/available-drivers")
    public ResponseEntity<ApiResponse<List<Driver>>> getAvailableDriversEndpoint() {
        try {
            List<Driver> allDrivers = routeOptimizationService.getDriverRepository().findAll();
            
            return ResponseEntity.ok(ApiResponse.success("All drivers retrieved successfully", allDrivers));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to retrieve drivers: " + e.getMessage()));
        }
    }
    
    /**
     * Get all waste collection staff
     */
    @GetMapping("/available-staff")
    public ResponseEntity<ApiResponse<List<WasteCollectionStaff>>> getAvailableStaffEndpoint() {
        try {
            List<WasteCollectionStaff> allStaff = routeOptimizationService.getWasteCollectionStaffRepository().findAll();
            
            return ResponseEntity.ok(ApiResponse.success("All staff retrieved successfully", allStaff));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to retrieve staff: " + e.getMessage()));
        }
    }
    
    /**
     * Create a specific route with selected resources
     */
    @PostMapping("/create-with-resources/{routeIndex}")
    public ResponseEntity<ApiResponse<CollectionRoute>> createRouteWithResources(
            @PathVariable int routeIndex,
            @RequestBody Map<String, Object> requestData) {
        try {
            // Extract resource assignments from request
            String truckId = (String) requestData.get("truckId");
            String driverId = (String) requestData.get("driverId");
            List<String> staffIds = new ArrayList<>();
            
            // Handle staffIds as either List or String
            Object staffIdsObj = requestData.get("staffIds");
            if (staffIdsObj instanceof List) {
                staffIds = (List<String>) staffIdsObj;
            } else if (staffIdsObj instanceof String) {
                staffIds.add((String) staffIdsObj);
            }
            
            // Get all smart bins with "FULL" status (case-insensitive)
            List<DummySmartBin> allBins = routeOptimizationService.getDummySmartBinRepository().findAll()
                .stream()
                .filter(bin -> "FULL".equalsIgnoreCase(bin.getStatus()))
                .collect(Collectors.toList());
            
            if (allBins.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("No bins need collection at this time"));
            }
            
            // For demo purposes, using a fixed depot location
            GPSLocation depot = new GPSLocation(40.7128, -74.0060); // New York City coordinates
            
            // Group bins by proximity to create separate routes for each geographic cluster
            List<List<DummySmartBin>> routeGroups = groupBinsByProximity(allBins, depot);
            
            // Filter groups to only include those with at least 2 bins
            List<List<DummySmartBin>> validRouteGroups = routeGroups.stream()
                .filter(group -> group.size() >= 2)
                .collect(Collectors.toList());
            
            // If no valid groups, use all bins as one group
            if (validRouteGroups.isEmpty()) {
                validRouteGroups.add(allBins);
            }
            
            // Check if routeIndex is valid
            if (routeIndex < 0 || routeIndex >= validRouteGroups.size()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid route index. Available routes: 0 to " + (validRouteGroups.size() - 1)));
            }
            
            // Get the specific group of bins for this route
            List<DummySmartBin> bins = validRouteGroups.get(routeIndex);
            
            // Create optimized route
            CollectionRoute route = routeOptimizationService.createOptimizedRoute(bins, depot);
            
            // Assign specific truck, driver, and staff to the route
            routeService.assignSpecificResourcesToRoute(route, truckId, driverId, staffIds);
            
            return ResponseEntity.ok(ApiResponse.success("Route created successfully", route));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to create route: " + e.getMessage()));
        }
    }
}