package com.nextime.msproduction.infrastructure.persistence.repository;

import com.nextime.msproduction.domain.enums.ProductionStatus;
import com.nextime.msproduction.infrastructure.persistence.entity.ProductionOrderEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductionOrderJpaRepository extends JpaRepository<ProductionOrderEntity, UUID> {
    Optional<ProductionOrderEntity> findByOrderIdentifier(String orderIdentifier);
    List<ProductionOrderEntity> findByStatus(ProductionStatus status);
}


