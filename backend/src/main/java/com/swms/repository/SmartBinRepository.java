package com.swms.repository;

import com.swms.model.SmartBin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SmartBinRepository extends MongoRepository<SmartBin, String> {
    Optional<SmartBin> findByBinId(String binId);

    List<SmartBin> findByStatus(String status);

    @Query("{ 'status': 'FULL' }")
    List<SmartBin> findAllFullBins();

    @Query("{ 'status': 'EMPTY' }")
    List<SmartBin> findAllEmptyBins();

    @Query("{ 'status': 'HALF_FULL' }")
    List<SmartBin> findAllHalfFullBins();

    List<SmartBin> findByLocation(String location);
}