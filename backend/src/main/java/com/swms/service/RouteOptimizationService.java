package com.swms.service;

import com.swms.model.*;
import com.swms.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RouteOptimizationService {
    
    @Autowired
    private DummySmartBinRepository smartBinRepository;
    
    @Autowired
    private TruckRepository truckRepository;
    
    @Autowired
    private DriverRepository driverRepository;
    
    @Autowired
    private WasteCollectionStaffRepository wasteCollectionStaffRepository;
    
    @Autowired
    private CollectionRouteRepository collectionRouteRepository;
    
    @Autowired
    private RouteStopRepository routeStopRepository;
    
    private static final double EARTH_RADIUS = 6371; // Earth radius in kilometers
    
    // Expose the repositories for access in the controller
    public DummySmartBinRepository getDummySmartBinRepository() {
        return smartBinRepository;
    }
    
    public TruckRepository getTruckRepository() {
        return truckRepository;
    }
    
    public DriverRepository getDriverRepository() {
        return driverRepository;
    }
    
    public WasteCollectionStaffRepository getWasteCollectionStaffRepository() {
        return wasteCollectionStaffRepository;
    }
    
    public RouteStopRepository getRouteStopRepository() {
        return routeStopRepository;
    }
    
    /**
     * Fetches bins that need collection (status = 'full')
     * @return List of SmartBin entities that are full (>80% filled)
     */
    public List<DummySmartBin> getBinsNeedingCollection() {
        // In a real implementation, this would call the Smart Bin API
        // For now, we'll query the repository directly
        return smartBinRepository.findByStatus("full");
    }
    
    /**
     * Get all trucks in the system
     * @return List of all Truck entities
     */
    public List<Truck> getAllTrucks() {
        return truckRepository.findAll();
    }
    
    /**
     * Get all drivers in the system
     * @return List of all Driver entities
     */
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }
    
    /**
     * Get all waste collection staff in the system
     * @return List of all WasteCollectionStaff entities
     */
    public List<WasteCollectionStaff> getAllStaff() {
        return wasteCollectionStaffRepository.findAll();
    }
    
    /**
     * Checks which trucks are available (status = 'AVAILABLE' and no driver assigned)
     * @return List of available Truck entities
     */
    public List<Truck> checkTruckAvailability() {
        return truckRepository.findByCurrentStatusAndAssignedDriverIdIsNull("AVAILABLE");
    }
    
    /**
     * Checks which drivers and staff are available
     * @return Map containing lists of available drivers and staff
     */
    public Map<String, List<?>> checkStaffAvailability() {
        List<Driver> availableDrivers = driverRepository.findByAvailabilityTrueAndCurrentRouteIdIsNull();
        List<WasteCollectionStaff> availableStaff = wasteCollectionStaffRepository.findByAvailabilityTrueAndCurrentRouteIdIsNull();
        
        Map<String, List<?>> availabilityMap = new HashMap<>();
        availabilityMap.put("drivers", availableDrivers);
        availabilityMap.put("staff", availableStaff);
        
        return availabilityMap;
    }
    
    /**
     * Implements the Nearest Neighbor Algorithm to optimize route
     * @param bins List of SmartBin entities to visit
     * @param depot Starting location (usually the waste management facility)
     * @return Optimized list of route stops
     */
    public List<RouteStop> calculateNearestNeighborRoute(List<DummySmartBin> bins, GPSLocation depot) {
        List<RouteStop> routeStops = new ArrayList<>();
        
        if (bins.isEmpty()) {
            return routeStops;
        }
        
        // Create a copy of bins to track unvisited bins
        List<DummySmartBin> unvisitedBins = new ArrayList<>(bins);
        GPSLocation currentLocation = depot;
        
        // Continue until all bins are visited
        while (!unvisitedBins.isEmpty()) {
            DummySmartBin nearestBin = null;
            double minDistance = Double.MAX_VALUE;
            
            // Find the nearest unvisited bin
            for (DummySmartBin bin : unvisitedBins) {
                double distance = calculateDistance(currentLocation, bin.getCoordinates());
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestBin = bin;
                }
            }
            
            // Add the nearest bin to the route
            if (nearestBin != null) {
                RouteStop stop = new RouteStop();
                stop.setBinId(nearestBin.getBinId());
                stop.setBinLocation(nearestBin.getLocation());
                stop.setCoordinates(nearestBin.getCoordinates());
                stop.setSequenceOrder(routeStops.size() + 1);
                stop.setStatus("PENDING");
                stop.setCreatedAt(LocalDateTime.now());
                stop.setUpdatedAt(LocalDateTime.now());
                
                routeStops.add(stop);
                unvisitedBins.remove(nearestBin);
                currentLocation = nearestBin.getCoordinates();
            }
        }
        
        return routeStops;
    }
    
    /**
     * Calculates distance between two GPS points using Haversine formula
     * @param point1 First GPS location
     * @param point2 Second GPS location
     * @return Distance in kilometers
     */
    public double calculateDistance(GPSLocation point1, GPSLocation point2) {
        double lat1 = Math.toRadians(point1.getLatitude());
        double lon1 = Math.toRadians(point1.getLongitude());
        double lat2 = Math.toRadians(point2.getLatitude());
        double lon2 = Math.toRadians(point2.getLongitude());
        
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;
        
        double a = Math.pow(Math.sin(dLat / 2), 2) +
                   Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dLon / 2), 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS * c;
    }
    
    /**
     * Calculates total distance and estimated time for a route
     * @param route List of RouteStop entities
     * @param depot Starting location
     * @return Map containing total distance and estimated time
     */
    public Map<String, Object> calculateTotalRouteMetrics(List<RouteStop> route, GPSLocation depot) {
        double totalDistance = 0.0;
        int estimatedTime = 0; // in minutes
        
        if (route.isEmpty()) {
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("totalDistance", totalDistance);
            metrics.put("estimatedTime", estimatedTime);
            return metrics;
        }
        
        GPSLocation currentLocation = depot;
        
        // Calculate distance between each consecutive stop
        for (RouteStop stop : route) {
            double distance = calculateDistance(currentLocation, stop.getCoordinates());
            totalDistance += distance;
            currentLocation = stop.getCoordinates();
        }
        
        // Add distance back to depot
        double returnDistance = calculateDistance(currentLocation, depot);
        totalDistance += returnDistance;
        
        // Estimate time (assuming 30 km/h average speed and 5 minutes per stop)
        estimatedTime = (int) (totalDistance / 30 * 60) + (route.size() * 5);
        
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalDistance", totalDistance);
        metrics.put("estimatedTime", estimatedTime);
        return metrics;
    }
    
    /**
     * Creates a new collection route with optimized stops
     * @param bins List of SmartBin entities to collect
     * @param depot Starting location
     * @return Created CollectionRoute entity
     */
    public CollectionRoute createOptimizedRoute(List<DummySmartBin> bins, GPSLocation depot) {
        // Calculate optimized route using Nearest Neighbor Algorithm
        List<RouteStop> routeStops = calculateNearestNeighborRoute(bins, depot);
        
        // Save route stops to database
        List<RouteStop> savedStops = routeStopRepository.saveAll(routeStops);
        List<String> stopIds = savedStops.stream()
                .map(RouteStop::getStopId)
                .collect(Collectors.toList());
        
        // Calculate route metrics
        Map<String, Object> metrics = calculateTotalRouteMetrics(routeStops, depot);
        
        // Create collection route
        CollectionRoute route = new CollectionRoute();
        route.setDate(LocalDateTime.now());
        route.setStatus("PLANNED");
        route.setStopIds(stopIds);
        route.setTotalDistance((Double) metrics.get("totalDistance"));
        route.setEstimatedTime((Integer) metrics.get("estimatedTime"));
        route.setCreatedAt(LocalDateTime.now());
        route.setUpdatedAt(LocalDateTime.now());
        
        return collectionRouteRepository.save(route);
    }
}