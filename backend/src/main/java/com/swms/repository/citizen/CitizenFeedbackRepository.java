package com.swms.repository.citizen;

import com.swms.model.citizen.CitizenFeedback;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CitizenFeedbackRepository extends MongoRepository<CitizenFeedback, String> {
    List<CitizenFeedback> findByCitizenId(String citizenId);
    List<CitizenFeedback> findByRequestId(String requestId);
}