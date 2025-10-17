package com.swms.dto.jayathu;

import java.util.List;

public class DashboardSummaryDto {
    private TotalWasteDto totalWaste;
    private List<LocationWasteDto> wasteByLocation;
    private List<BinStatusDto> binStatus;
    private List<LocationWasteDto> topLocations;
    
    
    public DashboardSummaryDto() {}
    
    
    public TotalWasteDto getTotalWaste() { return totalWaste; }
    public void setTotalWaste(TotalWasteDto totalWaste) { this.totalWaste = totalWaste; }
    
    public List<LocationWasteDto> getWasteByLocation() { return wasteByLocation; }
    public void setWasteByLocation(List<LocationWasteDto> wasteByLocation) { this.wasteByLocation = wasteByLocation; }
    
    public List<BinStatusDto> getBinStatus() { return binStatus; }
    public void setBinStatus(List<BinStatusDto> binStatus) { this.binStatus = binStatus; }
    
    public List<LocationWasteDto> getTopLocations() { return topLocations; }
    public void setTopLocations(List<LocationWasteDto> topLocations) { this.topLocations = topLocations; }
}