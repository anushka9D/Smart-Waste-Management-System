package com.swms.service;

import com.swms.model.SmartBin;
import com.swms.repository.SmartBinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SmartBinService {

    @Autowired
    private SmartBinRepository smartBinRepository;

    public List<SmartBin> getAllSmartBins() {
        List<SmartBin> smartBins = smartBinRepository.findAll();
        // Update status based on current level before returning
        smartBins.forEach(this::updateBinStatus);
        return smartBins;
    }
    
    public Optional<SmartBin> getSmartBinById(String binId) {
        Optional<SmartBin> smartBin = smartBinRepository.findById(binId);
        // Update status based on current level before returning
        smartBin.ifPresent(this::updateBinStatus);
        return smartBin;
    }
    
    public List<SmartBin> getSmartBinsByLocation(String location) {
        List<SmartBin> smartBins = smartBinRepository.findByLocation(location);
        // Update status based on current level before returning
        smartBins.forEach(this::updateBinStatus);
        return smartBins;
    }
    
    public List<SmartBin> getSmartBinsByStatus(String status) {
        return smartBinRepository.findByStatus(status);
    }
    
    public String saveSmartBin(SmartBin smartBin) {
        // Calculate and set status based on current level
        updateBinStatus(smartBin);
        SmartBin savedBin = smartBinRepository.save(smartBin);
        return "Smart bin created with ID: " + savedBin.getBinId();
    }
    
    public String updateSmartBin(String binId, SmartBin smartBin) {
        Optional<SmartBin> existingBin = smartBinRepository.findById(binId);
        
        if (existingBin.isPresent()) {
            // Calculate and set status based on current level
            updateBinStatus(smartBin);
            smartBin.setBinId(binId); // Ensure the ID remains the same
            smartBinRepository.save(smartBin);
            return "Smart bin updated successfully";
        }
        
        throw new RuntimeException("Smart bin not found with id: " + binId);
    }
    
    public String deleteSmartBin(String binId) {
        if (smartBinRepository.existsById(binId)) {
            smartBinRepository.deleteById(binId);
            return "Smart bin deleted successfully";
        }
        
        throw new RuntimeException("Smart bin not found with id: " + binId);
    }
    
    /**
     * Updates the status of a smart bin based on its current fill level
     * @param bin The smart bin to update
     */
    private void updateBinStatus(SmartBin bin) {
        double fillPercentage = (bin.getCurrentLevel() / bin.getCapacity()) * 100;
        
        if (fillPercentage > 80) {
            bin.setStatus("full");
        } else if (fillPercentage > 50) {
            bin.setStatus("nearly full");
        } else {
            bin.setStatus("not full");
        }
    }
}