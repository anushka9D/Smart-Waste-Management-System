package com.swms.service.jayathu;
import com.swms.dto.jayathu.*;
import com.swms.model.jayathu.*;
import com.swms.repository.jayathu.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MapBinService {

    @Autowired
    private MapBinRepository mapBinRepository;

   
    public List<MapBinDto> getAllBinsForMap() {
        List<SmartBinCityAuth> bins = mapBinRepository.findAll();
        return convertToMapBinDtoList(bins);
    }

    
    public List<MapBinDto> getBinsByWasteTypeForMap(String wasteType) {
        List<SmartBinCityAuth> bins = mapBinRepository.findByWasteType(wasteType);
        return convertToMapBinDtoList(bins);
    }

   
    public List<MapBinDto> getBinsByStatusForMap(String status) {
        List<SmartBinCityAuth> bins = mapBinRepository.findByStatus(status);
        return convertToMapBinDtoList(bins);
    }

   
    public List<MapBinDto> getBinsByLocationForMap(String location) {
        List<SmartBinCityAuth> bins = mapBinRepository.findByLocation(location);
        return convertToMapBinDtoList(bins);
    }

   
   private List<MapBinDto> convertToMapBinDtoList(List<SmartBinCityAuth> bins) {
    if (bins == null) {
        return new ArrayList<>();
    }

    return bins.stream()
            .filter(bin -> bin.getCoordinates() != null)
            .filter(bin -> bin.getCoordinates().getLatitude() != null && 
                          bin.getCoordinates().getLongitude() != null)
            .map(bin -> new MapBinDto(
                    bin.getId(),
                    bin.getLocation(),
                    bin.getCoordinates().getLatitude(),
                    bin.getCoordinates().getLongitude(),
                    bin.getCurrentLevel() != null ? bin.getCurrentLevel().intValue() : 0,
                    bin.getCapacity() != null ? bin.getCapacity().intValue() : 100,
                    bin.getStatus(),
                    bin.getWasteType()
            ))
            .collect(Collectors.toList());
}
}