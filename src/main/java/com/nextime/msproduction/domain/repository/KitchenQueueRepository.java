package com.nextime.msproduction.domain.repository;

import com.nextime.msproduction.domain.KitchenQueue;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface KitchenQueueRepository {
    KitchenQueue save(KitchenQueue queue);
    Optional<KitchenQueue> findById(String id);
    Optional<KitchenQueue> findByProductionOrderId(UUID productionOrderId);
    List<KitchenQueue> findAll();
    List<KitchenQueue> findByStatus(com.nextime.msproduction.domain.enums.ProductionStatus status);
    void deleteById(String id);
}


