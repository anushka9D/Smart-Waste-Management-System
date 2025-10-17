package com.swms.controller.jayathu;

import com.swms.service.*;
import com.swms.service.jayathu.SmartBinServiceCityAuth;
import com.swms.model.*;
import com.swms.model.jayathu.SmartBinCityAuth;
import com.swms.security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bins")
@CrossOrigin(origins = { "http://localhost:5173" })
public class SmartBinControllerCityAuth {

    @Autowired
    private SmartBinServiceCityAuth smartBinService;

    
    @PostMapping
    public ResponseEntity<?> createSmartBin(@RequestBody SmartBinCityAuth smartBin) {
        try {
            SmartBinCityAuth savedBin = smartBinService.createSmartBin(smartBin);
            return new ResponseEntity<>(savedBin, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Error creating smart bin: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating smart bin: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

   
    @GetMapping
    public ResponseEntity<List<SmartBinCityAuth>> getAllSmartBins() {
        try {
            List<SmartBinCityAuth> bins = smartBinService.getAllSmartBins();
            return new ResponseEntity<>(bins, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<?> getSmartBinById(@PathVariable String id) {
        try {
            Optional<SmartBinCityAuth> bin = smartBinService.getSmartBinById(id);
            if (bin.isPresent()) {
                return new ResponseEntity<>(bin.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Smart bin not found with id: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error retrieving smart bin: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

   
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSmartBin(@PathVariable String id, @RequestBody SmartBinCityAuth smartBinDetails) {
        try {
            SmartBinCityAuth updatedBin = smartBinService.updateSmartBin(id, smartBinDetails);
            return new ResponseEntity<>(updatedBin, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Error updating smart bin: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating smart bin: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSmartBin(@PathVariable String id) {
        try {
            smartBinService.deleteSmartBin(id);
            return new ResponseEntity<>("Smart bin deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting smart bin: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("SmartBin service is working correctly");
    }
}