package com.swms.controller;

import com.swms.dto.ApiResponse;
import com.swms.model.*;
import com.swms.security.JwtUtil;
import com.swms.service.RouteOptimizationService;
import com.swms.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
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
    
    @Autowired
    private JwtUtil jwtUtil;
    
    
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CollectionRoute>> createRoute(@RequestBody Map<String, Object> requestData) {
        try {
            
            String truckId = (String) requestData.get("truckId");
            String driverId = (String) requestData.get("driverId");
            List<String> staffIds = new ArrayList<>();
            
            
            Object staffIdsObj = requestData.get("staffIds");
            if (staffIdsObj instanceof List) {
                staffIds = (List<String>) staffIdsObj;
            } else if (staffIdsObj instanceof String) {
                staffIds.add((String) staffIdsObj);
            }
            
            // Get bins needing collection
            List<SmartBin> bins = routeOptimizationService.getBinsNeedingCollection();
            
            if (bins.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("No bins need collection at this time"));
            }
            
            
            GPSLocation depot = new GPSLocation(40.7128, -74.0060); 
            
            
            CollectionRoute route = routeOptimizationService.createOptimizedRoute(bins, depot);
            
            
            routeService.assignSpecificResourcesToRoute(route, truckId, driverId, staffIds);
            
            return ResponseEntity.ok(ApiResponse.success("Route created successfully", route));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to create route: " + e.getMessage()));
        }
    }
    
   
    @PostMapping("/create/{routeIndex}")
    public ResponseEntity<ApiResponse<CollectionRoute>> createSpecificRoute(
            @PathVariable int routeIndex, 
            @RequestBody Map<String, Object> requestData) {
        try {
            
            String truckId = (String) requestData.get("truckId");
            String driverId = (String) requestData.get("driverId");
            List<String> staffIds = new ArrayList<>();
            
            
            Object staffIdsObj = requestData.get("staffIds");
            if (staffIdsObj instanceof List) {
                staffIds = (List<String>) staffIdsObj;
            } else if (staffIdsObj instanceof String) {
                staffIds.add((String) staffIdsObj);
            }
            
            
            List<SmartBin> allBins = routeOptimizationService.getSmartBinRepository().findAll()
                .stream()
                .filter(bin -> "FULL".equalsIgnoreCase(bin.getStatus()))
                .collect(Collectors.toList());
            
            if (allBins.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("No bins need collection at this time"));
            }
            
           
            GPSLocation depot = new GPSLocation(40.7128, -74.0060); 
            
            
            List<List<SmartBin>> routeGroups = groupBinsByProximity(allBins, depot);
            
            
            List<List<SmartBin>> validRouteGroups = routeGroups.stream()
                .filter(group -> group.size() >= 2)
                .collect(Collectors.toList());
            
           
            if (validRouteGroups.isEmpty()) {
                validRouteGroups.add(allBins);
            }
            
            
            if (routeIndex < 0 || routeIndex >= validRouteGroups.size()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid route index. Available routes: 0 to " + (validRouteGroups.size() - 1)));
            }
            
            
            List<SmartBin> bins = validRouteGroups.get(routeIndex);
            
            
            CollectionRoute route = routeOptimizationService.createOptimizedRoute(bins, depot);
            
            
            routeService.assignSpecificResourcesToRoute(route, truckId, driverId, staffIds);
            
            return ResponseEntity.ok(ApiResponse.success("Route created successfully", route));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to create route: " + e.getMessage()));
        }
    }
    
   
    @GetMapping("/preview")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRoutePreview() {
        try {
            
            List<SmartBin> allBins = routeOptimizationService.getSmartBinRepository().findAll()
                .stream()
                .filter(bin -> "FULL".equalsIgnoreCase(bin.getStatus()))
                .collect(Collectors.toList());
            
            
            List<Map<String, Object>> routesData = new ArrayList<>();
            
           
            if (!allBins.isEmpty()) {
               
                GPSLocation depot = new GPSLocation(40.7128, -74.0060); 
                
                
                List<List<SmartBin>> routeGroups = groupBinsByProximity(allBins, depot);
                
                
                List<Truck> allTrucks = getAllTrucks();
                List<Driver> allDrivers = getAllDrivers();
                List<WasteCollectionStaff> allStaff = getAllStaff();
                
                
                List<Truck> availableTrucks = getAvailableTrucks();
                List<Driver> availableDrivers = getAvailableDrivers();
                List<WasteCollectionStaff> availableStaff = getAvailableStaff();
                
           
                availableDrivers = availableDrivers.stream()
                    .filter(Driver::isAvailability)
                    .collect(Collectors.toList());
                    
                
                availableStaff = availableStaff.stream()
                    .filter(WasteCollectionStaff::isAvailability)
                    .collect(Collectors.toList());
                
                for (List<SmartBin> bins : routeGroups) {
                    
                    if (bins.size() < 2) {
                        continue;
                    }
                    
                 
                    double totalCapacity = bins.stream()
                        .mapToDouble(SmartBin::getCapacity)
                        .sum();
                    
                   
                    List<RouteStop> routeStops = routeOptimizationService.calculateNearestNeighborRoute(bins, depot);
                    
                  
                    List<String> stopIds = new ArrayList<>();
                    for (int i = 0; i < routeStops.size(); i++) {
                        stopIds.add("STOP_" + System.currentTimeMillis() + "_" + i);
                    }
                    
                    
                    List<String> binIds = routeStops.stream()
                        .map(RouteStop::getBinId)
                        .collect(Collectors.toList());
                    
                    
                    Map<String, Object> metrics = routeOptimizationService.calculateTotalRouteMetrics(routeStops, depot);
                    
                    
                    String suitableTruckId = findSuitableTruck(allTrucks, totalCapacity);
                    String suitableDriverId = findSuitableDriver(allDrivers);
                    List<String> suitableStaffIds = findSuitableStaff(allStaff, 2);
                    
                    
                    Map<String, Object> routeData = new HashMap<>();
                    routeData.put("routeId", "ROUTE_" + System.currentTimeMillis() + "_" + routesData.size());
                    routeData.put("date", LocalDateTime.now());
                    routeData.put("status", "Not assigned"); 
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
                
                
                if (routesData.isEmpty() && !allBins.isEmpty()) {
                    
                    double totalCapacity = allBins.stream()
                        .mapToDouble(SmartBin::getCapacity)
                        .sum();
                    
                   
                    List<RouteStop> routeStops = routeOptimizationService.calculateNearestNeighborRoute(allBins, depot);
                    
                    
                    List<String> stopIds = new ArrayList<>();
                    for (int i = 0; i < routeStops.size(); i++) {
                        stopIds.add("STOP_" + System.currentTimeMillis() + "_" + i);
                    }
                    
                 
                    List<String> binIds = routeStops.stream()
                        .map(RouteStop::getBinId)
                        .collect(Collectors.toList());
                    
                   
                    Map<String, Object> metrics = routeOptimizationService.calculateTotalRouteMetrics(routeStops, depot);
                    
                    
                    String suitableTruckId = findSuitableTruck(allTrucks, totalCapacity);
                    String suitableDriverId = findSuitableDriver(allDrivers);
                    List<String> suitableStaffIds = findSuitableStaff(allStaff, 2); 
                    
                   
                    Map<String, Object> routeData = new HashMap<>();
                    routeData.put("routeId", "ROUTE_" + System.currentTimeMillis() + "_" + routesData.size());
                    routeData.put("date", LocalDateTime.now());
                    routeData.put("status", "Not assigned"); 
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
            }
            
            
            Map<String, Object> response = new HashMap<>();
            response.put("routes", routesData);
            response.put("totalRoutes", routesData.size());
            
            return ResponseEntity.ok(ApiResponse.success("Route preview generated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to generate route preview: " + e.getMessage()));
        }
    }
    
   
    private List<List<SmartBin>> groupBinsByProximity(List<SmartBin> bins, GPSLocation depot) {
        List<List<SmartBin>> groups = new ArrayList<>();
        
        
        final double GROUPING_DISTANCE_KM = 3.0;
        
        for (SmartBin bin : bins) {
            boolean addedToGroup = false;
            
            
            for (List<SmartBin> group : groups) {
                
                for (SmartBin groupBin : group) {
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
            
           
            if (!addedToGroup) {
                List<SmartBin> newGroup = new ArrayList<>();
                newGroup.add(bin);
                groups.add(newGroup);
            }
        }
        
        return groups;
    }
    
    
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
        
        return 6371 * c; 
    }
    
   
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
    
    
    @GetMapping("/driver/details")
    public ResponseEntity<ApiResponse<Driver>> getAuthenticatedDriverDetails() {
        try {
            
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                return ResponseEntity.status(401)
                    .body(ApiResponse.error("Unauthorized: No authentication found"));
            }
            
            
            String userId = extractUserIdFromJwt();
            if (userId == null) {
                return ResponseEntity.status(401)
                    .body(ApiResponse.error("Unauthorized: Unable to extract user ID from token"));
            }
            
          
            Optional<Driver> driverOpt = routeOptimizationService.getDriverRepository().findById(userId);
            if (driverOpt.isEmpty()) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.error("Driver not found with ID: " + userId));
            }
            
            return ResponseEntity.ok(ApiResponse.success("Driver details retrieved successfully", driverOpt.get()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to retrieve driver details: " + e.getMessage()));
        }
    }
    
    /**
     * Get routes assigned to the authenticated driver
     */
    @GetMapping("/assigned/driver")
    public ResponseEntity<ApiResponse<List<CollectionRoute>>> getAuthenticatedDriverRoutes() {
        try {
            // Get the authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                return ResponseEntity.status(401)
                    .body(ApiResponse.error("Unauthorized: No authentication found"));
            }
            
           
            String userId = extractUserIdFromJwt();
            if (userId == null) {
                return ResponseEntity.status(401)
                    .body(ApiResponse.error("Unauthorized: Unable to extract user ID from token"));
            }
            
           
            List<CollectionRoute> routes = routeService.getRoutesByDriverId(userId);
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
     * Get all assigned routes (for city authority dashboard)
     */
    @GetMapping("/assigned")
    public ResponseEntity<ApiResponse<List<CollectionRoute>>> getAllAssignedRoutes() {
        try {
            List<CollectionRoute> routes = routeService.getAssignedRoutes();
            return ResponseEntity.ok(ApiResponse.success("Assigned routes retrieved successfully", routes));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to retrieve assigned routes: " + e.getMessage()));
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
            
            String truckId = (String) requestData.get("truckId");
            String driverId = (String) requestData.get("driverId");
            List<String> staffIds = new ArrayList<>();
            
            
            Object staffIdsObj = requestData.get("staffIds");
            if (staffIdsObj instanceof List) {
                staffIds = (List<String>) staffIdsObj;
            } else if (staffIdsObj instanceof String) {
                staffIds.add((String) staffIdsObj);
            }
            
        
            List<SmartBin> allBins = routeOptimizationService.getSmartBinRepository().findAll()
                .stream()
                .filter(bin -> "FULL".equalsIgnoreCase(bin.getStatus()))
                .collect(Collectors.toList());
            
            if (allBins.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("No bins need collection at this time"));
            }
            
           
            GPSLocation depot = new GPSLocation(40.7128, -74.0060); 
            
            
            List<List<SmartBin>> routeGroups = groupBinsByProximity(allBins, depot);
            
            
            List<List<SmartBin>> validRouteGroups = routeGroups.stream()
                .filter(group -> group.size() >= 2)
                .collect(Collectors.toList());
            
           
            if (validRouteGroups.isEmpty()) {
                validRouteGroups.add(allBins);
            }
            
            
            if (routeIndex < 0 || routeIndex >= validRouteGroups.size()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid route index. Available routes: 0 to " + (validRouteGroups.size() - 1)));
            }
            
           
            List<SmartBin> bins = validRouteGroups.get(routeIndex);
            
            
            CollectionRoute route = routeOptimizationService.createOptimizedRoute(bins, depot);
            
            
            routeService.assignSpecificResourcesToRoute(route, truckId, driverId, staffIds);
            
            return ResponseEntity.ok(ApiResponse.success("Route created successfully", route));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to create route: " + e.getMessage()));
        }
    }
    
    /**
     * Get route stops by stop IDs
     */
    @PostMapping("/stops/by-ids")
    public ResponseEntity<ApiResponse<List<RouteStop>>> getStopsByIds(@RequestBody List<String> stopIds) {
        try {
            List<RouteStop> stops = routeOptimizationService.getRouteStopRepository().findByStopIdIn(stopIds);
            return ResponseEntity.ok(ApiResponse.success("Route stops retrieved successfully", stops));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to retrieve route stops: " + e.getMessage()));
        }
    }
    
    /**
     * Helper method to extract user ID from authentication
     */
    private String getUserIdFromAuthentication(Authentication authentication) {
        
        Object details = authentication.getDetails();
        if (details instanceof org.springframework.security.web.authentication.WebAuthenticationDetails) {
            
            return extractUserIdFromJwt();
        }
        
        return authentication.getName();
    }
    
    
    private String extractUserIdFromJwt() {
      
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            String token = extractJwtFromRequest(request);
            if (token != null) {
                return jwtUtil.getUserIdFromToken(token);
            }
        }
        return null;
    }
    
    
    private HttpServletRequest getCurrentRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        return null;
    }
    
   
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    
    
    private List<Truck> getAllTrucks() {
        return routeOptimizationService.getTruckRepository().findAll();
    }
    
    private List<Driver> getAllDrivers() {
        return routeOptimizationService.getDriverRepository().findAll();
    }
    
    private List<WasteCollectionStaff> getAllStaff() {
        return routeOptimizationService.getWasteCollectionStaffRepository().findAll();
    }
    
    private List<Truck> getAvailableTrucks() {
        return routeOptimizationService.getTruckRepository()
            .findByCurrentStatusAndAssignedDriverIdIsNull("AVAILABLE");
    }
    
    private List<Driver> getAvailableDrivers() {
        return routeOptimizationService.getDriverRepository()
            .findByAvailabilityTrueAndCurrentRouteIdIsNull();
    }
    
    private List<WasteCollectionStaff> getAvailableStaff() {
        return routeOptimizationService.getWasteCollectionStaffRepository()
            .findByAvailabilityTrueAndCurrentRouteIdIsNull();
    }
    
    private String findSuitableTruck(List<Truck> trucks, double requiredCapacity) {
        return trucks.stream()
            .filter(truck -> truck.getCapacity() >= requiredCapacity && 
                           ("AVAILABLE".equals(truck.getCurrentStatus()) || 
                            "NOT_IN_USE".equals(truck.getCurrentStatus())) &&
                           truck.getAssignedDriverId() == null)
            .findFirst()
            .map(Truck::getTruckId)
            .orElse(null);
    }
    
    private String findSuitableDriver(List<Driver> drivers) {
        return drivers.stream()
            .filter(driver -> driver.isAvailability() && driver.getCurrentRouteId() == null)
            .findFirst()
            .map(Driver::getUserId)  
            .orElse(null);
    }
    
    private List<String> findSuitableStaff(List<WasteCollectionStaff> staff, int maxCount) {
        return staff.stream()
            .filter(member -> member.isAvailability() && member.getCurrentRouteId() == null)
            .limit(maxCount)
            .map(WasteCollectionStaff::getUserId)  
            .collect(Collectors.toList());
    }
}