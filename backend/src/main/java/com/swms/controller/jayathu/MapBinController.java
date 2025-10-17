package com.swms.controller.jayathu;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.swms.dto.jayathu.*;
import com.swms.service.jayathu.*;

import java.util.List;

@RestController
@RequestMapping("/api/map")
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:5173" })
public class MapBinController {

    @Autowired
    private MapBinService mapBinService;

    
    @GetMapping("/bins")
    public ResponseEntity<List<MapBinDto>> getAllBinsForMap() {
        try {
            List<MapBinDto> bins = mapBinService.getAllBinsForMap();
            return new ResponseEntity<>(bins, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    @GetMapping("/bins/waste-type/{wasteType}")
    public ResponseEntity<List<MapBinDto>> getBinsByWasteTypeForMap(@PathVariable String wasteType) {
        try {
            List<MapBinDto> bins = mapBinService.getBinsByWasteTypeForMap(wasteType);
            return new ResponseEntity<>(bins, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

   
    @GetMapping("/bins/status/{status}")
    public ResponseEntity<List<MapBinDto>> getBinsByStatusForMap(@PathVariable String status) {
        try {
            List<MapBinDto> bins = mapBinService.getBinsByStatusForMap(status);
            return new ResponseEntity<>(bins, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

   
    @GetMapping("/bins/location/{location}")
    public ResponseEntity<List<MapBinDto>> getBinsByLocationForMap(@PathVariable String location) {
        try {
            List<MapBinDto> bins = mapBinService.getBinsByLocationForMap(location);
            return new ResponseEntity<>(bins, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

   
    @GetMapping("/bins/filter")
    public ResponseEntity<List<MapBinDto>> getBinsWithFilters(
            @RequestParam(required = false) String wasteType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String location) {
        try {
            List<MapBinDto> bins;
            
            if (wasteType != null && !wasteType.isEmpty()) {
                bins = mapBinService.getBinsByWasteTypeForMap(wasteType);
            } else if (status != null && !status.isEmpty()) {
                bins = mapBinService.getBinsByStatusForMap(status);
            } else if (location != null && !location.isEmpty()) {
                bins = mapBinService.getBinsByLocationForMap(location);
            } else {
                bins = mapBinService.getAllBinsForMap();
            }
            
            return new ResponseEntity<>(bins, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

   
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Map service is working correctly");
    }
}