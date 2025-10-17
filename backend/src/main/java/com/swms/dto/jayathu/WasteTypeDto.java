package com.swms.dto.jayathu;

public class WasteTypeDto {
    private String wasteType;
    private int totalWaste;
    private int binCount;
    private double percentage;

   
    public WasteTypeDto() {
    }

    public WasteTypeDto(String wasteType, int totalWaste, int binCount, int totalWasteOverall) {
        this.wasteType = wasteType;
        this.totalWaste = totalWaste;
        this.binCount = binCount;
        this.percentage = totalWasteOverall > 0 ? (double) totalWaste / totalWasteOverall * 100 : 0;
    }

   
    public String getWasteType() {
        return wasteType;
    }

    public void setWasteType(String wasteType) {
        this.wasteType = wasteType;
    }

    public int getTotalWaste() {
        return totalWaste;
    }

    public void setTotalWaste(int totalWaste) {
        this.totalWaste = totalWaste;
    }

    public int getBinCount() {
        return binCount;
    }

    public void setBinCount(int binCount) {
        this.binCount = binCount;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    @Override
    public String toString() {
        return "WasteTypeDto{" +
                "wasteType='" + wasteType + '\'' +
                ", totalWaste=" + totalWaste +
                ", binCount=" + binCount +
                ", percentage=" + percentage +
                '}';
    }
}