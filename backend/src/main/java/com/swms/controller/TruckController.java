package com.swms.controller;

import com.swms.dto.ApiResponse;
import com.swms.model.Truck;
import com.swms.service.TruckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/trucks")
@CrossOrigin(originPatterns = "*", maxAge = 3600)
public class TruckController {

    @Autowired
    private TruckService truckService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createTruck(@RequestBody Truck truck) {
        try {
            String result = truckService.saveTruck(truck);
            return ResponseEntity.ok(ApiResponse.success(result, null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to create truck: " + e.getMessage()));
        }
    }

    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<String>> createTrucks(@RequestBody List<Truck> trucks) {
        try {
            int count = 0;
            for (Truck truck : trucks) {
                truckService.saveTruck(truck);
                count++;
            }
            return ResponseEntity.ok(ApiResponse.success(count + " trucks created successfully", null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to create trucks: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Truck>>> getAllTrucks() {
        try {
            List<Truck> trucks = truckService.getAllTrucks();
            return ResponseEntity.ok(ApiResponse.success("Trucks retrieved successfully", trucks));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to retrieve trucks: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{truckId}")
    public ResponseEntity<ApiResponse<Truck>> getTruckById(@PathVariable String truckId) {
        try {
            Optional<Truck> truck = truckService.getTruckById(truckId);
            if (truck.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Truck found", truck.get()));
            }
            return ResponseEntity.status(404).body(ApiResponse.error("Truck not found with id: " + truckId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to retrieve truck: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{truckId}")
    public ResponseEntity<ApiResponse<String>> updateTruck(@PathVariable String truckId, @RequestBody Truck truck) {
        try {
            String result = truckService.updateTruck(truckId, truck);
            return ResponseEntity.ok(ApiResponse.success(result, null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to update truck: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{truckId}")
    public ResponseEntity<ApiResponse<String>> deleteTruck(@PathVariable String truckId) {
        try {
            String result = truckService.deleteTruck(truckId);
            return ResponseEntity.ok(ApiResponse.success(result, null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to delete truck: " + e.getMessage()));
        }
    }
}