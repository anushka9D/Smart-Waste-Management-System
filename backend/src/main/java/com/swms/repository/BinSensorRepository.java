package com.swms.repository;

import com.swms.model.BinSensor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BinSensorRepository extends MongoRepository<BinSensor, String> {
    Optional<BinSensor> findBySensorId(String sensorId);

    Optional<BinSensor> findByBinId(String binId);

    @Query("{ 'type': 'FAULTY' }")
    List<BinSensor> findAllFaultySensors();
}
