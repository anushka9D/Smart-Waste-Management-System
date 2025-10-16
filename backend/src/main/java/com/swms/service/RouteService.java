package com.swms.service;

import com.swms.model.*;
import com.swms.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RouteService {
    
    @Autowired
    private CollectionRouteRepository collectionRouteRepository;
    
    @Autowired
    private RouteStopRepository routeStopRepository;
    
    @Autowired
    private DriverRepository driverRepository;
    
    @Autowired
    private WasteCollectionStaffRepository wasteCollectionStaffRepository;
    
    @Autowired
    private TruckRepository truckRepository;
    
    /**
     * Assigns available truck, driver, and staff to a route
     * @param route The route to assign resources to
     */
    public void assignResourcesToRoute(CollectionRoute route) {
        // Find available truck
        List<Truck> availableTrucks = truckRepository.findByCurrentStatusAndAssignedDriverIdIsNull("AVAILABLE");
        if (!availableTrucks.isEmpty()) {
            Truck truck = availableTrucks.get(0);
            route.setAssignedTruckId(truck.getTruckId());
            truck.setCurrentStatus("IN_USE");
            truckRepository.save(truck);
        }
        
        // Find available driver
        List<Driver> availableDrivers = driverRepository.findByAvailabilityTrueAndCurrentRouteIdIsNull();
        if (!availableDrivers.isEmpty()) {
            Driver driver = availableDrivers.get(0);
            route.setAssignedDriverId(driver.getUserId());
            driver.setAvailability(false);
            driver.setCurrentRouteId(route.getRouteId());
            driverRepository.save(driver);
        }
        
        // Find available staff (assign up to 2 staff members)
        List<WasteCollectionStaff> availableStaff = wasteCollectionStaffRepository.findByAvailabilityTrueAndCurrentRouteIdIsNull();
        // Implementation would assign staff as needed
    }
    
    /**
     * Assigns specific truck, driver, and staff to a route
     * @param route The route to assign resources to
     * @param truckId The truck ID to assign
     * @param driverId The driver ID to assign
     * @param staffIds The staff IDs to assign
     */
    public void assignSpecificResourcesToRoute(CollectionRoute route, String truckId, String driverId, List<String> staffIds) {
        // Assign truck
        if (truckId != null) {
            Optional<Truck> truckOpt = truckRepository.findById(truckId);
            if (truckOpt.isPresent()) {
                Truck truck = truckOpt.get();
                route.setAssignedTruckId(truck.getTruckId());
                truck.setCurrentStatus("IN_USE");
                truck.setAssignedDriverId(driverId); // Link truck to driver
                truckRepository.save(truck);
            }
        }
        
        // Assign driver
        if (driverId != null) {
            Optional<Driver> driverOpt = driverRepository.findById(driverId);
            if (driverOpt.isPresent()) {
                Driver driver = driverOpt.get();
                route.setAssignedDriverId(driver.getUserId());
                driver.setAvailability(false);
                driver.setCurrentRouteId(route.getRouteId());
                driverRepository.save(driver);
            }
        }
        
        // Assign staff
        if (staffIds != null && !staffIds.isEmpty()) {
            route.setAssignedStaffIds(staffIds);
            
            // Update staff availability
            for (String staffId : staffIds) {
                Optional<WasteCollectionStaff> staffOpt = wasteCollectionStaffRepository.findById(staffId);
                if (staffOpt.isPresent()) {
                    WasteCollectionStaff staff = staffOpt.get();
                    staff.setAvailability(false);
                    staff.setCurrentRouteId(route.getRouteId());
                    wasteCollectionStaffRepository.save(staff);
                }
            }
        }
        
        // Update route status
        route.setStatus("ASSIGNED");
        route.setUpdatedAt(LocalDateTime.now());
        collectionRouteRepository.save(route);
    }
    
    /**
     * Get all assigned routes
     */
    public List<CollectionRoute> getAssignedRoutes() {
        return collectionRouteRepository.findByStatus("ASSIGNED");
    }
    
    /**
     * Get routes assigned to a specific driver
     * @param driverId The driver ID
     * @return List of CollectionRoute entities
     */
    public List<CollectionRoute> getRoutesByDriverId(String driverId) {
        return collectionRouteRepository.findByAssignedDriverId(driverId);
    }
    
    /**
     * Get routes assigned to a specific staff member
     * @param staffId The staff ID
     * @return List of CollectionRoute entities
     */
    public List<CollectionRoute> getRoutesByStaffId(String staffId) {
        return collectionRouteRepository.findByAssignedStaffIdsContaining(staffId);
    }
    
    /**
     * Update the status of a route
     * @param routeId The route ID
     * @param status The new status
     * @return Success message
     */
    public String updateRouteStatus(String routeId, String status) {
        Optional<CollectionRoute> routeOpt = collectionRouteRepository.findById(routeId);
        
        if (routeOpt.isPresent()) {
            CollectionRoute route = routeOpt.get();
            route.setStatus(status);
            route.setUpdatedAt(LocalDateTime.now());
            collectionRouteRepository.save(route);
            return "Route status updated successfully";
        }
        
        throw new RuntimeException("Route not found with ID: " + routeId);
    }
    
    /**
     * Mark a route stop as completed
     * @param stopId The stop ID
     * @return Success message
     */
    public String completeRouteStop(String stopId) {
        Optional<RouteStop> stopOpt = routeStopRepository.findById(stopId);
        
        if (stopOpt.isPresent()) {
            RouteStop stop = stopOpt.get();
            stop.setStatus("COMPLETED");
            stop.setCollectionTime(LocalDateTime.now());
            stop.setUpdatedAt(LocalDateTime.now());
            routeStopRepository.save(stop);
            
            // Check if all stops in the route are completed and update route status if needed
            checkAndCompleteRouteIfAllStopsCompleted(stop);
            
            return "Route stop marked as completed";
        }
        
        throw new RuntimeException("Route stop not found with ID: " + stopId);
    }
    
    /**
     * Check if all stops in a route are completed and update route status to COMPLETED if so
     * @param completedStop The recently completed stop
     */
    private void checkAndCompleteRouteIfAllStopsCompleted(RouteStop completedStop) {
        // Find the route that contains this stop
        // This is a simplified approach - in a real implementation, you might want to store
        // the route ID in each stop for easier lookup
        List<CollectionRoute> allRoutes = collectionRouteRepository.findAll();
        
        for (CollectionRoute route : allRoutes) {
            // Check if this route contains the completed stop
            if (route.getStopIds() != null && route.getStopIds().contains(completedStop.getStopId())) {
                // Get all stops for this route
                List<RouteStop> routeStops = routeStopRepository.findByStopIdIn(route.getStopIds());
                
                // Check if all stops are completed
                boolean allStopsCompleted = routeStops.stream()
                    .allMatch(stop -> "COMPLETED".equals(stop.getStatus()));
                
                // If all stops are completed and route is not already COMPLETED, update route status
                if (allStopsCompleted && !"COMPLETED".equals(route.getStatus())) {
                    route.setStatus("COMPLETED");
                    route.setUpdatedAt(LocalDateTime.now());
                    collectionRouteRepository.save(route);
                    break; // Found and updated the route, no need to check others
                }
            }
        }
    }
    
    /**
     * Get routes for a specific date range
     * @param start Start date
     * @param end End date
     * @return List of CollectionRoute entities
     */
    public List<CollectionRoute> getRoutesByDate(LocalDateTime start, LocalDateTime end) {
        return collectionRouteRepository.findByDateBetween(start, end);
    }
}