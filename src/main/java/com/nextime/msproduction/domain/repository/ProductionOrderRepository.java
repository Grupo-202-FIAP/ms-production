package com.nextime.msproduction.domain.repository;

import com.nextime.msproduction.domain.ProductionOrder;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductionOrderRepository {
    ProductionOrder save(ProductionOrder order);
    Optional<ProductionOrder> findById(UUID id);
    Optional<ProductionOrder> findByOrderIdentifier(String orderIdentifier);
    List<ProductionOrder> findAll();
    List<ProductionOrder> findByStatus(com.nextime.msproduction.domain.enums.ProductionStatus status);
    void deleteById(UUID id);
}


