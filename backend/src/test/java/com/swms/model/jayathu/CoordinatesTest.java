package com.swms.model.jayathu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CoordinatesTest {

    private Coordinates coordinates;

    @BeforeEach
    void setUp() {
        coordinates = new Coordinates();
    }

    @Test
    void testDefaultConstructor() {
        // Act
        Coordinates coords = new Coordinates();

        // Assert
        assertNotNull(coords);
    }

    @Test
    void testParameterizedConstructor() {
        // Arrange
        double latitude = 12.34;
        double longitude = 56.78;

        // Act
        Coordinates coords = new Coordinates(latitude, longitude);

        // Assert
        assertNotNull(coords);
        assertEquals(latitude, coords.getLatitude(), 0.001);
        assertEquals(longitude, coords.getLongitude(), 0.001);
    }

    @Test
    void testGettersAndSetters() {
        // Arrange
        double latitude = 12.34;
        double longitude = 56.78;

        // Act
        coordinates.setLatitude(latitude);
        coordinates.setLongitude(longitude);

        // Assert
        assertEquals(latitude, coordinates.getLatitude(), 0.001);
        assertEquals(longitude, coordinates.getLongitude(), 0.001);
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        Coordinates coords1 = new Coordinates(12.34, 56.78);
        Coordinates coords2 = new Coordinates(12.34, 56.78);
        Coordinates coords3 = new Coordinates(98.76, 54.32);

        // Assert
        assertEquals(coords1, coords2);
        assertNotEquals(coords1, coords3);
        assertEquals(coords1.hashCode(), coords2.hashCode());
        assertNotEquals(coords1.hashCode(), coords3.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        Coordinates coords = new Coordinates(12.34, 56.78);

        // Act
        String toStringResult = coords.toString();

        // Assert
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("latitude=12.34"));
        assertTrue(toStringResult.contains("longitude=56.78"));
    }

    // Edge case tests
    @Test
    void testZeroCoordinates() {
        // Act
        Coordinates coords = new Coordinates(0.0, 0.0);

        // Assert
        assertEquals(0.0, coords.getLatitude(), 0.001);
        assertEquals(0.0, coords.getLongitude(), 0.001);
    }

    @Test
    void testNegativeCoordinates() {
        // Act
        Coordinates coords = new Coordinates(-12.34, -56.78);

        // Assert
        assertEquals(-12.34, coords.getLatitude(), 0.001);
        assertEquals(-56.78, coords.getLongitude(), 0.001);
    }

    @Test
    void testExtremeCoordinates() {
        // Act
        Coordinates coords = new Coordinates(90.0, 180.0);

        // Assert
        assertEquals(90.0, coords.getLatitude(), 0.001);
        assertEquals(180.0, coords.getLongitude(), 0.001);
    }
}