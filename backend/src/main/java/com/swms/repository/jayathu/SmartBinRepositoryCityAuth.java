package com.swms.repository.jayathu;

import com.swms.model.*;
import com.swms.model.jayathu.SmartBinCityAuth;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SmartBinRepositoryCityAuth extends MongoRepository<SmartBinCityAuth, String> {

   
    List<SmartBin> findByLocation(String location);

    List<SmartBin> findByStatus(String status);

    List<SmartBin> findByWasteType(String wasteType);

   
    @Aggregation(pipeline = {
            "{ $group: { _id: '$location', totalWaste: { $sum: '$currentLevel' }, binCount: { $sum: 1 } } }",
            "{ $sort: { totalWaste: -1 } }"
    })
    List<LocationSummary> findWasteByLocation();

    @Aggregation(pipeline = {
            "{ $group: { _id: '$status', count: { $sum: 1 } } }"
    })
    List<StatusSummary> findBinStatusSummary();

    @Aggregation(pipeline = {
            "{ $group: { _id: null, totalWaste: { $sum: '$currentLevel' }, totalCapacity: { $sum: '$capacity' } } }",
            "{ $project: { _id: 0, totalWaste: { $ifNull: ['$totalWaste', 0] }, totalCapacity: { $ifNull: ['$totalCapacity', 0] } } }"
    })
    TotalWasteStats findTotalWasteStats();

   
    @Aggregation(pipeline = {
            "{ $group: { _id: '$wasteType', totalWaste: { $sum: '$currentLevel' }, binCount: { $sum: 1 } } }"
    })
    List<WasteTypeSummary> findWasteByType();

    @Aggregation(pipeline = {
            "{ $match: { wasteType: ?0 } }",
            "{ $group: { _id: '$location', totalWaste: { $sum: '$currentLevel' }, binCount: { $sum: 1 } } }",
            "{ $sort: { totalWaste: -1 } }"
    })
    List<LocationSummary> findWasteByLocationAndWasteType(String wasteType);

   
    @Aggregation(pipeline = {
            "{ $match: { wasteType: 'e-waste' } }",
            "{ $group: { _id: null, totalEWaste: { $sum: '$currentLevel' } } }",
            "{ $project: { _id: 0, totalEWaste: { $ifNull: ['$totalEWaste', 0] } } }"
    })
    List<EWasteStats> findTotalEWaste();

   
    @Aggregation(pipeline = {
            "{ $match: { wasteType: 'recyclable' } }",
            "{ $group: { _id: null, totalRecyclable: { $sum: '$currentLevel' } } }",
            "{ $project: { _id: 0, totalRecyclable: { $ifNull: ['$totalRecyclable', 0] } } }"
    })
    List<RecyclableStats> findTotalRecyclableWaste();

   
    @Aggregation(pipeline = {
            "{ $group: { _id: '$location', totalWaste: { $sum: '$currentLevel' } } }",
            "{ $sort: { totalWaste: -1 } }",
            "{ $limit: 1 }"
    })
    List<LocationSummary> findLocationWithMostWaste();

   
    @Aggregation(pipeline = {
            "{ $match: { wasteType: 'plastic' } }",
            "{ $group: { _id: null, totalPlastic: { $sum: '$currentLevel' } } }",
            "{ $project: { _id: 0, totalPlastic: { $ifNull: ['$totalPlastic', 0] } } }"
    })
    List<PlasticStats> findTotalPlasticWaste();

   
    @Aggregation(pipeline = {
            "{ $match: { wasteType: 'organic' } }",
            "{ $group: { _id: null, totalOrganic: { $sum: '$currentLevel' } } }",
            "{ $project: { _id: 0, totalOrganic: { $ifNull: ['$totalOrganic', 0] } } }"
    })
    List<OrganicStats> findTotalOrganicWaste();

   
    @Aggregation(pipeline = {
            "{ $match: { wasteType: 'metal' } }",
            "{ $group: { _id: null, totalMetal: { $sum: '$currentLevel' } } }",
            "{ $project: { _id: 0, totalMetal: { $ifNull: ['$totalMetal', 0] } } }"
    })
    List<MetalStats> findTotalMetalWaste();

   
    interface LocationSummary {
        String get_id(); 

        Integer getTotalWaste(); 

        Integer getBinCount(); 
    }

    interface StatusSummary {
        String get_id(); 

        Integer getCount(); 
    }

    interface TotalWasteStats {
        Integer getTotalWaste(); 

        Integer getTotalCapacity(); 
    }

    
    interface WasteTypeSummary {
        String get_id(); 

        Integer getTotalWaste(); 

        Integer getBinCount();
    }

    
    interface EWasteStats {
        Integer getTotalEWaste(); 
    }

    
    interface RecyclableStats {
        Integer getTotalRecyclable(); 
    }

   
    interface PlasticStats {
        Integer getTotalPlastic(); 
    }

    
    interface OrganicStats {
        Integer getTotalOrganic(); 
    }

  
    interface MetalStats {
        Integer getTotalMetal(); 
    }
}