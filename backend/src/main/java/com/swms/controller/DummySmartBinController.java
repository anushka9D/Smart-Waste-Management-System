package com.swms.controller;

import com.swms.dto.ApiResponse;
// Changed import from SmartBinRequest to DummySmartBinRequest
import com.swms.dto.DummySmartBinRequest;
// Changed import from SmartBin to DummySmartBin
import com.swms.model.DummySmartBin;
// Changed import from SmartBinService to DummySmartBinService
import com.swms.service.DummySmartBinService;
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
public class DummySmartBinController {

    // Changed type from SmartBinService to DummySmartBinService
    private final DummySmartBinService smartBinService;

    @Autowired
    // Changed parameter type from SmartBinService to DummySmartBinService
    public DummySmartBinController(DummySmartBinService smartBinService) {
        this.smartBinService = smartBinService;
    }

    @PostMapping
    // Changed parameter type from SmartBinRequest to DummySmartBinRequest
    public ResponseEntity<ApiResponse<String>> createSmartBin(@RequestBody DummySmartBinRequest request) {
        try {
            // Changed method call from convertToEntity to convertToEntity
            DummySmartBin smartBin = convertToEntity(request);
            String result = smartBinService.saveSmartBin(smartBin);
            return ResponseEntity.ok(ApiResponse.success(result, null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to create smart bin: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DummySmartBin>>> getAllSmartBins() {
        try {
            // Changed return type from List<SmartBin> to List<DummySmartBin>
            List<DummySmartBin> smartBins = smartBinService.getAllSmartBins();
            return ResponseEntity.ok(ApiResponse.success("Smart bins retrieved successfully", smartBins));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to retrieve smart bins: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{binId}")
    public ResponseEntity<ApiResponse<DummySmartBin>> getSmartBinById(@PathVariable String binId) {
        try {
            // Changed return type from Optional<SmartBin> to Optional<DummySmartBin>
            Optional<DummySmartBin> smartBin = smartBinService.getSmartBinById(binId);
            if (smartBin.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Smart bin found", smartBin.get()));
            }
            return ResponseEntity.status(404).body(ApiResponse.error("Smart bin not found with id: " + binId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to retrieve smart bin: " + e.getMessage()));
        }
    }
    
    @GetMapping("/location/{location}")
    public ResponseEntity<ApiResponse<List<DummySmartBin>>> getSmartBinsByLocation(@PathVariable String location) {
        try {
            // Changed return type from List<SmartBin> to List<DummySmartBin>
            List<DummySmartBin> smartBins = smartBinService.getSmartBinsByLocation(location);
            return ResponseEntity.ok(ApiResponse.success("Smart bins retrieved successfully", smartBins));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to retrieve smart bins: " + e.getMessage()));
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<DummySmartBin>>> getSmartBinsByStatus(@PathVariable String status) {
        try {
            // Changed return type from List<SmartBin> to List<DummySmartBin>
            List<DummySmartBin> smartBins = smartBinService.getSmartBinsByStatus(status);
            return ResponseEntity.ok(ApiResponse.success("Smart bins retrieved successfully", smartBins));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to retrieve smart bins: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{binId}")
    // Changed parameter type from SmartBinRequest to DummySmartBinRequest
    public ResponseEntity<ApiResponse<String>> updateSmartBin(@PathVariable String binId, @RequestBody DummySmartBinRequest request) {
        try {
            // Changed method call from convertToEntity to convertToEntity
            DummySmartBin smartBin = convertToEntity(request);
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
     * Converts DummySmartBinRequest DTO to DummySmartBin entity
     * @param request The DTO to convert
     * @return The corresponding entity
     */
    // Changed parameter type from SmartBinRequest to DummySmartBinRequest
    // Changed return type from SmartBin to DummySmartBin
    private DummySmartBin convertToEntity(DummySmartBinRequest request) {
        DummySmartBin smartBin = new DummySmartBin();
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