package com.swms.service;

import com.swms.model.citizen.Citizen;
import com.swms.model.CityAuthority;
import com.swms.model.Driver;
import com.swms.model.WasteCollectionStaff;
import com.swms.model.SensorManager;
import com.swms.repository.citizen.CitizenRepository;
import com.swms.repository.CityAuthorityRepository;
import com.swms.repository.DriverRepository;
import com.swms.repository.WasteCollectionStaffRepository;
import com.swms.repository.SensorManagerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private CitizenRepository citizenRepository;

    @Mock
    private CityAuthorityRepository cityAuthorityRepository;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private WasteCollectionStaffRepository wasteCollectionStaffRepository;

    @Mock
    private SensorManagerRepository sensorManagerRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private Citizen testCitizen;
    private CityAuthority testCityAuthority;
    private Driver testDriver;
    private WasteCollectionStaff testWasteCollectionStaff;
    private SensorManager testSensorManager;

    @BeforeEach
    void setUp() {
        // Initialize test data
        testCitizen = new Citizen();
        testCitizen.setUserId("citizen1");
        testCitizen.setName("John Doe");
        testCitizen.setEmail("john.doe@example.com");
        testCitizen.setPhone("1234567890");
        testCitizen.setPassword("encodedPassword");
        testCitizen.setUserType("CITIZEN");
        testCitizen.setEnabled(true);

        testCityAuthority = new CityAuthority();
        testCityAuthority.setUserId("ca1");
        testCityAuthority.setName("Jane Smith");
        testCityAuthority.setEmail("jane.smith@example.com");
        testCityAuthority.setPhone("0987654321");
        testCityAuthority.setPassword("encodedPassword");
        testCityAuthority.setUserType("CITY_AUTHORITY");
        testCityAuthority.setEmployeeId("EMP001");
        testCityAuthority.setDepartment("Waste Management");
        testCityAuthority.setEnabled(true);

        testDriver = new Driver();
        testDriver.setUserId("driver1");
        testDriver.setName("Bob Johnson");
        testDriver.setEmail("bob.johnson@example.com");
        testDriver.setPhone("1112223333");
        testDriver.setPassword("encodedPassword");
        testDriver.setUserType("DRIVER");
        testDriver.setLicenseNumber("DL12345");
        testDriver.setVehicleType("TRUCK");
        testDriver.setAvailability(true);
        testDriver.setEnabled(true);

        testWasteCollectionStaff = new WasteCollectionStaff();
        testWasteCollectionStaff.setUserId("wcs1");
        testWasteCollectionStaff.setName("Alice Brown");
        testWasteCollectionStaff.setEmail("alice.brown@example.com");
        testWasteCollectionStaff.setPhone("4445556666");
        testWasteCollectionStaff.setPassword("encodedPassword");
        testWasteCollectionStaff.setUserType("WASTE_COLLECTION_STAFF");
        testWasteCollectionStaff.setEmployeeId("EMP002");
        testWasteCollectionStaff.setEnabled(true);

        testSensorManager = new SensorManager();
        testSensorManager.setUserId("sm1");
        testSensorManager.setName("Charlie Wilson");
        testSensorManager.setEmail("charlie.wilson@example.com");
        testSensorManager.setPhone("7778889999");
        testSensorManager.setPassword("encodedPassword");
        testSensorManager.setUserType("SENSOR_MANAGER");
        testSensorManager.setEmployeeId("EMP003");
        testSensorManager.setEnabled(true);
    }

    // Test loadUserByUsername for Citizen - Positive Cases
    @Test
    void testLoadUserByUsername_Citizen_Positive() {
        // Arrange
        when(citizenRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testCitizen));

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("john.doe@example.com");

        // Assert
        assertNotNull(userDetails);
        assertEquals(testCitizen.getEmail(), userDetails.getUsername());
        assertEquals(testCitizen.getPassword(), userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CITIZEN")));
        
        verify(citizenRepository).findByEmail("john.doe@example.com");
    }

    // Test loadUserByUsername for CityAuthority - Positive Cases
    @Test
    void testLoadUserByUsername_CityAuthority_Positive() {
        // Arrange
        when(citizenRepository.findByEmail("jane.smith@example.com")).thenReturn(Optional.empty());
        when(cityAuthorityRepository.findByEmail("jane.smith@example.com")).thenReturn(Optional.of(testCityAuthority));

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("jane.smith@example.com");

        // Assert
        assertNotNull(userDetails);
        assertEquals(testCityAuthority.getEmail(), userDetails.getUsername());
        assertEquals(testCityAuthority.getPassword(), userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CITY_AUTHORITY")));
        
        verify(citizenRepository).findByEmail("jane.smith@example.com");
        verify(cityAuthorityRepository).findByEmail("jane.smith@example.com");
    }

    // Test loadUserByUsername for Driver - Positive Cases
    @Test
    void testLoadUserByUsername_Driver_Positive() {
        // Arrange
        when(citizenRepository.findByEmail("bob.johnson@example.com")).thenReturn(Optional.empty());
        when(cityAuthorityRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(driverRepository.findByEmail("bob.johnson@example.com")).thenReturn(Optional.of(testDriver));

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("bob.johnson@example.com");

        // Assert
        assertNotNull(userDetails);
        assertEquals(testDriver.getEmail(), userDetails.getUsername());
        assertEquals(testDriver.getPassword(), userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_DRIVER")));
        
        verify(citizenRepository).findByEmail("bob.johnson@example.com");
        verify(cityAuthorityRepository).findByEmail("bob.johnson@example.com");
        verify(driverRepository).findByEmail("bob.johnson@example.com");
    }

    // Test loadUserByUsername for WasteCollectionStaff - Positive Cases
    @Test
    void testLoadUserByUsername_WasteCollectionStaff_Positive() {
        // Arrange
        when(citizenRepository.findByEmail("alice.brown@example.com")).thenReturn(Optional.empty());
        when(cityAuthorityRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(driverRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(wasteCollectionStaffRepository.findByEmail("alice.brown@example.com")).thenReturn(Optional.of(testWasteCollectionStaff));

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("alice.brown@example.com");

        // Assert
        assertNotNull(userDetails);
        assertEquals(testWasteCollectionStaff.getEmail(), userDetails.getUsername());
        assertEquals(testWasteCollectionStaff.getPassword(), userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_WASTE_COLLECTION_STAFF")));
        
        verify(citizenRepository).findByEmail("alice.brown@example.com");
        verify(cityAuthorityRepository).findByEmail("alice.brown@example.com");
        verify(driverRepository).findByEmail("alice.brown@example.com");
        verify(wasteCollectionStaffRepository).findByEmail("alice.brown@example.com");
    }

    // Test loadUserByUsername for SensorManager - Positive Cases
    @Test
    void testLoadUserByUsername_SensorManager_Positive() {
        // Arrange
        when(citizenRepository.findByEmail("charlie.wilson@example.com")).thenReturn(Optional.empty());
        when(cityAuthorityRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(driverRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(wasteCollectionStaffRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(sensorManagerRepository.findByEmail("charlie.wilson@example.com")).thenReturn(Optional.of(testSensorManager));

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("charlie.wilson@example.com");

        // Assert
        assertNotNull(userDetails);
        assertEquals(testSensorManager.getEmail(), userDetails.getUsername());
        assertEquals(testSensorManager.getPassword(), userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SENSOR_MANAGER")));
        
        verify(citizenRepository).findByEmail("charlie.wilson@example.com");
        verify(cityAuthorityRepository).findByEmail("charlie.wilson@example.com");
        verify(driverRepository).findByEmail("charlie.wilson@example.com");
        verify(wasteCollectionStaffRepository).findByEmail("charlie.wilson@example.com");
        verify(sensorManagerRepository).findByEmail("charlie.wilson@example.com");
    }

    // Test loadUserByUsername - Negative Cases
    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Arrange
        when(citizenRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
        when(cityAuthorityRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
        when(driverRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
        when(wasteCollectionStaffRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
        when(sensorManagerRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("nonexistent@example.com");
        });

        assertEquals("User not found with email: nonexistent@example.com", exception.getMessage());
        
        verify(citizenRepository).findByEmail("nonexistent@example.com");
        verify(cityAuthorityRepository).findByEmail("nonexistent@example.com");
        verify(driverRepository).findByEmail("nonexistent@example.com");
        verify(wasteCollectionStaffRepository).findByEmail("nonexistent@example.com");
        verify(sensorManagerRepository).findByEmail("nonexistent@example.com");
    }

    // Edge case tests
    @Test
    void testLoadUserByUsername_NullEmail() {
        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(null);
        });

        assertEquals("User not found with email: null", exception.getMessage());
    }

    // Error case tests
    @Test
    void testLoadUserByUsername_CitizenRepositoryException() {
        // Arrange
        when(citizenRepository.findByEmail("john.doe@example.com")).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            customUserDetailsService.loadUserByUsername("john.doe@example.com");
        });

        verify(citizenRepository).findByEmail("john.doe@example.com");
    }
}