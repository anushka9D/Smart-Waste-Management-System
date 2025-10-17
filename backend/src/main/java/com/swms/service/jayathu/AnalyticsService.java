package com.swms.service.jayathu;

import com.swms.dto.*;
import com.swms.dto.jayathu.AnalyticsDto;
import com.swms.dto.jayathu.BinStatusDto;
import com.swms.dto.jayathu.DashboardSummaryDto;
import com.swms.dto.jayathu.LocationWasteDto;
import com.swms.dto.jayathu.TotalWasteDto;
import com.swms.dto.jayathu.WasteTypeDto;

import org.springframework.stereotype.Service;
import com.swms.repository.*;
import com.swms.repository.jayathu.SmartBinRepositoryCityAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

        private final SmartBinRepositoryCityAuth smartBinRepository;

        public AnalyticsService(SmartBinRepositoryCityAuth smartBinRepository) {
                this.smartBinRepository = smartBinRepository;
        }

        public DashboardSummaryDto getDashboardAnalytics() {
                DashboardSummaryDto dashboard = new DashboardSummaryDto();

                // Total waste metrics
                var totalStats = smartBinRepository.findTotalWasteStats();
                if (totalStats != null) {
                        dashboard.setTotalWaste(new TotalWasteDto(
                                        totalStats.getTotalWaste() != null ? totalStats.getTotalWaste() : 0,
                                        totalStats.getTotalCapacity() != null ? totalStats.getTotalCapacity() : 0));
                } else {
                        dashboard.setTotalWaste(new TotalWasteDto(0, 0));
                }

                // Waste by location
                List<LocationWasteDto> locationWaste = smartBinRepository.findWasteByLocation()
                                .stream()
                                .map(summary -> new LocationWasteDto(
                                                summary.get_id(),
                                                summary.getTotalWaste() != null ? summary.getTotalWaste() : 0,
                                                summary.getBinCount() != null ? summary.getBinCount() : 0))
                                .collect(Collectors.toList());
                dashboard.setWasteByLocation(locationWaste);

                // Bin status summary
                List<SmartBinRepositoryCityAuth.StatusSummary> statusSummaries = smartBinRepository
                                .findBinStatusSummary();
                int totalBins = statusSummaries.stream()
                                .mapToInt(summary -> summary.getCount() != null ? summary.getCount() : 0)
                                .sum();

                List<BinStatusDto> binStatus = statusSummaries.stream()
                                .map(summary -> new BinStatusDto(
                                                summary.get_id(),
                                                summary.getCount() != null ? summary.getCount() : 0,
                                                totalBins))
                                .collect(Collectors.toList());
                dashboard.setBinStatus(binStatus);

                // Top 5 locations with most waste
                List<LocationWasteDto> topLocations = locationWaste.stream()
                                .limit(5)
                                .collect(Collectors.toList());
                dashboard.setTopLocations(topLocations);

                return dashboard;
        }

        public List<LocationWasteDto> getWasteByLocation() {
                return smartBinRepository.findWasteByLocation()
                                .stream()
                                .map(summary -> new LocationWasteDto(
                                                summary.get_id(),
                                                summary.getTotalWaste() != null ? summary.getTotalWaste() : 0,
                                                summary.getBinCount() != null ? summary.getBinCount() : 0))
                                .collect(Collectors.toList());
        }

        public List<BinStatusDto> getBinStatusSummary() {
                List<SmartBinRepositoryCityAuth.StatusSummary> statusSummaries = smartBinRepository
                                .findBinStatusSummary();
                int totalBins = statusSummaries.stream()
                                .mapToInt(summary -> summary.getCount() != null ? summary.getCount() : 0)
                                .sum();

                return statusSummaries.stream()
                                .map(summary -> new BinStatusDto(
                                                summary.get_id(),
                                                summary.getCount() != null ? summary.getCount() : 0,
                                                totalBins))
                                .collect(Collectors.toList());
        }

        public TotalWasteDto getTotalWasteMetrics() {
                var stats = smartBinRepository.findTotalWasteStats();
                if (stats != null) {
                        return new TotalWasteDto(
                                        stats.getTotalWaste() != null ? stats.getTotalWaste() : 0,
                                        stats.getTotalCapacity() != null ? stats.getTotalCapacity() : 0);
                }
                return new TotalWasteDto(0, 0);
        }

       
        public List<WasteTypeDto> getWasteByType() {
                List<SmartBinRepositoryCityAuth.WasteTypeSummary> wasteTypeSummaries = smartBinRepository
                                .findWasteByType();
                int totalWasteOverall = wasteTypeSummaries.stream()
                                .mapToInt(summary -> summary.getTotalWaste() != null ? summary.getTotalWaste() : 0)
                                .sum();

                return wasteTypeSummaries.stream()
                                .map(summary -> new WasteTypeDto(
                                                summary.get_id(),
                                                summary.getTotalWaste() != null ? summary.getTotalWaste() : 0,
                                                summary.getBinCount() != null ? summary.getBinCount() : 0,
                                                totalWasteOverall))
                                .collect(Collectors.toList());
        }

       
        public int getTotalEWaste() {
                List<SmartBinRepositoryCityAuth.EWasteStats> eWasteStatsList = smartBinRepository.findTotalEWaste();
                if (!eWasteStatsList.isEmpty()) {
                        Integer total = eWasteStatsList.get(0).getTotalEWaste();
                        return total != null ? total : 0;
                }
                return 0;
        }

       
        public int getTotalRecyclableWaste() {
                List<SmartBinRepositoryCityAuth.RecyclableStats> recyclableStatsList = smartBinRepository
                                .findTotalRecyclableWaste();
                if (!recyclableStatsList.isEmpty()) {
                        Integer total = recyclableStatsList.get(0).getTotalRecyclable();
                        return total != null ? total : 0;
                }
                return 0;
        }

     
        public LocationWasteDto getLocationWithMostWaste() {
                List<SmartBinRepositoryCityAuth.LocationSummary> locationSummaries = smartBinRepository
                                .findLocationWithMostWaste();
                if (!locationSummaries.isEmpty()) {
                        SmartBinRepositoryCityAuth.LocationSummary summary = locationSummaries.get(0);
                        return new LocationWasteDto(
                                        summary.get_id(),
                                        summary.getTotalWaste() != null ? summary.getTotalWaste() : 0,
                                        summary.getBinCount() != null ? summary.getBinCount() : 0);
                }
                return new LocationWasteDto("No data", 0, 0);
        }

       
        public int getTotalPlasticWaste() {
                List<SmartBinRepositoryCityAuth.PlasticStats> plasticStatsList = smartBinRepository
                                .findTotalPlasticWaste();
                if (!plasticStatsList.isEmpty()) {
                        Integer total = plasticStatsList.get(0).getTotalPlastic();
                        return total != null ? total : 0;
                }
                return 0;
        }

       
        public int getTotalOrganicWaste() {
                List<SmartBinRepositoryCityAuth.OrganicStats> organicStatsList = smartBinRepository
                                .findTotalOrganicWaste();
                if (!organicStatsList.isEmpty()) {
                        Integer total = organicStatsList.get(0).getTotalOrganic();
                        return total != null ? total : 0;
                }
                return 0;
        }

       
        public int getTotalMetalWaste() {
                List<SmartBinRepositoryCityAuth.MetalStats> metalStatsList = smartBinRepository.findTotalMetalWaste();
                if (!metalStatsList.isEmpty()) {
                        Integer total = metalStatsList.get(0).getTotalMetal();
                        return total != null ? total : 0;
                }
                return 0;
        }

       
        public List<WasteTypeDto> getWasteByTypeFiltered(String wasteType) {
               
                if (wasteType == null || wasteType.trim().isEmpty()) {
                        return new ArrayList<>();
                }

                
                List<SmartBinRepositoryCityAuth.WasteTypeSummary> allWasteTypeSummaries = smartBinRepository
                                .findWasteByType();
                List<SmartBinRepositoryCityAuth.WasteTypeSummary> filteredSummaries = allWasteTypeSummaries.stream()
                                .filter(summary -> summary.get_id() != null &&
                                                summary.get_id().equalsIgnoreCase(wasteType))
                                .collect(Collectors.toList());

              
                int totalWasteOverall = allWasteTypeSummaries.stream()
                                .mapToInt(summary -> summary.getTotalWaste() != null ? summary.getTotalWaste() : 0)
                                .sum();

               
                return filteredSummaries.stream()
                                .map(summary -> new WasteTypeDto(
                                                summary.get_id(),
                                                summary.getTotalWaste() != null ? summary.getTotalWaste() : 0,
                                                summary.getBinCount() != null ? summary.getBinCount() : 0,
                                                totalWasteOverall))
                                .collect(Collectors.toList());
        }

        
        public List<LocationWasteDto> getWasteByLocationFiltered(String wasteType) {
                
                if (wasteType == null || wasteType.trim().isEmpty()) {
                        return new ArrayList<>();
                }

               
                List<SmartBinRepositoryCityAuth.LocationSummary> locationSummaries = smartBinRepository
                                .findWasteByLocationAndWasteType(wasteType);

              
                return locationSummaries.stream()
                                .map(summary -> new LocationWasteDto(
                                                summary.get_id(),
                                                summary.getTotalWaste() != null ? summary.getTotalWaste() : 0,
                                                summary.getBinCount() != null ? summary.getBinCount() : 0))
                                .collect(Collectors.toList());
        }

       
        public List<AnalyticsDto> getWasteByLocationForCharts() {
                return smartBinRepository.findWasteByLocation()
                                .stream()
                                .map(summary -> new AnalyticsDto(
                                                summary.get_id(),
                                                summary.getTotalWaste() != null ? summary.getTotalWaste() : 0,
                                                "location"))
                                .collect(Collectors.toList());
        }

        public List<AnalyticsDto> getBinStatusForCharts() {
                List<SmartBinRepositoryCityAuth.StatusSummary> statusSummaries = smartBinRepository
                                .findBinStatusSummary();
                int totalBins = statusSummaries.stream()
                                .mapToInt(summary -> summary.getCount() != null ? summary.getCount() : 0)
                                .sum();

                return statusSummaries.stream()
                                .map(summary -> new AnalyticsDto(
                                                summary.get_id(),
                                                summary.getCount() != null ? summary.getCount() : 0,
                                                "status"))
                                .collect(Collectors.toList());
        }

       
        public List<AnalyticsDto> getWasteByTypeForCharts() {
                return smartBinRepository.findWasteByType()
                                .stream()
                                .map(summary -> new AnalyticsDto(
                                                summary.get_id(),
                                                summary.getTotalWaste() != null ? summary.getTotalWaste() : 0,
                                                "wasteType"))
                                .collect(Collectors.toList());
        }

        
        public String testService() {
                return "Analytics service is working correctly";
        }
}