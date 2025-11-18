package com.nextime.msproduction.infrastructure.persistence.repository;

import com.nextime.msproduction.domain.enums.ProductionStatus;
import com.nextime.msproduction.infrastructure.persistence.mongodb.document.KitchenQueueDocument;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KitchenQueueMongoRepository extends MongoRepository<KitchenQueueDocument, String> {
    Optional<KitchenQueueDocument> findByProductionOrderId(UUID productionOrderId);
    List<KitchenQueueDocument> findByStatus(ProductionStatus status);
}


