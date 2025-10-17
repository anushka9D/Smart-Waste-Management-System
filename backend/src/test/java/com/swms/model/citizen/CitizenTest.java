package com.swms.model.citizen;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CitizenTest {

    @Test
    void testCitizenDefaultConstructor() {
        Citizen citizen = new Citizen();
        
        assertNotNull(citizen);
        assertNull(citizen.getUserId());
        assertNull(citizen.getName());
        assertNull(citizen.getEmail());
        assertNull(citizen.getPhone());
        assertNull(citizen.getPassword());
        assertNull(citizen.getUserType());
        assertNull(citizen.getCreatedAt());
        assertNull(citizen.getUpdatedAt());
        assertTrue(citizen.isEnabled());
    }

    @Test
    void testCitizenParameterizedConstructor() {
        LocalDateTime now = LocalDateTime.now();
        Citizen citizen = new Citizen("citizen1", "John Doe", "john@example.com", "1234567890", "password", "CITIZEN");
        
        assertNotNull(citizen);
        assertEquals("citizen1", citizen.getUserId());
        assertEquals("John Doe", citizen.getName());
        assertEquals("john@example.com", citizen.getEmail());
        assertEquals("1234567890", citizen.getPhone());
        assertEquals("password", citizen.getPassword());
        assertEquals("CITIZEN", citizen.getUserType());
        assertNotNull(citizen.getCreatedAt());
        assertNotNull(citizen.getUpdatedAt());
        assertTrue(citizen.isEnabled());
    }

    @Test
    void testCitizenInheritance() {
        Citizen citizen = new Citizen();
        citizen.setUserId("citizen1");
        citizen.setName("John Doe");
        citizen.setEmail("john@example.com");
        citizen.setPhone("1234567890");
        citizen.setPassword("password");
        citizen.setUserType("CITIZEN");
        
        assertEquals("citizen1", citizen.getUserId());
        assertEquals("John Doe", citizen.getName());
        assertEquals("john@example.com", citizen.getEmail());
        assertEquals("1234567890", citizen.getPhone());
        assertEquals("password", citizen.getPassword());
        assertEquals("CITIZEN", citizen.getUserType());
    }
}