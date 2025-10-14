package com.swms.service;

import com.swms.model.Truck;
import com.swms.repository.TruckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TruckService {

    @Autowired
    private TruckRepository truckRepository;

    public List<Truck> getAllTrucks() {
        return truckRepository.findAll();
    }
    
    public Optional<Truck> getTruckById(String truckId) {
        return truckRepository.findById(truckId);
    }
    
    public String saveTruck(Truck truck) {
        if (truck.getCreatedAt() == null) {
            truck.setCreatedAt(LocalDateTime.now());
        }
        truck.setUpdatedAt(LocalDateTime.now());
        
        Truck savedTruck = truckRepository.save(truck);
        return "Truck created with ID: " + savedTruck.getTruckId();
    }
    
    public String updateTruck(String truckId, Truck truck) {
        Optional<Truck> existingTruck = truckRepository.findById(truckId);
        
        if (existingTruck.isPresent()) {
            truck.setTruckId(truckId); // Ensure the ID remains the same
            truck.setUpdatedAt(LocalDateTime.now());
            truckRepository.save(truck);
            return "Truck updated successfully";
        }
        
        throw new RuntimeException("Truck not found with id: " + truckId);
    }
    
    public String deleteTruck(String truckId) {
        if (truckRepository.existsById(truckId)) {
            truckRepository.deleteById(truckId);
            return "Truck deleted successfully";
        }
        
        throw new RuntimeException("Truck not found with id: " + truckId);
    }
}