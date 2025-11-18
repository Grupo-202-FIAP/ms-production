package com.nextime.msproduction.application.mapper;

import com.nextime.msproduction.application.dto.ProductionOrderResponse;
import com.nextime.msproduction.application.dto.ProductionStepResponse;
import com.nextime.msproduction.domain.ProductionOrder;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ProductionOrderMapper {

    private final ProductionStepMapper productionStepMapper;

    public ProductionOrderMapper(ProductionStepMapper productionStepMapper) {
        this.productionStepMapper = productionStepMapper;
    }

    public ProductionOrderResponse toResponse(ProductionOrder order) {
        if (order == null) {
            return null;
        }

        return ProductionOrderResponse.builder()
                .id(order.getId())
                .orderIdentifier(order.getOrderIdentifier())
                .originalOrderId(order.getOriginalOrderId())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus() != null ? order.getStatus().getStatus() : null)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .steps(order.getSteps() != null
                        ? order.getSteps().stream()
                                .map(productionStepMapper::toResponse)
                                .collect(Collectors.toList())
                        : null)
                .build();
    }
}


