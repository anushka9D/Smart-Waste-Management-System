package com.swms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSmartBinRequest {
    private String location;
    private Double latitude;
    private Double longitude;
    private Double capacity;
    private String wasteType;

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getLocation() {
        return location;
    }

    public Double getCapacity() {
        return capacity;
    }

    public String getWasteType() {
        return wasteType;
    }
}