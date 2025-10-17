package com.swms.dto.jayathu;

public class MapBinDto {
    private String id;
    private String location;
    private Double latitude;      
    private Double longitude;    
    private Integer currentLevel; 
    private Integer capacity;    
    private String status;
    private String wasteType;

    
    public MapBinDto() {
    }

    public MapBinDto(String id, String location, Double latitude, Double longitude, 
                     Integer currentLevel, Integer capacity, String status, String wasteType) {
        this.id = id;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.currentLevel = currentLevel;
        this.capacity = capacity;
        this.status = status;
        this.wasteType = wasteType;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(Integer currentLevel) {
        this.currentLevel = currentLevel;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWasteType() {
        return wasteType;
    }

    public void setWasteType(String wasteType) {
        this.wasteType = wasteType;
    }

    @Override
    public String toString() {
        return "MapBinDto{" +
                "id='" + id + '\'' +
                ", location='" + location + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", currentLevel=" + currentLevel +
                ", capacity=" + capacity +
                ", status='" + status + '\'' +
                ", wasteType='" + wasteType + '\'' +
                '}';
    }
}