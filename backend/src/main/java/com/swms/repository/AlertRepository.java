package com.swms.repository;

import com.swms.model.Alert;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlertRepository extends MongoRepository<Alert, String> {
    Optional<Alert> findByBinId(String binId);

    @Query("{ 'isReviewed': 'NO' }")
    List<Alert> findUnreviewedAlerts();

    List<Alert> findAllByOrderByCreatedAtDesc();
}
