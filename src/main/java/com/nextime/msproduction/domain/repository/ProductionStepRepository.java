package com.nextime.msproduction.domain.repository;

import com.nextime.msproduction.domain.ProductionStep;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductionStepRepository {
    ProductionStep save(ProductionStep step);
    Optional<ProductionStep> findById(UUID id);
    List<ProductionStep> findByProductionOrderId(UUID productionOrderId);
    void deleteById(UUID id);
}


