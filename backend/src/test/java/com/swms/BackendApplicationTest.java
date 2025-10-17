package com.swms;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BackendApplicationTest {

    @Test
    void testMainMethod() {
        // Arrange
        String[] args = new String[]{};

        // Act & Assert
        // We just test that the main method doesn't throw exceptions
        // We don't actually run the Spring application context
        assertDoesNotThrow(() -> {
            BackendApplication.main(args);
        });
    }
}