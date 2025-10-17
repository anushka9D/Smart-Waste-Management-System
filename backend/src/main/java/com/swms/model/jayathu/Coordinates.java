package com.swms.model.jayathu;

import lombok.Data;

@Data
public class Coordinates {
    private double latitude;
    private double longitude;

   
    public Coordinates() {
    }

    
    public Coordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}