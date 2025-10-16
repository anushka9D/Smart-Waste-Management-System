package com.swms.controller;

import com.swms.dto.AlertDTO;
import com.swms.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/alerts")

public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping("/unreviewed")
    public ResponseEntity<List<AlertDTO>> getUnreviewedAlerts() {

        List<AlertDTO> alerts = alertService.getAllUnreviewedAlerts();
        return ResponseEntity.ok(alerts);
    }

    @GetMapping
    public ResponseEntity<List<AlertDTO>> getAllAlerts() {

        List<AlertDTO> alerts = alertService.getAllAlerts();
        return ResponseEntity.ok(alerts);
    }

    @PutMapping("/{binId}/review")
    public ResponseEntity<String> markAlertAsReviewed(@PathVariable String binId) {

        alertService.markAlertAsReviewed(binId);
        return ResponseEntity.ok("Alert marked as reviewed");
    }
}
