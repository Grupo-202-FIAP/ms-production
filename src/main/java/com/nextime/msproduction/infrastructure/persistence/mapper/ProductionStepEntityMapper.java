package com.nextime.msproduction.infrastructure.persistence.mapper;

import com.nextime.msproduction.domain.ProductionStep;
import com.nextime.msproduction.infrastructure.persistence.entity.ProductionStepEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductionStepEntityMapper {

    public ProductionStepEntity toEntity(ProductionStep step) {
        if (step == null) {
            return null;
        }

        return ProductionStepEntity.builder()
                .id(step.getId())
                .productionOrderId(step.getProductionOrderId())
                .productName(step.getProductName())
                .quantity(step.getQuantity())
                .status(step.getStatus())
                .notes(step.getNotes())
                .startedAt(step.getStartedAt())
                .completedAt(step.getCompletedAt())
                .priority(step.getPriority())
                .build();
    }

    public ProductionStep toDomain(ProductionStepEntity entity) {
        if (entity == null) {
            return null;
        }

        ProductionStep step = new ProductionStep();
        step.setId(entity.getId());
        step.setProductionOrderId(entity.getProductionOrderId());
        step.setProductName(entity.getProductName());
        step.setQuantity(entity.getQuantity());
        step.setStatus(entity.getStatus());
        step.setNotes(entity.getNotes());
        step.setStartedAt(entity.getStartedAt());
        step.setCompletedAt(entity.getCompletedAt());
        step.setPriority(entity.getPriority());

        return step;
    }
}


