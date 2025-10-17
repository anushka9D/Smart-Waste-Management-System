package com.swms.service;

import com.swms.dto.AuthResponse;
import com.swms.dto.LoginRequest;
import com.swms.dto.citizen.CitizenRequest;
import com.swms.dto.CityAuthorityRequest;
import com.swms.dto.DriverRequest;
import com.swms.dto.WasteCollectionStaffRequest;
import com.swms.dto.SensorManagerRequest;
import com.swms.model.citizen.Citizen;
import com.swms.model.CityAuthority;
import com.swms.model.Driver;
import com.swms.model.WasteCollectionStaff;
import com.swms.model.SensorManager;
import com.swms.model.User;
import com.swms.repository.citizen.CitizenRepository;
import com.swms.repository.CityAuthorityRepository;
import com.swms.repository.DriverRepository;
import com.swms.repository.WasteCollectionStaffRepository;
import com.swms.repository.SensorManagerRepository;
import com.swms.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private CitizenRepository citizenRepository;

    @Mock
    private CityAuthorityRepository cityAuthorityRepository;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private SensorManagerRepository sensorManagerRepository;

    @Mock
    private WasteCollectionStaffRepository wasteCollectionStaffRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private CitizenRequest citizenRequest;
    private CityAuthorityRequest cityAuthorityRequest;
    private DriverRequest driverRequest;
    private WasteCollectionStaffRequest wasteCollectionStaffRequest;
    private SensorManagerRequest sensorManagerRequest;
    private LoginRequest loginRequest;

    private Citizen testCitizen;
    private CityAuthority testCityAuthority;
    private Driver testDriver;
    private WasteCollectionStaff testWasteCollectionStaff;
    private SensorManager testSensorManager;

    @BeforeEach
    void setUp() {
        // Initialize test data for requests
        citizenRequest = new CitizenRequest();
        citizenRequest.setName("John Doe");
        citizenRequest.setEmail("john.doe@example.com");
        citizenRequest.setPhone("1234567890");
        citizenRequest.setPassword("password123");

        cityAuthorityRequest = new CityAuthorityRequest();
        cityAuthorityRequest.setName("Jane Smith");
        cityAuthorityRequest.setEmail("jane.smith@example.com");
        cityAuthorityRequest.setPhone("0987654321");
        cityAuthorityRequest.setPassword("password123");
        cityAuthorityRequest.setEmployeeId("EMP001");
        cityAuthorityRequest.setDepartment("Waste Management");

        driverRequest = new DriverRequest();
        driverRequest.setName("Bob Johnson");
        driverRequest.setEmail("bob.johnson@example.com");
        driverRequest.setPhone("1112223333");
        driverRequest.setPassword("password123");
        driverRequest.setLicenseNumber("DL123456");
        driverRequest.setVehicleType("TRUCK");

        wasteCollectionStaffRequest = new WasteCollectionStaffRequest();
        wasteCollectionStaffRequest.setName("Alice Brown");
        wasteCollectionStaffRequest.setEmail("alice.brown@example.com");
        wasteCollectionStaffRequest.setPhone("4445556666");
        wasteCollectionStaffRequest.setPassword("password123");
        wasteCollectionStaffRequest.setEmployeeId("EMP002");
        wasteCollectionStaffRequest.setRouteArea("Downtown");

        sensorManagerRequest = new SensorManagerRequest();
        sensorManagerRequest.setName("Charlie Wilson");
        sensorManagerRequest.setEmail("charlie.wilson@example.com");
        sensorManagerRequest.setPhone("7778889999");
        sensorManagerRequest.setPassword("password123");
        sensorManagerRequest.setEmployeeId("EMP003");
        sensorManagerRequest.setAssignedZone("Zone A");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("john.doe@example.com");
        loginRequest.setPassword("password123");

        // Initialize test data for entities
        testCitizen = new Citizen();
        testCitizen.setUserId(UUID.randomUUID().toString());
        testCitizen.setName("John Doe");
        testCitizen.setEmail("john.doe@example.com");
        testCitizen.setPhone("1234567890");
        testCitizen.setPassword("encodedPassword");
        testCitizen.setUserType("CITIZEN");
        testCitizen.setCreatedAt(LocalDateTime.now());
        testCitizen.setUpdatedAt(LocalDateTime.now());
        testCitizen.setEnabled(true);

        testCityAuthority = new CityAuthority();
        testCityAuthority.setUserId(UUID.randomUUID().toString());
        testCityAuthority.setName("Jane Smith");
        testCityAuthority.setEmail("jane.smith@example.com");
        testCityAuthority.setPhone("0987654321");
        testCityAuthority.setPassword("encodedPassword");
        testCityAuthority.setUserType("CITY_AUTHORITY");
        testCityAuthority.setEmployeeId("EMP001");
        testCityAuthority.setDepartment("Waste Management");
        testCityAuthority.setCreatedAt(LocalDateTime.now());
        testCityAuthority.setUpdatedAt(LocalDateTime.now());
        testCityAuthority.setEnabled(true);

        testDriver = new Driver();
        testDriver.setUserId(UUID.randomUUID().toString());
        testDriver.setName("Bob Johnson");
        testDriver.setEmail("bob.johnson@example.com");
        testDriver.setPhone("1112223333");
        testDriver.setPassword("encodedPassword");
        testDriver.setUserType("DRIVER");
        testDriver.setLicenseNumber("DL123456");
        testDriver.setVehicleType("TRUCK");
        testDriver.setCreatedAt(LocalDateTime.now());
        testDriver.setUpdatedAt(LocalDateTime.now());
        testDriver.setEnabled(true);

        testWasteCollectionStaff = new WasteCollectionStaff();
        testWasteCollectionStaff.setUserId(UUID.randomUUID().toString());
        testWasteCollectionStaff.setName("Alice Brown");
        testWasteCollectionStaff.setEmail("alice.brown@example.com");
        testWasteCollectionStaff.setPhone("4445556666");
        testWasteCollectionStaff.setPassword("encodedPassword");
        testWasteCollectionStaff.setUserType("WASTE_COLLECTION_STAFF");
        testWasteCollectionStaff.setEmployeeId("EMP002");
        testWasteCollectionStaff.setRouteArea("Downtown");
        testWasteCollectionStaff.setCreatedAt(LocalDateTime.now());
        testWasteCollectionStaff.setUpdatedAt(LocalDateTime.now());
        testWasteCollectionStaff.setEnabled(true);

        testSensorManager = new SensorManager();
        testSensorManager.setUserId(UUID.randomUUID().toString());
        testSensorManager.setName("Charlie Wilson");
        testSensorManager.setEmail("charlie.wilson@example.com");
        testSensorManager.setPhone("7778889999");
        testSensorManager.setPassword("encodedPassword");
        testSensorManager.setUserType("SENSOR_MANAGER");
        testSensorManager.setEmployeeId("EMP003");
        testSensorManager.setAssignedZone("Zone A");
        testSensorManager.setCreatedAt(LocalDateTime.now());
        testSensorManager.setUpdatedAt(LocalDateTime.now());
        testSensorManager.setEnabled(true);
    }

    // Test registerCitizen method - Positive Cases
    @Test
    void testRegisterCitizen_Positive() {
        // Arrange
        when(citizenRepository.existsByEmail(citizenRequest.getEmail())).thenReturn(false);
        when(cityAuthorityRepository.existsByEmail(citizenRequest.getEmail())).thenReturn(false);
        when(driverRepository.existsByEmail(citizenRequest.getEmail())).thenReturn(false);
        when(wasteCollectionStaffRepository.existsByEmail(citizenRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(citizenRequest.getPassword())).thenReturn("encodedPassword");
        when(citizenRepository.save(any(Citizen.class))).thenReturn(testCitizen);
        when(jwtUtil.generateToken(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn("jwtToken");

        // Act
        AuthResponse response = authService.registerCitizen(citizenRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        assertEquals(testCitizen.getUserId(), response.getUserId());
        assertEquals(testCitizen.getName(), response.getName());
        assertEquals(testCitizen.getEmail(), response.getEmail());
        assertEquals(testCitizen.getPhone(), response.getPhone());
        assertEquals(testCitizen.getUserType(), response.getUserType());
        assertEquals("Citizen registered successfully", response.getMessage());

        verify(citizenRepository).existsByEmail(citizenRequest.getEmail());
        verify(cityAuthorityRepository).existsByEmail(citizenRequest.getEmail());
        verify(driverRepository).existsByEmail(citizenRequest.getEmail());
        verify(wasteCollectionStaffRepository).existsByEmail(citizenRequest.getEmail());
        verify(passwordEncoder).encode(citizenRequest.getPassword());
        verify(citizenRepository).save(any(Citizen.class));
        verify(jwtUtil).generateToken(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    // Test registerCitizen method - Negative Cases
    @Test
    void testRegisterCitizen_EmailAlreadyExists() {
        // Arrange
        when(citizenRepository.existsByEmail(citizenRequest.getEmail())).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.registerCitizen(citizenRequest);
        });

        assertEquals("Email is already registered", exception.getMessage());

        verify(citizenRepository).existsByEmail(citizenRequest.getEmail());
        verify(cityAuthorityRepository, never()).existsByEmail(anyString());
        verify(driverRepository, never()).existsByEmail(anyString());
        verify(wasteCollectionStaffRepository, never()).existsByEmail(anyString());
        verify(citizenRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
        verify(jwtUtil, never()).generateToken(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    // Test registerCitizen method - Edge Cases
    @Test
    void testRegisterCitizen_NullRequest() {
        // Act & Assert
        assertThrows(Exception.class, () -> {
            authService.registerCitizen(null);
        });
    }

    // Test registerCityAuthority method - Positive Cases
    @Test
    void testRegisterCityAuthority_Positive() {
        // Arrange
        when(citizenRepository.existsByEmail(cityAuthorityRequest.getEmail())).thenReturn(false);
        when(cityAuthorityRepository.existsByEmail(cityAuthorityRequest.getEmail())).thenReturn(false);
        when(driverRepository.existsByEmail(cityAuthorityRequest.getEmail())).thenReturn(false);
        when(wasteCollectionStaffRepository.existsByEmail(cityAuthorityRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(cityAuthorityRequest.getPassword())).thenReturn("encodedPassword");
        when(cityAuthorityRepository.save(any(CityAuthority.class))).thenReturn(testCityAuthority);
        when(jwtUtil.generateToken(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn("jwtToken");

        // Act
        AuthResponse response = authService.registerCityAuthority(cityAuthorityRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        assertEquals(testCityAuthority.getUserId(), response.getUserId());
        assertEquals(testCityAuthority.getName(), response.getName());
        assertEquals(testCityAuthority.getEmail(), response.getEmail());
        assertEquals(testCityAuthority.getPhone(), response.getPhone());
        assertEquals(testCityAuthority.getUserType(), response.getUserType());
        assertEquals("City Authority registered successfully", response.getMessage());

        verify(citizenRepository).existsByEmail(cityAuthorityRequest.getEmail());
        verify(cityAuthorityRepository).existsByEmail(cityAuthorityRequest.getEmail());
        verify(driverRepository).existsByEmail(cityAuthorityRequest.getEmail());
        verify(wasteCollectionStaffRepository).existsByEmail(cityAuthorityRequest.getEmail());
        verify(passwordEncoder).encode(cityAuthorityRequest.getPassword());
        verify(cityAuthorityRepository).save(any(CityAuthority.class));
        verify(jwtUtil).generateToken(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    // Test registerCityAuthority method - Negative Cases
    @Test
    void testRegisterCityAuthority_EmailAlreadyExists() {
        // Arrange
        when(cityAuthorityRepository.existsByEmail(cityAuthorityRequest.getEmail())).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.registerCityAuthority(cityAuthorityRequest);
        });

        assertEquals("Email is already registered", exception.getMessage());

        verify(citizenRepository).existsByEmail(cityAuthorityRequest.getEmail());
        verify(cityAuthorityRepository).existsByEmail(cityAuthorityRequest.getEmail());
        verify(driverRepository, never()).existsByEmail(anyString());
        verify(wasteCollectionStaffRepository, never()).existsByEmail(anyString());
        verify(cityAuthorityRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
        verify(jwtUtil, never()).generateToken(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    // Test registerDriver method - Positive Cases
    @Test
    void testRegisterDriver_Positive() {
        // Arrange
        when(citizenRepository.existsByEmail(driverRequest.getEmail())).thenReturn(false);
        when(cityAuthorityRepository.existsByEmail(driverRequest.getEmail())).thenReturn(false);
        when(driverRepository.existsByEmail(driverRequest.getEmail())).thenReturn(false);
        when(wasteCollectionStaffRepository.existsByEmail(driverRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(driverRequest.getPassword())).thenReturn("encodedPassword");
        when(driverRepository.save(any(Driver.class))).thenReturn(testDriver);
        when(jwtUtil.generateToken(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn("jwtToken");

        // Act
        AuthResponse response = authService.registerDriver(driverRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        assertEquals(testDriver.getUserId(), response.getUserId());
        assertEquals(testDriver.getName(), response.getName());
        assertEquals(testDriver.getEmail(), response.getEmail());
        assertEquals(testDriver.getPhone(), response.getPhone());
        assertEquals(testDriver.getUserType(), response.getUserType());
        assertEquals("Driver registered successfully", response.getMessage());

        verify(citizenRepository).existsByEmail(driverRequest.getEmail());
        verify(cityAuthorityRepository).existsByEmail(driverRequest.getEmail());
        verify(driverRepository).existsByEmail(driverRequest.getEmail());
        verify(wasteCollectionStaffRepository).existsByEmail(driverRequest.getEmail());
        verify(passwordEncoder).encode(driverRequest.getPassword());
        verify(driverRepository).save(any(Driver.class));
        verify(jwtUtil).generateToken(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    // Test registerDriver method - Negative Cases
    @Test
    void testRegisterDriver_EmailAlreadyExists() {
        // Arrange
        when(driverRepository.existsByEmail(driverRequest.getEmail())).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.registerDriver(driverRequest);
        });

        assertEquals("Email is already registered", exception.getMessage());

        verify(citizenRepository).existsByEmail(driverRequest.getEmail());
        verify(cityAuthorityRepository).existsByEmail(driverRequest.getEmail());
        verify(driverRepository).existsByEmail(driverRequest.getEmail());
        verify(wasteCollectionStaffRepository, never()).existsByEmail(anyString());
        verify(driverRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
        verify(jwtUtil, never()).generateToken(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    // Test registerWasteCollectionStaff method - Positive Cases
    @Test
    void testRegisterWasteCollectionStaff_Positive() {
        // Arrange
        when(citizenRepository.existsByEmail(wasteCollectionStaffRequest.getEmail())).thenReturn(false);
        when(cityAuthorityRepository.existsByEmail(wasteCollectionStaffRequest.getEmail())).thenReturn(false);
        when(driverRepository.existsByEmail(wasteCollectionStaffRequest.getEmail())).thenReturn(false);
        when(wasteCollectionStaffRepository.existsByEmail(wasteCollectionStaffRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(wasteCollectionStaffRequest.getPassword())).thenReturn("encodedPassword");
        when(wasteCollectionStaffRepository.save(any(WasteCollectionStaff.class))).thenReturn(testWasteCollectionStaff);
        when(jwtUtil.generateToken(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn("jwtToken");

        // Act
        AuthResponse response = authService.registerWasteCollectionStaff(wasteCollectionStaffRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        assertEquals(testWasteCollectionStaff.getUserId(), response.getUserId());
        assertEquals(testWasteCollectionStaff.getName(), response.getName());
        assertEquals(testWasteCollectionStaff.getEmail(), response.getEmail());
        assertEquals(testWasteCollectionStaff.getPhone(), response.getPhone());
        assertEquals(testWasteCollectionStaff.getUserType(), response.getUserType());
        assertEquals("Waste Collection Staff registered successfully", response.getMessage());

        verify(citizenRepository).existsByEmail(wasteCollectionStaffRequest.getEmail());
        verify(cityAuthorityRepository).existsByEmail(wasteCollectionStaffRequest.getEmail());
        verify(driverRepository).existsByEmail(wasteCollectionStaffRequest.getEmail());
        verify(wasteCollectionStaffRepository).existsByEmail(wasteCollectionStaffRequest.getEmail());
        verify(passwordEncoder).encode(wasteCollectionStaffRequest.getPassword());
        verify(wasteCollectionStaffRepository).save(any(WasteCollectionStaff.class));
        verify(jwtUtil).generateToken(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    // Test registerWasteCollectionStaff method - Negative Cases
    @Test
    void testRegisterWasteCollectionStaff_EmailAlreadyExists() {
        // Arrange
        when(wasteCollectionStaffRepository.existsByEmail(wasteCollectionStaffRequest.getEmail())).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.registerWasteCollectionStaff(wasteCollectionStaffRequest);
        });

        assertEquals("Email is already registered", exception.getMessage());

        verify(citizenRepository).existsByEmail(wasteCollectionStaffRequest.getEmail());
        verify(cityAuthorityRepository).existsByEmail(wasteCollectionStaffRequest.getEmail());
        verify(driverRepository).existsByEmail(wasteCollectionStaffRequest.getEmail());
        verify(wasteCollectionStaffRepository).existsByEmail(wasteCollectionStaffRequest.getEmail());
        verify(wasteCollectionStaffRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
        verify(jwtUtil, never()).generateToken(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    // Test registerSensorManager method - Positive Cases
    @Test
    void testRegisterSensorManager_Positive() {
        // Arrange
        when(citizenRepository.existsByEmail(sensorManagerRequest.getEmail())).thenReturn(false);
        when(cityAuthorityRepository.existsByEmail(sensorManagerRequest.getEmail())).thenReturn(false);
        when(driverRepository.existsByEmail(sensorManagerRequest.getEmail())).thenReturn(false);
        when(wasteCollectionStaffRepository.existsByEmail(sensorManagerRequest.getEmail())).thenReturn(false);
        when(sensorManagerRepository.existsByEmail(sensorManagerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(sensorManagerRequest.getPassword())).thenReturn("encodedPassword");
        when(sensorManagerRepository.save(any(SensorManager.class))).thenReturn(testSensorManager);
        when(jwtUtil.generateToken(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn("jwtToken");

        // Act
        AuthResponse response = authService.registerSensorManager(sensorManagerRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        assertEquals(testSensorManager.getUserId(), response.getUserId());
        assertEquals(testSensorManager.getName(), response.getName());
        assertEquals(testSensorManager.getEmail(), response.getEmail());
        assertEquals(testSensorManager.getPhone(), response.getPhone());
        assertEquals(testSensorManager.getUserType(), response.getUserType());
        assertEquals("Sensor Manager registered successfully", response.getMessage());

        verify(citizenRepository).existsByEmail(sensorManagerRequest.getEmail());
        verify(cityAuthorityRepository).existsByEmail(sensorManagerRequest.getEmail());
        verify(driverRepository).existsByEmail(sensorManagerRequest.getEmail());
        verify(wasteCollectionStaffRepository).existsByEmail(sensorManagerRequest.getEmail());
        verify(sensorManagerRepository).existsByEmail(sensorManagerRequest.getEmail());
        verify(passwordEncoder).encode(sensorManagerRequest.getPassword());
        verify(sensorManagerRepository).save(any(SensorManager.class));
        verify(jwtUtil).generateToken(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    // Test registerSensorManager method - Negative Cases
    @Test
    void testRegisterSensorManager_EmailAlreadyExists() {
        // Arrange
        when(sensorManagerRepository.existsByEmail(sensorManagerRequest.getEmail())).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.registerSensorManager(sensorManagerRequest);
        });

        assertEquals("Email is already registered", exception.getMessage());

        verify(citizenRepository).existsByEmail(sensorManagerRequest.getEmail());
        verify(cityAuthorityRepository).existsByEmail(sensorManagerRequest.getEmail());
        verify(driverRepository).existsByEmail(sensorManagerRequest.getEmail());
        verify(wasteCollectionStaffRepository).existsByEmail(sensorManagerRequest.getEmail());
        verify(sensorManagerRepository).existsByEmail(sensorManagerRequest.getEmail());
        verify(sensorManagerRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
        verify(jwtUtil, never()).generateToken(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    // Test login method - Positive Cases
    @Test
    void testLogin_Positive_Citizen() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(citizenRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testCitizen));
        when(jwtUtil.generateToken(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn("jwtToken");

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        assertEquals(testCitizen.getUserId(), response.getUserId());
        assertEquals(testCitizen.getName(), response.getName());
        assertEquals(testCitizen.getEmail(), response.getEmail());
        assertEquals(testCitizen.getPhone(), response.getPhone());
        assertEquals(testCitizen.getUserType(), response.getUserType());
        assertEquals("Login successful", response.getMessage());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(citizenRepository).findByEmail(loginRequest.getEmail());
        verify(cityAuthorityRepository, never()).findByEmail(anyString());
        verify(driverRepository, never()).findByEmail(anyString());
        verify(wasteCollectionStaffRepository, never()).findByEmail(anyString());
        verify(sensorManagerRepository, never()).findByEmail(anyString());
        verify(jwtUtil).generateToken(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    // Test login method - Positive Cases (City Authority)
    @Test
    void testLogin_Positive_CityAuthority() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(citizenRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());
        when(cityAuthorityRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testCityAuthority));
        when(jwtUtil.generateToken(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn("jwtToken");

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        assertEquals(testCityAuthority.getUserId(), response.getUserId());
        assertEquals(testCityAuthority.getName(), response.getName());
        assertEquals(testCityAuthority.getEmail(), response.getEmail());
        assertEquals(testCityAuthority.getPhone(), response.getPhone());
        assertEquals(testCityAuthority.getUserType(), response.getUserType());
        assertEquals("Login successful", response.getMessage());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(citizenRepository).findByEmail(loginRequest.getEmail());
        verify(cityAuthorityRepository).findByEmail(loginRequest.getEmail());
        verify(driverRepository, never()).findByEmail(anyString());
        verify(wasteCollectionStaffRepository, never()).findByEmail(anyString());
        verify(sensorManagerRepository, never()).findByEmail(anyString());
        verify(jwtUtil).generateToken(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    // Test login method - Positive Cases (Driver)
    @Test
    void testLogin_Positive_Driver() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(citizenRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());
        when(cityAuthorityRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());
        when(driverRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testDriver));
        when(jwtUtil.generateToken(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn("jwtToken");

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        assertEquals(testDriver.getUserId(), response.getUserId());
        assertEquals(testDriver.getName(), response.getName());
        assertEquals(testDriver.getEmail(), response.getEmail());
        assertEquals(testDriver.getPhone(), response.getPhone());
        assertEquals(testDriver.getUserType(), response.getUserType());
        assertEquals("Login successful", response.getMessage());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(citizenRepository).findByEmail(loginRequest.getEmail());
        verify(cityAuthorityRepository).findByEmail(loginRequest.getEmail());
        verify(driverRepository).findByEmail(loginRequest.getEmail());
        verify(wasteCollectionStaffRepository, never()).findByEmail(anyString());
        verify(sensorManagerRepository, never()).findByEmail(anyString());
        verify(jwtUtil).generateToken(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    // Test login method - Positive Cases (Waste Collection Staff)
    @Test
    void testLogin_Positive_WasteCollectionStaff() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(citizenRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());
        when(cityAuthorityRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());
        when(driverRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());
        when(wasteCollectionStaffRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testWasteCollectionStaff));
        when(jwtUtil.generateToken(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn("jwtToken");

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        assertEquals(testWasteCollectionStaff.getUserId(), response.getUserId());
        assertEquals(testWasteCollectionStaff.getName(), response.getName());
        assertEquals(testWasteCollectionStaff.getEmail(), response.getEmail());
        assertEquals(testWasteCollectionStaff.getPhone(), response.getPhone());
        assertEquals(testWasteCollectionStaff.getUserType(), response.getUserType());
        assertEquals("Login successful", response.getMessage());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(citizenRepository).findByEmail(loginRequest.getEmail());
        verify(cityAuthorityRepository).findByEmail(loginRequest.getEmail());
        verify(driverRepository).findByEmail(loginRequest.getEmail());
        verify(wasteCollectionStaffRepository).findByEmail(loginRequest.getEmail());
        verify(sensorManagerRepository, never()).findByEmail(anyString());
        verify(jwtUtil).generateToken(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    // Test login method - Positive Cases (Sensor Manager)
    @Test
    void testLogin_Positive_SensorManager() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(citizenRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());
        when(cityAuthorityRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());
        when(driverRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());
        when(wasteCollectionStaffRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());
        when(sensorManagerRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testSensorManager));
        when(jwtUtil.generateToken(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn("jwtToken");

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        assertEquals(testSensorManager.getUserId(), response.getUserId());
        assertEquals(testSensorManager.getName(), response.getName());
        assertEquals(testSensorManager.getEmail(), response.getEmail());
        assertEquals(testSensorManager.getPhone(), response.getPhone());
        assertEquals(testSensorManager.getUserType(), response.getUserType());
        assertEquals("Login successful", response.getMessage());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(citizenRepository).findByEmail(loginRequest.getEmail());
        verify(cityAuthorityRepository).findByEmail(loginRequest.getEmail());
        verify(driverRepository).findByEmail(loginRequest.getEmail());
        verify(wasteCollectionStaffRepository).findByEmail(loginRequest.getEmail());
        verify(sensorManagerRepository).findByEmail(loginRequest.getEmail());
        verify(jwtUtil).generateToken(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    // Test login method - Negative Cases
    @Test
    void testLogin_UserNotFound() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(citizenRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());
        when(cityAuthorityRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());
        when(driverRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());
        when(wasteCollectionStaffRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());
        when(sensorManagerRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals("User not found", exception.getMessage());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(citizenRepository).findByEmail(loginRequest.getEmail());
        verify(cityAuthorityRepository).findByEmail(loginRequest.getEmail());
        verify(driverRepository).findByEmail(loginRequest.getEmail());
        verify(wasteCollectionStaffRepository).findByEmail(loginRequest.getEmail());
        verify(sensorManagerRepository).findByEmail(loginRequest.getEmail());
        verify(jwtUtil, never()).generateToken(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    // Test login method - Edge Cases
    @Test
    void testLogin_NullRequest() {
        // Act & Assert
        assertThrows(Exception.class, () -> {
            authService.login(null);
        });
    }

    // Test findUserByEmail method - Positive Cases
    @Test
    void testFindUserByEmail_Citizen() {
        // Arrange
        when(citizenRepository.findByEmail(testCitizen.getEmail())).thenReturn(Optional.of(testCitizen));

        // Act
        // Using reflection to test private method
        try {
            java.lang.reflect.Method method = AuthService.class.getDeclaredMethod("findUserByEmail", String.class);
            method.setAccessible(true);
            Object result = method.invoke(authService, testCitizen.getEmail());

            // Assert
            assertNotNull(result);
            assertEquals(testCitizen, result);
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }

        verify(citizenRepository).findByEmail(testCitizen.getEmail());
        verify(cityAuthorityRepository, never()).findByEmail(anyString());
        verify(driverRepository, never()).findByEmail(anyString());
        verify(wasteCollectionStaffRepository, never()).findByEmail(anyString());
        verify(sensorManagerRepository, never()).findByEmail(anyString());
    }

    // Test findUserByEmail method - Positive Cases (City Authority)
    @Test
    void testFindUserByEmail_CityAuthority() {
        // Arrange
        when(citizenRepository.findByEmail(testCityAuthority.getEmail())).thenReturn(Optional.empty());
        when(cityAuthorityRepository.findByEmail(testCityAuthority.getEmail())).thenReturn(Optional.of(testCityAuthority));

        // Act
        // Using reflection to test private method
        try {
            java.lang.reflect.Method method = AuthService.class.getDeclaredMethod("findUserByEmail", String.class);
            method.setAccessible(true);
            Object result = method.invoke(authService, testCityAuthority.getEmail());

            // Assert
            assertNotNull(result);
            assertEquals(testCityAuthority, result);
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }

        verify(citizenRepository).findByEmail(testCityAuthority.getEmail());
        verify(cityAuthorityRepository).findByEmail(testCityAuthority.getEmail());
        verify(driverRepository, never()).findByEmail(anyString());
        verify(wasteCollectionStaffRepository, never()).findByEmail(anyString());
        verify(sensorManagerRepository, never()).findByEmail(anyString());
    }

    // Test getUserType method - Positive Cases
    @Test
    void testGetUserType_Citizen() {
        // Act
        // Using reflection to test private method
        try {
            java.lang.reflect.Method method = AuthService.class.getDeclaredMethod("getUserType", User.class);
            method.setAccessible(true);
            Object result = method.invoke(authService, testCitizen);

            // Assert
            assertEquals("CITIZEN", result);
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    // Test getUserType method - Positive Cases (City Authority)
    @Test
    void testGetUserType_CityAuthority() {
        // Act
        // Using reflection to test private method
        try {
            java.lang.reflect.Method method = AuthService.class.getDeclaredMethod("getUserType", User.class);
            method.setAccessible(true);
            Object result = method.invoke(authService, testCityAuthority);

            // Assert
            assertEquals("CITY_AUTHORITY", result);
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    // Test getUserType method - Positive Cases (Driver)
    @Test
    void testGetUserType_Driver() {
        // Act
        // Using reflection to test private method
        try {
            java.lang.reflect.Method method = AuthService.class.getDeclaredMethod("getUserType", User.class);
            method.setAccessible(true);
            Object result = method.invoke(authService, testDriver);

            // Assert
            assertEquals("DRIVER", result);
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    // Test getUserType method - Positive Cases (Waste Collection Staff)
    @Test
    void testGetUserType_WasteCollectionStaff() {
        // Act
        // Using reflection to test private method
        try {
            java.lang.reflect.Method method = AuthService.class.getDeclaredMethod("getUserType", User.class);
            method.setAccessible(true);
            Object result = method.invoke(authService, testWasteCollectionStaff);

            // Assert
            assertEquals("WASTE_COLLECTION_STAFF", result);
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    // Test getUserType method - Positive Cases (Sensor Manager)
    @Test
    void testGetUserType_SensorManager() {
        // Act
        // Using reflection to test private method
        try {
            java.lang.reflect.Method method = AuthService.class.getDeclaredMethod("getUserType", User.class);
            method.setAccessible(true);
            Object result = method.invoke(authService, testSensorManager);

            // Assert
            assertEquals("SENSOR_MANAGER", result);
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }
}