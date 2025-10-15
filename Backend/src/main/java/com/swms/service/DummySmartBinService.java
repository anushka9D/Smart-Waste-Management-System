package com.swms.service;

// Changed import from SmartBin to DummySmartBin
import com.swms.model.DummySmartBin;
// Changed import from SmartBinRepository to DummySmartBinRepository
import com.swms.repository.DummySmartBinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DummySmartBinService {

    @Autowired
    // Changed type from SmartBinRepository to DummySmartBinRepository
    private DummySmartBinRepository smartBinRepository;

    // Changed return type from List<SmartBin> to List<DummySmartBin>
    public List<DummySmartBin> getAllSmartBins() {
        List<DummySmartBin> smartBins = smartBinRepository.findAll();
        // Update status based on current level before returning
        smartBins.forEach(this::updateBinStatus);
        return smartBins;
    }
    
    // Changed return type from Optional<SmartBin> to Optional<DummySmartBin>
    public Optional<DummySmartBin> getSmartBinById(String binId) {
        Optional<DummySmartBin> smartBin = smartBinRepository.findById(binId);
        // Update status based on current level before returning
        smartBin.ifPresent(this::updateBinStatus);
        return smartBin;
    }
    
    // Changed return type from List<SmartBin> to List<DummySmartBin>
    public List<DummySmartBin> getSmartBinsByLocation(String location) {
        List<DummySmartBin> smartBins = smartBinRepository.findByLocation(location);
        // Update status based on current level before returning
        smartBins.forEach(this::updateBinStatus);
        return smartBins;
    }
    
    // Changed return type from List<SmartBin> to List<DummySmartBin>
    public List<DummySmartBin> getSmartBinsByStatus(String status) {
        return smartBinRepository.findByStatus(status);
    }
    
    // Changed parameter type from SmartBin to DummySmartBin
    public String saveSmartBin(DummySmartBin smartBin) {
        // Calculate and set status based on current level
        updateBinStatus(smartBin);
        DummySmartBin savedBin = smartBinRepository.save(smartBin);
        return "Smart bin created with ID: " + savedBin.getBinId();
    }
    
    // Changed parameter type from SmartBin to DummySmartBin
    public String updateSmartBin(String binId, DummySmartBin smartBin) {
        Optional<DummySmartBin> existingBin = smartBinRepository.findById(binId);
        
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
    // Changed parameter type from SmartBin to DummySmartBin
    private void updateBinStatus(DummySmartBin bin) {
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