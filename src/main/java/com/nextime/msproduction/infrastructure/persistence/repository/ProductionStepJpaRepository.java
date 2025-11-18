package com.nextime.msproduction.infrastructure.persistence.repository;

import com.nextime.msproduction.infrastructure.persistence.entity.ProductionStepEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductionStepJpaRepository extends JpaRepository<ProductionStepEntity, UUID> {
    List<ProductionStepEntity> findByProductionOrderId(UUID productionOrderId);
}


