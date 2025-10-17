package com.swms.dto.jayathu;

public class AnalyticsDto {
    private String label;
    private Object value;
    private String category;

    
    public AnalyticsDto() {
    }

    public AnalyticsDto(String label, Object value) {
        this.label = label;
        this.value = value;
    }

    public AnalyticsDto(String label, Object value, String category) {
        this.label = label;
        this.value = value;
        this.category = category;
    }

    
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}