package com.swms.dto.jayathu;

public class BinStatusDto {
    private String status;
    private int count;
    private double percentage;

    
    public BinStatusDto() {
    }

    public BinStatusDto(String status, int count, int totalBins) {
        this.status = status;
        this.count = count;
        this.percentage = totalBins > 0 ? (double) count / totalBins * 100 : 0;
    }

    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    @Override
    public String toString() {
        return "BinStatusDto{" +
                "status='" + status + '\'' +
                ", count=" + count +
                ", percentage=" + percentage +
                '}';
    }
}