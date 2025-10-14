package com.swms.controller;

import com.swms.dto.ApiResponse;
import com.swms.dto.SmartBinRequest;
import com.swms.model.SmartBin;
import com.swms.service.SmartBinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/smartbins")
@CrossOrigin(originPatterns = "*", maxAge = 3600)
public class SmartBinController {

    private final SmartBinService smartBinService;

    @Autowired
    public SmartBinController(SmartBinService smartBinService) {
        this.smartBinService = smartBinService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createSmartBin(@RequestBody SmartBinRequest request) {
        try {
            SmartBin smartBin = convertToEntity(request);
            String result = smartBinService.saveSmartBin(smartBin);
            return ResponseEntity.ok(ApiResponse.success(result, null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to create smart bin: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SmartBin>>> getAllSmartBins() {
        try {
            List<SmartBin> smartBins = smartBinService.getAllSmartBins();
            return ResponseEntity.ok(ApiResponse.success("Smart bins retrieved successfully", smartBins));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to retrieve smart bins: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{binId}")
    public ResponseEntity<ApiResponse<SmartBin>> getSmartBinById(@PathVariable String binId) {
        try {
            Optional<SmartBin> smartBin = smartBinService.getSmartBinById(binId);
            if (smartBin.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Smart bin found", smartBin.get()));
            }
            return ResponseEntity.status(404).body(ApiResponse.error("Smart bin not found with id: " + binId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to retrieve smart bin: " + e.getMessage()));
        }
    }
    
    @GetMapping("/location/{location}")
    public ResponseEntity<ApiResponse<List<SmartBin>>> getSmartBinsByLocation(@PathVariable String location) {
        try {
            List<SmartBin> smartBins = smartBinService.getSmartBinsByLocation(location);
            return ResponseEntity.ok(ApiResponse.success("Smart bins retrieved successfully", smartBins));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to retrieve smart bins: " + e.getMessage()));
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<SmartBin>>> getSmartBinsByStatus(@PathVariable String status) {
        try {
            List<SmartBin> smartBins = smartBinService.getSmartBinsByStatus(status);
            return ResponseEntity.ok(ApiResponse.success("Smart bins retrieved successfully", smartBins));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to retrieve smart bins: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{binId}")
    public ResponseEntity<ApiResponse<String>> updateSmartBin(@PathVariable String binId, @RequestBody SmartBinRequest request) {
        try {
            SmartBin smartBin = convertToEntity(request);
            String result = smartBinService.updateSmartBin(binId, smartBin);
            return ResponseEntity.ok(ApiResponse.success(result, null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to update smart bin: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{binId}")
    public ResponseEntity<ApiResponse<String>> deleteSmartBin(@PathVariable String binId) {
        try {
            String result = smartBinService.deleteSmartBin(binId);
            return ResponseEntity.ok(ApiResponse.success(result, null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to delete smart bin: " + e.getMessage()));
        }
    }
    
    /**
     * Converts SmartBinRequest DTO to SmartBin entity
     * @param request The DTO to convert
     * @return The corresponding entity
     */
    private SmartBin convertToEntity(SmartBinRequest request) {
        SmartBin smartBin = new SmartBin();
        smartBin.setBinId(request.getBinId());
        smartBin.setLocation(request.getLocation());
        smartBin.setCoordinates(request.getCoordinates());
        smartBin.setCurrentLevel(request.getCurrentLevel());
        smartBin.setCapacity(request.getCapacity());
        
        // Parse the last collected date
        if (request.getLastCollected() != null && !request.getLastCollected().isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime lastCollected = LocalDateTime.parse(request.getLastCollected(), formatter);
                smartBin.setLastCollected(lastCollected);
            } catch (Exception e) {
                // If parsing fails, use current time
                smartBin.setLastCollected(LocalDateTime.now());
            }
        } else {
            smartBin.setLastCollected(LocalDateTime.now());
        }
        
        return smartBin;
    }
}