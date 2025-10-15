package com.swms.service;

import com.swms.dto.AuthResponse;
import com.swms.dto.LoginRequest;
import com.swms.dto.citizen.CitizenRequest;
import com.swms.dto.CityAuthorityRequest;
import com.swms.dto.DriverRequest;
import com.swms.dto.WasteCollectionStaffRequest;
import com.swms.model.citizen.Citizen;
import com.swms.model.CityAuthority;
import com.swms.model.Driver;
import com.swms.model.User;
import com.swms.model.WasteCollectionStaff;
import com.swms.repository.citizen.CitizenRepository;
import com.swms.repository.CityAuthorityRepository;
import com.swms.repository.DriverRepository;
import com.swms.repository.WasteCollectionStaffRepository;
import com.swms.dto.SensorManagerRequest;
import com.swms.model.SensorManager;
import com.swms.repository.SensorManagerRepository;
import com.swms.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private CitizenRepository citizenRepository;
    
    @Autowired
    private CityAuthorityRepository cityAuthorityRepository;
    
    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private SensorManagerRepository sensorManagerRepository;
    
    @Autowired
    private WasteCollectionStaffRepository wasteCollectionStaffRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    // Citizen registration
    public AuthResponse registerCitizen(CitizenRequest request) {
        // Check if email already exists
        if (citizenRepository.existsByEmail(request.getEmail()) ||
            cityAuthorityRepository.existsByEmail(request.getEmail()) ||
            driverRepository.existsByEmail(request.getEmail()) ||
            wasteCollectionStaffRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }

        // Create new citizen
        Citizen citizen = new Citizen();
        citizen.setUserId(UUID.randomUUID().toString());
        citizen.setName(request.getName());
        citizen.setEmail(request.getEmail());
        citizen.setPhone(request.getPhone());
        citizen.setPassword(passwordEncoder.encode(request.getPassword()));
        citizen.setUserType("CITIZEN");
        citizen.setAge(request.getAge());
        citizen.setCreatedAt(LocalDateTime.now());
        citizen.setUpdatedAt(LocalDateTime.now());
        citizen.setEnabled(true);

        Citizen savedCitizen = citizenRepository.save(citizen);

        // Generate JWT token with userType
        String token = jwtUtil.generateToken(
            savedCitizen.getUserId(), 
            savedCitizen.getName(), 
            savedCitizen.getEmail(),
            savedCitizen.getUserType()
        );

        return new AuthResponse(
                token,
                savedCitizen.getUserId(),
                savedCitizen.getName(),
                savedCitizen.getEmail(),
                savedCitizen.getPhone(),
                savedCitizen.getUserType(),
                "Citizen registered successfully"
        );
    }
    
    // City Authority registration
    public AuthResponse registerCityAuthority(CityAuthorityRequest request) {
        // Check if email already exists
        if (citizenRepository.existsByEmail(request.getEmail()) ||
            cityAuthorityRepository.existsByEmail(request.getEmail()) ||
            driverRepository.existsByEmail(request.getEmail()) ||
            wasteCollectionStaffRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }

        // Create new city authority
        CityAuthority cityAuthority = new CityAuthority();
        cityAuthority.setUserId(UUID.randomUUID().toString());
        cityAuthority.setName(request.getName());
        cityAuthority.setEmail(request.getEmail());
        cityAuthority.setPhone(request.getPhone());
        cityAuthority.setPassword(passwordEncoder.encode(request.getPassword()));
        cityAuthority.setUserType("CITY_AUTHORITY");
        cityAuthority.setEmployeeId(request.getEmployeeId());
        cityAuthority.setDepartment(request.getDepartment());
        cityAuthority.setCreatedAt(LocalDateTime.now());
        cityAuthority.setUpdatedAt(LocalDateTime.now());
        cityAuthority.setEnabled(true);

        CityAuthority savedCityAuthority = cityAuthorityRepository.save(cityAuthority);

        // Generate JWT token with userType
        String token = jwtUtil.generateToken(
            savedCityAuthority.getUserId(), 
            savedCityAuthority.getName(), 
            savedCityAuthority.getEmail(),
            savedCityAuthority.getUserType()
        );

        return new AuthResponse(
                token,
                savedCityAuthority.getUserId(),
                savedCityAuthority.getName(),
                savedCityAuthority.getEmail(),
                savedCityAuthority.getPhone(),
                savedCityAuthority.getUserType(),
                "City Authority registered successfully"
        );
    }
    
    // Driver registration
    public AuthResponse registerDriver(DriverRequest request) {
        // Check if email already exists
        if (citizenRepository.existsByEmail(request.getEmail()) ||
            cityAuthorityRepository.existsByEmail(request.getEmail()) ||
            driverRepository.existsByEmail(request.getEmail()) ||
            wasteCollectionStaffRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }

        // Create new driver
        Driver driver = new Driver();
        driver.setUserId(UUID.randomUUID().toString());
        driver.setName(request.getName());
        driver.setEmail(request.getEmail());
        driver.setPhone(request.getPhone());
        driver.setPassword(passwordEncoder.encode(request.getPassword()));
        driver.setUserType("DRIVER");
        driver.setLicenseNumber(request.getLicenseNumber());
        driver.setVehicleType(request.getVehicleType());
        driver.setCreatedAt(LocalDateTime.now());
        driver.setUpdatedAt(LocalDateTime.now());
        driver.setEnabled(true);

        Driver savedDriver = driverRepository.save(driver);

        // Generate JWT token with userType
        String token = jwtUtil.generateToken(
            savedDriver.getUserId(), 
            savedDriver.getName(), 
            savedDriver.getEmail(),
            savedDriver.getUserType()
        );

        return new AuthResponse(
                token,
                savedDriver.getUserId(),
                savedDriver.getName(),
                savedDriver.getEmail(),
                savedDriver.getPhone(),
                savedDriver.getUserType(),
                "Driver registered successfully"
        );
    }
    
    // Waste Collection Staff registration
    public AuthResponse registerWasteCollectionStaff(WasteCollectionStaffRequest request) {
        // Check if email already exists
        if (citizenRepository.existsByEmail(request.getEmail()) ||
            cityAuthorityRepository.existsByEmail(request.getEmail()) ||
            driverRepository.existsByEmail(request.getEmail()) ||
            wasteCollectionStaffRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }

        // Create new waste collection staff
        WasteCollectionStaff staff = new WasteCollectionStaff();
        staff.setUserId(UUID.randomUUID().toString());
        staff.setName(request.getName());
        staff.setEmail(request.getEmail());
        staff.setPhone(request.getPhone());
        staff.setPassword(passwordEncoder.encode(request.getPassword()));
        staff.setUserType("WASTE_COLLECTION_STAFF");
        staff.setEmployeeId(request.getEmployeeId());
        staff.setRouteArea(request.getRouteArea());
        staff.setCreatedAt(LocalDateTime.now());
        staff.setUpdatedAt(LocalDateTime.now());
        staff.setEnabled(true);

        WasteCollectionStaff savedStaff = wasteCollectionStaffRepository.save(staff);

        // Generate JWT token with userType
        String token = jwtUtil.generateToken(
            savedStaff.getUserId(), 
            savedStaff.getName(), 
            savedStaff.getEmail(),
            savedStaff.getUserType()
        );

        return new AuthResponse(
                token,
                savedStaff.getUserId(),
                savedStaff.getName(),
                savedStaff.getEmail(),
                savedStaff.getPhone(),
                savedStaff.getUserType(),
                "Waste Collection Staff registered successfully"
        );
    }

    public AuthResponse registerSensorManager(SensorManagerRequest request) {
    // Check if email already exists
    if (citizenRepository.existsByEmail(request.getEmail()) ||
        cityAuthorityRepository.existsByEmail(request.getEmail()) ||
        driverRepository.existsByEmail(request.getEmail()) ||
        wasteCollectionStaffRepository.existsByEmail(request.getEmail()) ||
        sensorManagerRepository.existsByEmail(request.getEmail())) {
        throw new RuntimeException("Email is already registered");
    }

    // Create new sensor manager
    SensorManager sensorManager = new SensorManager();
    sensorManager.setUserId(UUID.randomUUID().toString());
    sensorManager.setName(request.getName());
    sensorManager.setEmail(request.getEmail());
    sensorManager.setPhone(request.getPhone());
    sensorManager.setPassword(passwordEncoder.encode(request.getPassword()));
    sensorManager.setUserType("SENSOR_MANAGER");
    sensorManager.setEmployeeId(request.getEmployeeId());
    sensorManager.setAssignedZone(request.getAssignedZone());
    sensorManager.setCreatedAt(LocalDateTime.now());
    sensorManager.setUpdatedAt(LocalDateTime.now());
    sensorManager.setEnabled(true);

    SensorManager savedSensorManager = sensorManagerRepository.save(sensorManager);

    // Generate JWT token with userType
    String token = jwtUtil.generateToken(
        savedSensorManager.getUserId(), 
        savedSensorManager.getName(), 
        savedSensorManager.getEmail(),
        savedSensorManager.getUserType()
    );

    return new AuthResponse(
            token,
            savedSensorManager.getUserId(),
            savedSensorManager.getName(),
            savedSensorManager.getEmail(),
            savedSensorManager.getPhone(),
            savedSensorManager.getUserType(),
            "Sensor Manager registered successfully"
    );
}

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Try to find user among all types
        User user = findUserByEmail(request.getEmail());
        
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        String userType = getUserType(user);

        // Generate JWT token with userType
        String token = jwtUtil.generateToken(
            user.getUserId(), 
            user.getName(), 
            user.getEmail(),
            userType
        );

        return new AuthResponse(
                token,
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                userType,
                "Login successful"
        );
    }
    
    private User findUserByEmail(String email) {
        // Try to find citizen
        return citizenRepository.findByEmail(email)
                .map(user -> (User) user)
                .orElseGet(() -> 
                    cityAuthorityRepository.findByEmail(email)
                        .map(user -> (User) user)
                        .orElseGet(() -> 
                            driverRepository.findByEmail(email)
                                .map(user -> (User) user)
                                .orElseGet(() -> 
                                    wasteCollectionStaffRepository.findByEmail(email)
                                        .map(user -> (User) user)
                                        .orElseGet(() -> 
                                            sensorManagerRepository.findByEmail(email)
                                                .map(user -> (User) user)
                                                .orElse(null)
                                        )
                                )
                        )
                );
    }
    
    private String getUserType(User user) {
        if (user instanceof Citizen) {
            return "CITIZEN";
        } else if (user instanceof CityAuthority) {
            return "CITY_AUTHORITY";
        } else if (user instanceof Driver) {
            return "DRIVER";
        } else if (user instanceof WasteCollectionStaff) {
            return "WASTE_COLLECTION_STAFF";
        } else if (user instanceof SensorManager) {
            return "SENSOR_MANAGER";
        }
        return "UNKNOWN";
    }
}