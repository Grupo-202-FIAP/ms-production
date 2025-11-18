package com.nextime.msproduction.application.mapper;

import com.nextime.msproduction.application.dto.ProductionStepResponse;
import com.nextime.msproduction.domain.ProductionStep;
import org.springframework.stereotype.Component;

@Component
public class ProductionStepMapper {

    public ProductionStepResponse toResponse(ProductionStep step) {
        if (step == null) {
            return null;
        }

        return ProductionStepResponse.builder()
                .id(step.getId())
                .productionOrderId(step.getProductionOrderId())
                .productName(step.getProductName())
                .quantity(step.getQuantity())
                .status(step.getStatus() != null ? step.getStatus().getStatus() : null)
                .notes(step.getNotes())
                .startedAt(step.getStartedAt())
                .completedAt(step.getCompletedAt())
                .priority(step.getPriority())
                .build();
    }
}


