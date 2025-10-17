package com.swms.model.jayathu;

import com.swms.model.GPSLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SmartBinCityAuthTest {

    private SmartBinCityAuth smartBin;

    @BeforeEach
    void setUp() {
        smartBin = new SmartBinCityAuth();
    }

    @Test
    void testDefaultConstructor() {
        // Act
        SmartBinCityAuth bin = new SmartBinCityAuth();

        // Assert
        assertNotNull(bin);
    }

    @Test
    void testParameterizedConstructor() {
        // Arrange
        String binId = "BIN001";
        String location = "Downtown";
        GPSLocation coordinates = new GPSLocation(12.34, 56.78);
        Double currentLevel = 50.0;
        Double capacity = 100.0;
        String status = "ACTIVE";
        LocalDateTime lastCollected = LocalDateTime.now().minusDays(1);
        LocalDateTime createdAt = LocalDateTime.now().minusDays(2);
        LocalDateTime lastUpdated = LocalDateTime.now().minusHours(1);
        String wasteType = "ORGANIC";

        // Act
        SmartBinCityAuth bin = new SmartBinCityAuth(binId, location, coordinates, currentLevel, capacity, 
                status, lastCollected, createdAt, lastUpdated, wasteType);

        // Assert
        assertNotNull(bin);
        assertEquals(binId, bin.getBinId());
        assertEquals(location, bin.getLocation());
        assertEquals(coordinates, bin.getCoordinates());
        assertEquals(currentLevel, bin.getCurrentLevel());
        assertEquals(capacity, bin.getCapacity());
        assertEquals(status, bin.getStatus());
        assertEquals(lastCollected, bin.getLastCollected());
        assertEquals(createdAt, bin.getCreatedAt());
        assertEquals(lastUpdated, bin.getLastUpdated());
        assertEquals(wasteType, bin.getWasteType());
    }

    @Test
    void testGettersAndSetters() {
        // Arrange
        String binId = "BIN001";
        String location = "Downtown";
        GPSLocation coordinates = new GPSLocation(12.34, 56.78);
        Double currentLevel = 50.0;
        Double capacity = 100.0;
        String status = "ACTIVE";
        LocalDateTime lastCollected = LocalDateTime.now().minusDays(1);
        LocalDateTime createdAt = LocalDateTime.now().minusDays(2);
        LocalDateTime lastUpdated = LocalDateTime.now().minusHours(1);
        String wasteType = "ORGANIC";

        // Act
        smartBin.setBinId(binId);
        smartBin.setLocation(location);
        smartBin.setCoordinates(coordinates);
        smartBin.setCurrentLevel(currentLevel);
        smartBin.setCapacity(capacity);
        smartBin.setStatus(status);
        smartBin.setLastCollected(lastCollected);
        smartBin.setCreatedAt(createdAt);
        smartBin.setLastUpdated(lastUpdated);
        smartBin.setWasteType(wasteType);

        // Assert
        assertEquals(binId, smartBin.getBinId());
        assertEquals(location, smartBin.getLocation());
        assertEquals(coordinates, smartBin.getCoordinates());
        assertEquals(currentLevel, smartBin.getCurrentLevel());
        assertEquals(capacity, smartBin.getCapacity());
        assertEquals(status, smartBin.getStatus());
        assertEquals(lastCollected, smartBin.getLastCollected());
        assertEquals(createdAt, smartBin.getCreatedAt());
        assertEquals(lastUpdated, smartBin.getLastUpdated());
        assertEquals(wasteType, smartBin.getWasteType());
    }

    @Test
    void testIdGetter() {
        // Arrange
        String binId = "BIN001";
        smartBin.setBinId(binId);

        // Act
        String id = smartBin.getId();

        // Assert
        assertEquals(binId, id);
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        SmartBinCityAuth bin1 = new SmartBinCityAuth();
        bin1.setBinId("BIN001");
        
        SmartBinCityAuth bin2 = new SmartBinCityAuth();
        bin2.setBinId("BIN001");
        
        SmartBinCityAuth bin3 = new SmartBinCityAuth();
        bin3.setBinId("BIN002");

        // Assert
        assertEquals(bin1, bin2);
        assertNotEquals(bin1, bin3);
        assertEquals(bin1.hashCode(), bin2.hashCode());
        assertNotEquals(bin1.hashCode(), bin3.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        smartBin.setBinId("BIN001");
        smartBin.setLocation("Downtown");

        // Act
        String toStringResult = smartBin.toString();

        // Assert
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("BIN001"));
        assertTrue(toStringResult.contains("Downtown"));
    }

    // Edge case tests
    @Test
    void testNullValues() {
        // Act
        smartBin.setBinId(null);
        smartBin.setLocation(null);
        smartBin.setCoordinates(null);
        smartBin.setCurrentLevel(null);
        smartBin.setCapacity(null);
        smartBin.setStatus(null);
        smartBin.setLastCollected(null);
        smartBin.setCreatedAt(null);
        smartBin.setLastUpdated(null);
        smartBin.setWasteType(null);

        // Assert
        assertNull(smartBin.getBinId());
        assertNull(smartBin.getLocation());
        assertNull(smartBin.getCoordinates());
        assertNull(smartBin.getCurrentLevel());
        assertNull(smartBin.getCapacity());
        assertNull(smartBin.getStatus());
        assertNull(smartBin.getLastCollected());
        assertNull(smartBin.getCreatedAt());
        assertNull(smartBin.getLastUpdated());
        assertNull(smartBin.getWasteType());
        assertNull(smartBin.getId());
    }

    @Test
    void testZeroValues() {
        // Act
        smartBin.setCurrentLevel(0.0);
        smartBin.setCapacity(0.0);

        // Assert
        assertEquals(0.0, smartBin.getCurrentLevel());
        assertEquals(0.0, smartBin.getCapacity());
    }

    @Test
    void testNegativeValues() {
        // Act
        smartBin.setCurrentLevel(-10.0);
        smartBin.setCapacity(-50.0);

        // Assert
        assertEquals(-10.0, smartBin.getCurrentLevel());
        assertEquals(-50.0, smartBin.getCapacity());
    }
}