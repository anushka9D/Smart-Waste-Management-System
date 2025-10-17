package com.swms.service;

import com.swms.model.*;
import com.swms.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RouteServiceTest {

    @Mock
    private CollectionRouteRepository collectionRouteRepository;

    @Mock
    private RouteStopRepository routeStopRepository;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private WasteCollectionStaffRepository wasteCollectionStaffRepository;

    @Mock
    private TruckRepository truckRepository;

    @InjectMocks
    private RouteService routeService;

    private CollectionRoute testRoute;
    private Driver testDriver;
    private WasteCollectionStaff testStaff;
    private Truck testTruck;
    private RouteStop testStop;

    @BeforeEach
    void setUp() {
        // Initialize test data
        testRoute = new CollectionRoute();
        testRoute.setRouteId("route1");
        testRoute.setStatus("PLANNED");
        testRoute.setAssignedTruckId(null);
        testRoute.setAssignedDriverId(null);
        testRoute.setAssignedStaffIds(null); // Initialize as null
        testRoute.setStopIds(Arrays.asList("stop1", "stop2"));

        testDriver = new Driver();
        testDriver.setUserId("driver1");
        testDriver.setAvailability(true);
        testDriver.setCurrentRouteId(null);

        testStaff = new WasteCollectionStaff();
        testStaff.setUserId("staff1");
        testStaff.setAvailability(true);
        testStaff.setCurrentRouteId(null);

        testTruck = new Truck();
        testTruck.setTruckId("truck1");
        testTruck.setCurrentStatus("AVAILABLE");
        testTruck.setAssignedDriverId(null);

        testStop = new RouteStop();
        testStop.setStopId("stop1");
        testStop.setStatus("PENDING");
    }

    // Test assignResourcesToRoute method
    @Test
    void testAssignResourcesToRoute_Positive() {
        // Arrange
        List<Truck> availableTrucks = Arrays.asList(testTruck);
        List<Driver> availableDrivers = Arrays.asList(testDriver);
        List<WasteCollectionStaff> availableStaff = Arrays.asList(testStaff);

        when(truckRepository.findByCurrentStatusAndAssignedDriverIdIsNull("AVAILABLE"))
                .thenReturn(availableTrucks);
        when(driverRepository.findByAvailabilityTrueAndCurrentRouteIdIsNull())
                .thenReturn(availableDrivers);
        when(wasteCollectionStaffRepository.findByAvailabilityTrueAndCurrentRouteIdIsNull())
                .thenReturn(availableStaff);

        // Act
        routeService.assignResourcesToRoute(testRoute);

        // Assert
        assertEquals("truck1", testRoute.getAssignedTruckId());
        assertEquals("driver1", testRoute.getAssignedDriverId());
        // assignedStaffIds is not set in this method, so it should remain null
        assertNull(testRoute.getAssignedStaffIds());
        // The method doesn't explicitly set updatedAt, so we shouldn't assert it's not null

        verify(truckRepository).save(testTruck);
        verify(driverRepository).save(testDriver);
        assertEquals("IN_USE", testTruck.getCurrentStatus());
        assertFalse(testDriver.isAvailability());
        assertEquals("route1", testDriver.getCurrentRouteId());
    }

    @Test
    void testAssignResourcesToRoute_NoAvailableTruck() {
        // Arrange
        List<Truck> availableTrucks = new ArrayList<>();
        List<Driver> availableDrivers = Arrays.asList(testDriver);
        List<WasteCollectionStaff> availableStaff = Arrays.asList(testStaff);

        when(truckRepository.findByCurrentStatusAndAssignedDriverIdIsNull("AVAILABLE"))
                .thenReturn(availableTrucks);
        when(driverRepository.findByAvailabilityTrueAndCurrentRouteIdIsNull())
                .thenReturn(availableDrivers);
        when(wasteCollectionStaffRepository.findByAvailabilityTrueAndCurrentRouteIdIsNull())
                .thenReturn(availableStaff);

        // Act
        routeService.assignResourcesToRoute(testRoute);

        // Assert
        assertNull(testRoute.getAssignedTruckId());
        assertEquals("driver1", testRoute.getAssignedDriverId());
        assertNull(testRoute.getAssignedStaffIds());

        verify(truckRepository, never()).save(any());
        verify(driverRepository).save(testDriver);
    }

    @Test
    void testAssignResourcesToRoute_NoAvailableDriver() {
        // Arrange
        List<Truck> availableTrucks = Arrays.asList(testTruck);
        List<Driver> availableDrivers = new ArrayList<>();
        List<WasteCollectionStaff> availableStaff = Arrays.asList(testStaff);

        when(truckRepository.findByCurrentStatusAndAssignedDriverIdIsNull("AVAILABLE"))
                .thenReturn(availableTrucks);
        when(driverRepository.findByAvailabilityTrueAndCurrentRouteIdIsNull())
                .thenReturn(availableDrivers);
        when(wasteCollectionStaffRepository.findByAvailabilityTrueAndCurrentRouteIdIsNull())
                .thenReturn(availableStaff);

        // Act
        routeService.assignResourcesToRoute(testRoute);

        // Assert
        assertEquals("truck1", testRoute.getAssignedTruckId());
        assertNull(testRoute.getAssignedDriverId());
        assertNull(testRoute.getAssignedStaffIds());

        verify(truckRepository).save(testTruck);
        verify(driverRepository, never()).save(any());
    }

    // Test assignSpecificResourcesToRoute method
    @Test
    void testAssignSpecificResourcesToRoute_Positive() {
        // Arrange
        List<String> staffIds = Arrays.asList("staff1", "staff2");

        when(truckRepository.findById("truck1")).thenReturn(Optional.of(testTruck));
        when(driverRepository.findById("driver1")).thenReturn(Optional.of(testDriver));
        when(wasteCollectionStaffRepository.findById("staff1")).thenReturn(Optional.of(testStaff));

        WasteCollectionStaff testStaff2 = new WasteCollectionStaff();
        testStaff2.setUserId("staff2");
        testStaff2.setAvailability(true);
        testStaff2.setCurrentRouteId(null);
        when(wasteCollectionStaffRepository.findById("staff2")).thenReturn(Optional.of(testStaff2));

        // Act
        routeService.assignSpecificResourcesToRoute(testRoute, "truck1", "driver1", staffIds);

        // Assert
        assertEquals("truck1", testRoute.getAssignedTruckId());
        assertEquals("driver1", testRoute.getAssignedDriverId());
        assertEquals(staffIds, testRoute.getAssignedStaffIds());
        assertEquals("ASSIGNED", testRoute.getStatus());
        assertNotNull(testRoute.getUpdatedAt());

        verify(truckRepository).save(testTruck);
        verify(driverRepository).save(testDriver);
        verify(wasteCollectionStaffRepository, times(2)).save(any(WasteCollectionStaff.class));
        assertEquals("IN_USE", testTruck.getCurrentStatus());
        assertFalse(testDriver.isAvailability());
        assertFalse(testStaff.isAvailability());
    }

    @Test
    void testAssignSpecificResourcesToRoute_InvalidTruckId() {
        // Arrange
        List<String> staffIds = Arrays.asList("staff1");

        when(truckRepository.findById("invalidTruck")).thenReturn(Optional.empty());
        when(driverRepository.findById("driver1")).thenReturn(Optional.of(testDriver));
        when(wasteCollectionStaffRepository.findById("staff1")).thenReturn(Optional.of(testStaff));

        // Act
        routeService.assignSpecificResourcesToRoute(testRoute, "invalidTruck", "driver1", staffIds);

        // Assert
        assertNull(testRoute.getAssignedTruckId());
        assertEquals("driver1", testRoute.getAssignedDriverId());
        assertEquals(staffIds, testRoute.getAssignedStaffIds());
    }

    @Test
    void testAssignSpecificResourcesToRoute_InvalidDriverId() {
        // Arrange
        List<String> staffIds = Arrays.asList("staff1");

        when(truckRepository.findById("truck1")).thenReturn(Optional.of(testTruck));
        when(driverRepository.findById("invalidDriver")).thenReturn(Optional.empty());
        when(wasteCollectionStaffRepository.findById("staff1")).thenReturn(Optional.of(testStaff));

        // Act
        routeService.assignSpecificResourcesToRoute(testRoute, "truck1", "invalidDriver", staffIds);

        // Assert
        assertEquals("truck1", testRoute.getAssignedTruckId());
        assertNull(testRoute.getAssignedDriverId());
        assertEquals(staffIds, testRoute.getAssignedStaffIds());
    }

    @Test
    void testAssignSpecificResourcesToRoute_NullParameters() {
        // Act
        routeService.assignSpecificResourcesToRoute(testRoute, null, null, null);

        // Assert
        assertNull(testRoute.getAssignedTruckId());
        assertNull(testRoute.getAssignedDriverId());
        // When staffIds is null, assignedStaffIds should remain null
        assertNull(testRoute.getAssignedStaffIds());
        assertEquals("ASSIGNED", testRoute.getStatus());
    }

    // Test getAssignedRoutes method
    @Test
    void testGetAssignedRoutes_Positive() {
        // Arrange
        CollectionRoute assignedRoute = new CollectionRoute();
        assignedRoute.setStatus("ASSIGNED");
        List<CollectionRoute> assignedRoutes = Arrays.asList(assignedRoute);

        when(collectionRouteRepository.findByStatus("ASSIGNED")).thenReturn(assignedRoutes);

        // Act
        List<CollectionRoute> result = routeService.getAssignedRoutes();

        // Assert
        assertEquals(1, result.size());
        assertEquals("ASSIGNED", result.get(0).getStatus());
        verify(collectionRouteRepository).findByStatus("ASSIGNED");
    }

    // Test getRoutesByDriverId method
    @Test
    void testGetRoutesByDriverId_Positive() {
        // Arrange
        CollectionRoute driverRoute = new CollectionRoute();
        driverRoute.setAssignedDriverId("driver1");
        List<CollectionRoute> driverRoutes = Arrays.asList(driverRoute);

        when(collectionRouteRepository.findByAssignedDriverId("driver1")).thenReturn(driverRoutes);

        // Act
        List<CollectionRoute> result = routeService.getRoutesByDriverId("driver1");

        // Assert
        assertEquals(1, result.size());
        assertEquals("driver1", result.get(0).getAssignedDriverId());
        verify(collectionRouteRepository).findByAssignedDriverId("driver1");
    }

    // Test getRoutesByStaffId method
    @Test
    void testGetRoutesByStaffId_Positive() {
        // Arrange
        CollectionRoute staffRoute = new CollectionRoute();
        staffRoute.setAssignedStaffIds(Arrays.asList("staff1", "staff2"));
        List<CollectionRoute> staffRoutes = Arrays.asList(staffRoute);

        when(collectionRouteRepository.findByAssignedStaffIdsContaining("staff1")).thenReturn(staffRoutes);

        // Act
        List<CollectionRoute> result = routeService.getRoutesByStaffId("staff1");

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get(0).getAssignedStaffIds().contains("staff1"));
        verify(collectionRouteRepository).findByAssignedStaffIdsContaining("staff1");
    }

    // Test updateRouteStatus method
    @Test
    void testUpdateRouteStatus_Positive() {
        // Arrange
        testRoute.setStatus("IN_PROGRESS");
        when(collectionRouteRepository.findById("route1")).thenReturn(Optional.of(testRoute));

        // Act
        String result = routeService.updateRouteStatus("route1", "COMPLETED");

        // Assert
        assertEquals("Route status updated successfully", result);
        assertEquals("COMPLETED", testRoute.getStatus());
        assertNotNull(testRoute.getUpdatedAt());
        verify(collectionRouteRepository).findById("route1");
        verify(collectionRouteRepository).save(testRoute);
    }

    @Test
    void testUpdateRouteStatus_RouteNotFound() {
        // Arrange
        when(collectionRouteRepository.findById("invalidRoute")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            routeService.updateRouteStatus("invalidRoute", "COMPLETED");
        });

        assertEquals("Route not found with ID: invalidRoute", exception.getMessage());
        verify(collectionRouteRepository).findById("invalidRoute");
        verify(collectionRouteRepository, never()).save(any());
    }

    @Test
    void testUpdateRouteStatus_CompletedRoute_ResourceUpdate() {
        // Arrange
        testRoute.setAssignedDriverId("driver1");
        testRoute.setAssignedStaffIds(Arrays.asList("staff1"));
        testRoute.setAssignedTruckId("truck1");
        testRoute.setStatus("IN_PROGRESS");

        Driver assignedDriver = new Driver();
        assignedDriver.setUserId("driver1");
        assignedDriver.setAvailability(false);
        assignedDriver.setCurrentRouteId("route1");

        WasteCollectionStaff assignedStaff = new WasteCollectionStaff();
        assignedStaff.setUserId("staff1");
        assignedStaff.setAvailability(false);
        assignedStaff.setCurrentRouteId("route1");

        Truck assignedTruck = new Truck();
        assignedTruck.setTruckId("truck1");
        assignedTruck.setCurrentStatus("IN_USE");
        assignedTruck.setAssignedDriverId("driver1");

        when(collectionRouteRepository.findById("route1")).thenReturn(Optional.of(testRoute));
        when(driverRepository.findById("driver1")).thenReturn(Optional.of(assignedDriver));
        when(wasteCollectionStaffRepository.findById("staff1")).thenReturn(Optional.of(assignedStaff));
        when(truckRepository.findById("truck1")).thenReturn(Optional.of(assignedTruck));

        // Act
        String result = routeService.updateRouteStatus("route1", "COMPLETED");

        // Assert
        assertEquals("Route status updated successfully", result);
        assertEquals("COMPLETED", testRoute.getStatus());

        // Verify resource availability updates
        verify(driverRepository).save(assignedDriver);
        verify(wasteCollectionStaffRepository).save(assignedStaff);
        verify(truckRepository).save(assignedTruck);

        assertTrue(assignedDriver.isAvailability());
        assertNull(assignedDriver.getCurrentRouteId());
        assertTrue(assignedStaff.isAvailability());
        assertNull(assignedStaff.getCurrentRouteId());
        assertEquals("NOT_IN_USE", assignedTruck.getCurrentStatus());
        assertNull(assignedTruck.getAssignedDriverId());
    }

    // Test completeRouteStop method
    @Test
    void testCompleteRouteStop_Positive() {
        // Arrange
        when(routeStopRepository.findById("stop1")).thenReturn(Optional.of(testStop));

        // Act
        String result = routeService.completeRouteStop("stop1");

        // Assert
        assertEquals("Route stop marked as completed", result);
        assertEquals("COMPLETED", testStop.getStatus());
        assertNotNull(testStop.getCollectionTime());
        assertNotNull(testStop.getUpdatedAt());
        verify(routeStopRepository).findById("stop1");
        verify(routeStopRepository).save(testStop);
    }

    @Test
    void testCompleteRouteStop_StopNotFound() {
        // Arrange
        when(routeStopRepository.findById("invalidStop")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            routeService.completeRouteStop("invalidStop");
        });

        assertEquals("Route stop not found with ID: invalidStop", exception.getMessage());
        verify(routeStopRepository).findById("invalidStop");
        verify(routeStopRepository, never()).save(any());
    }

    // Test checkAndCompleteRouteIfAllStopsCompleted method
    @Test
    void testCheckAndCompleteRouteIfAllStopsCompleted_AllCompleted() {
        // Arrange
        testStop.setStatus("COMPLETED");
        
        CollectionRoute routeWithStops = new CollectionRoute();
        routeWithStops.setRouteId("route1");
        routeWithStops.setStatus("IN_PROGRESS");
        routeWithStops.setStopIds(Arrays.asList("stop1", "stop2"));

        RouteStop stop2 = new RouteStop();
        stop2.setStopId("stop2");
        stop2.setStatus("COMPLETED");

        List<CollectionRoute> allRoutes = Arrays.asList(routeWithStops);
        List<RouteStop> routeStops = Arrays.asList(testStop, stop2);

        when(routeStopRepository.findById("stop1")).thenReturn(Optional.of(testStop));
        when(collectionRouteRepository.findAll()).thenReturn(allRoutes);
        when(routeStopRepository.findByStopIdIn(Arrays.asList("stop1", "stop2"))).thenReturn(routeStops);

        // Act
        String result = routeService.completeRouteStop("stop1");

        // Assert
        assertEquals("Route stop marked as completed", result);
        verify(collectionRouteRepository).save(routeWithStops);
        assertEquals("COMPLETED", routeWithStops.getStatus());
    }

    @Test
    void testCheckAndCompleteRouteIfAllStopsCompleted_NotAllCompleted() {
        // Arrange
        testStop.setStatus("COMPLETED");
        
        CollectionRoute routeWithStops = new CollectionRoute();
        routeWithStops.setRouteId("route1");
        routeWithStops.setStatus("IN_PROGRESS");
        routeWithStops.setStopIds(Arrays.asList("stop1", "stop2"));

        RouteStop stop2 = new RouteStop();
        stop2.setStopId("stop2");
        stop2.setStatus("PENDING"); // Not completed

        List<CollectionRoute> allRoutes = Arrays.asList(routeWithStops);
        List<RouteStop> routeStops = Arrays.asList(testStop, stop2);

        when(routeStopRepository.findById("stop1")).thenReturn(Optional.of(testStop));
        when(collectionRouteRepository.findAll()).thenReturn(allRoutes);
        when(routeStopRepository.findByStopIdIn(Arrays.asList("stop1", "stop2"))).thenReturn(routeStops);

        // Act
        String result = routeService.completeRouteStop("stop1");

        // Assert
        assertEquals("Route stop marked as completed", result);
        verify(collectionRouteRepository, never()).save(any());
        assertEquals("IN_PROGRESS", routeWithStops.getStatus()); // Should not be changed
    }

    // Test getRoutesByDate method
    @Test
    void testGetRoutesByDate_Positive() {
        // Arrange
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 1, 31, 23, 59);
        
        CollectionRoute dateRoute = new CollectionRoute();
        dateRoute.setDate(LocalDateTime.of(2023, 1, 15, 10, 0));
        List<CollectionRoute> dateRoutes = Arrays.asList(dateRoute);

        when(collectionRouteRepository.findByDateBetween(start, end)).thenReturn(dateRoutes);

        // Act
        List<CollectionRoute> result = routeService.getRoutesByDate(start, end);

        // Assert
        assertEquals(1, result.size());
        assertEquals(dateRoute.getDate(), result.get(0).getDate());
        verify(collectionRouteRepository).findByDateBetween(start, end);
    }

    // Edge case tests
    @Test
    void testAssignSpecificResourcesToRoute_EmptyStaffList() {
        // Act
        routeService.assignSpecificResourcesToRoute(testRoute, "truck1", "driver1", new ArrayList<>());

        // Assert
        // When staffIds is an empty list, assignedStaffIds should remain null
        assertNull(testRoute.getAssignedStaffIds());
    }

    @Test
    void testUpdateRouteStatus_SameStatus() {
        // Arrange
        testRoute.setStatus("COMPLETED");
        when(collectionRouteRepository.findById("route1")).thenReturn(Optional.of(testRoute));

        // Act
        String result = routeService.updateRouteStatus("route1", "COMPLETED");

        // Assert
        assertEquals("Route status updated successfully", result);
        assertEquals("COMPLETED", testRoute.getStatus());
        // Should not trigger resource availability updates when status is already COMPLETED
        verify(driverRepository, never()).findById(anyString());
    }

    @Test
    void testCompleteRouteStop_ResourceNotFound() {
        // Arrange
        testRoute.setAssignedDriverId("invalidDriver");
        testRoute.setAssignedStaffIds(Arrays.asList("invalidStaff"));
        testRoute.setAssignedTruckId("invalidTruck");
        testRoute.setStatus("IN_PROGRESS");

        testStop.setStatus("COMPLETED");
        
        CollectionRoute routeWithStops = new CollectionRoute();
        routeWithStops.setRouteId("route1");
        routeWithStops.setStatus("IN_PROGRESS");
        routeWithStops.setAssignedDriverId("invalidDriver");
        routeWithStops.setAssignedStaffIds(Arrays.asList("invalidStaff"));
        routeWithStops.setAssignedTruckId("invalidTruck");
        routeWithStops.setStopIds(Arrays.asList("stop1"));

        List<CollectionRoute> allRoutes = Arrays.asList(routeWithStops);
        List<RouteStop> routeStops = Arrays.asList(testStop);

        when(routeStopRepository.findById("stop1")).thenReturn(Optional.of(testStop));
        when(collectionRouteRepository.findAll()).thenReturn(allRoutes);
        when(routeStopRepository.findByStopIdIn(Arrays.asList("stop1"))).thenReturn(routeStops);

        // Act
        String result = routeService.completeRouteStop("stop1");

        // Assert
        assertEquals("Route stop marked as completed", result);
        verify(collectionRouteRepository).save(routeWithStops);
        assertEquals("COMPLETED", routeWithStops.getStatus());
        // Should handle missing resources gracefully
    }
}