package com.swms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBinLevelRequest {
    private String binId;
    private Double currentLevel;

    public Double getCurrentLevel() {
        return currentLevel;
    }

    public String getBinId() {
        return binId;
    }
}
