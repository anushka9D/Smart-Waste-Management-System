package com.swms.controller.anushka;

import com.swms.dto.*;
import com.swms.service.anushka.SmartBinService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/v1/smartbin")

public class SmartBinController {

    private final SmartBinService smartBinService;

    public SmartBinController(SmartBinService smartBinService) {
        this.smartBinService = smartBinService;
    }

    @PostMapping
    public ResponseEntity<SmartBinDTO> createSmartBin(@RequestBody CreateSmartBinRequest request) {

        SmartBinDTO createdBin = smartBinService.createSmartBin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBin);
    }

    @GetMapping("/{binId}")
    public ResponseEntity<SmartBinDTO> getSmartBin(@PathVariable String binId) {

        SmartBinDTO bin = smartBinService.getSmartBinById(binId);
        return ResponseEntity.ok(bin);
    }

    @GetMapping("/all")
    public ResponseEntity<List<SmartBinDTO>> getAllSmartBins() {

        List<SmartBinDTO> bins = smartBinService.getAllSmartBins();
        return ResponseEntity.ok(bins);
    }

    @GetMapping("/status/full")
    public ResponseEntity<List<SmartBinDTO>> getFullBins() {

        List<SmartBinDTO> fullBins = smartBinService.getFullBins();
        return ResponseEntity.ok(fullBins);
    }

    @GetMapping("/status/empty")
    public ResponseEntity<List<SmartBinDTO>> getEmptyBins() {

        List<SmartBinDTO> emptyBins = smartBinService.getEmptyBins();
        return ResponseEntity.ok(emptyBins);
    }

    @GetMapping("/status/half-full")
    public ResponseEntity<List<SmartBinDTO>> getHalfFullBins() {

        List<SmartBinDTO> halfFullBins = smartBinService.getHalfFullBins();
        return ResponseEntity.ok(halfFullBins);
    }

    @PutMapping("/update/level")
    public ResponseEntity<SmartBinDTO> updateBinLevel(@RequestBody UpdateBinLevelRequest request) {

        SmartBinDTO updatedBin = smartBinService.updateBinCurrentLevel(request);
        return ResponseEntity.ok(updatedBin);
    }

    @PutMapping("/{binId}/collect")
    public ResponseEntity<SmartBinDTO> markBinAsCollected(@PathVariable String binId) {

        SmartBinDTO collectedBin = smartBinService.markBinAsCollected(binId);
        return ResponseEntity.ok(collectedBin);
    }

    @DeleteMapping("/{binId}")
    public ResponseEntity<Void> deleteSmartBin(@PathVariable String binId) {

        smartBinService.deleteSmartBin(binId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/location/{location}")
    public ResponseEntity<List<SmartBinDTO>> getBinsByLocation(@PathVariable String location) {

        List<SmartBinDTO> bins = smartBinService.getBinsByLocation(location);
        return ResponseEntity.ok(bins);
    }
}