package com.swms.service;

import com.swms.dto.WasteCollectionStaffRequest;
import com.swms.model.WasteCollectionStaff;
import com.swms.repository.WasteCollectionStaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WasteCollectionStaffService {

    @Autowired
    private WasteCollectionStaffRepository wasteCollectionStaffRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String saveWasteCollectionStaff(WasteCollectionStaffRequest request) {
        // Check if email already exists
        if (wasteCollectionStaffRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }

        WasteCollectionStaff staff = new WasteCollectionStaff();
        staff.setUserId(UUID.randomUUID().toString());
        staff.setName(request.getName());
        staff.setEmail(request.getEmail());
        staff.setPhone(request.getPhone());
        staff.setPassword(passwordEncoder.encode(request.getPassword()));
        staff.setEmployeeId(request.getEmployeeId());
        staff.setRouteArea(request.getRouteArea());
        staff.setCreatedAt(LocalDateTime.now());
        staff.setUpdatedAt(LocalDateTime.now());
        staff.setEnabled(true);
        
        WasteCollectionStaff savedStaff = wasteCollectionStaffRepository.save(staff);
        
        return "Waste Collection Staff created with ID: " + savedStaff.getUserId();
    }
    
    public List<WasteCollectionStaff> getAllWasteCollectionStaff() {
        return wasteCollectionStaffRepository.findAll();
    }
    
    public Optional<WasteCollectionStaff> getWasteCollectionStaffById(String id) {
        return wasteCollectionStaffRepository.findById(id);
    }
    
    public Optional<WasteCollectionStaff> getWasteCollectionStaffByEmail(String email) {
        return wasteCollectionStaffRepository.findByEmail(email);
    }
    
    public Optional<WasteCollectionStaff> getWasteCollectionStaffByEmployeeId(String employeeId) {
        return wasteCollectionStaffRepository.findByEmployeeId(employeeId);
    }
    
    public String updateWasteCollectionStaff(String id, WasteCollectionStaffRequest request) {
        Optional<WasteCollectionStaff> existingStaff = wasteCollectionStaffRepository.findById(id);
        
        if (existingStaff.isPresent()) {
            WasteCollectionStaff staff = existingStaff.get();
            staff.setName(request.getName());
            staff.setEmail(request.getEmail());
            staff.setPhone(request.getPhone());
            staff.setEmployeeId(request.getEmployeeId());
            staff.setRouteArea(request.getRouteArea());
            staff.setUpdatedAt(LocalDateTime.now());
            
            wasteCollectionStaffRepository.save(staff);
            return "Waste Collection Staff updated successfully";
        }
        
        throw new RuntimeException("Waste Collection Staff not found with id: " + id);
    }
    
    public String deleteWasteCollectionStaff(String id) {
        if (wasteCollectionStaffRepository.existsById(id)) {
            wasteCollectionStaffRepository.deleteById(id);
            return "Waste Collection Staff deleted successfully";
        }
        
        throw new RuntimeException("Waste Collection Staff not found with id: " + id);
    }
}