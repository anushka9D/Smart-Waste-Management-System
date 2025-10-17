package com.swms.controller.anushka;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swms.service.anushka.BinSensorService;

@RestController
@RequestMapping("/api/v1/bin-sensors")

public class BinSensorController {

    private final BinSensorService binSensorService;

    public BinSensorController(BinSensorService binSensorService) {
        this.binSensorService = binSensorService;
    }

    @PutMapping("/{binId}/faulty")
    public ResponseEntity<String> markSensorAsFaulty(@PathVariable String binId) {
        binSensorService.markSensorAsFaulty(binId);
        return ResponseEntity.ok("Sensor marked as faulty");
    }
}
