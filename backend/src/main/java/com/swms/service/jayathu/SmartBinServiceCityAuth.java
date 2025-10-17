package com.swms.service.jayathu;

import com.swms.model.*;
import com.swms.model.jayathu.SmartBinCityAuth;
import com.swms.repository.*;
import com.swms.repository.jayathu.SmartBinRepositoryCityAuth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

@Service
public class SmartBinServiceCityAuth {

    @Autowired
    private SmartBinRepositoryCityAuth smartBinRepositorycityauth;

    // Define allowed waste types
    private static final Set<String> ALLOWED_WASTE_TYPES = new HashSet<>();
    static {
        ALLOWED_WASTE_TYPES.add("plastic");
        ALLOWED_WASTE_TYPES.add("organic");
        ALLOWED_WASTE_TYPES.add("metal");
    }

    public SmartBinCityAuth createSmartBin(SmartBinCityAuth smartBin) {
        
        if (smartBin.getWasteType() != null && !ALLOWED_WASTE_TYPES.contains(smartBin.getWasteType())) {
            throw new IllegalArgumentException("Invalid waste type. Allowed types are: plastic, organic, metal");
        }

        LocalDateTime now = LocalDateTime.now();
        smartBin.setCreatedAt(now);
        smartBin.setLastUpdated(now);
        return smartBinRepositorycityauth.save(smartBin);
    }

    public List<SmartBinCityAuth> getAllSmartBins() {
        return smartBinRepositorycityauth.findAll();
    }

    public Optional<SmartBinCityAuth> getSmartBinById(String id) {
        return smartBinRepositorycityauth.findById(id);
    }

    public SmartBinCityAuth updateSmartBin(String id, SmartBinCityAuth smartBinDetails) {
        Optional<SmartBinCityAuth> optionalBin = smartBinRepositorycityauth.findById(id);
        if (optionalBin.isPresent()) {
            SmartBinCityAuth bin = optionalBin.get();

           
            bin.setLocation(smartBinDetails.getLocation());
            bin.setCoordinates(smartBinDetails.getCoordinates());
            bin.setCurrentLevel(smartBinDetails.getCurrentLevel());
            bin.setCapacity(smartBinDetails.getCapacity());
            bin.setStatus(smartBinDetails.getStatus());

           
            if (smartBinDetails.getWasteType() != null
                    && !ALLOWED_WASTE_TYPES.contains(smartBinDetails.getWasteType())) {
                throw new IllegalArgumentException("Invalid waste type. Allowed types are: plastic, organic, metal");
            }
            bin.setWasteType(smartBinDetails.getWasteType());

            bin.setLastUpdated(LocalDateTime.now());

            return smartBinRepositorycityauth.save(bin);
        }
        throw new RuntimeException("Smart bin not found with id: " + id);
    }

    public void deleteSmartBin(String id) {
        smartBinRepositorycityauth.deleteById(id);
    }
}