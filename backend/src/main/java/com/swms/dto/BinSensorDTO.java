package com.swms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BinSensorDTO {
    private String sensorId;
    private String binId;
    private String type;
    private String color;
    private Double measurement;
    private LocalDateTime lastReading;
}
