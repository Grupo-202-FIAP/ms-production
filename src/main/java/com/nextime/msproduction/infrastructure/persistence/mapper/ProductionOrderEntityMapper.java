package com.nextime.msproduction.infrastructure.persistence.mapper;

import com.nextime.msproduction.domain.ProductionOrder;
import com.nextime.msproduction.infrastructure.persistence.entity.ProductionOrderEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductionOrderEntityMapper {

    public ProductionOrderEntity toEntity(ProductionOrder order) {
        if (order == null) {
            return null;
        }

        return ProductionOrderEntity.builder()
                .id(order.getId())
                .orderIdentifier(order.getOrderIdentifier())
                .originalOrderId(order.getOriginalOrderId())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    public ProductionOrder toDomain(ProductionOrderEntity entity) {
        if (entity == null) {
            return null;
        }

        ProductionOrder order = new ProductionOrder();
        order.setId(entity.getId());
        order.setOrderIdentifier(entity.getOrderIdentifier());
        order.setOriginalOrderId(entity.getOriginalOrderId());
        order.setTotalPrice(entity.getTotalPrice());
        order.setStatus(entity.getStatus());
        order.setCreatedAt(entity.getCreatedAt());
        order.setUpdatedAt(entity.getUpdatedAt());
        // Steps serão carregados separadamente pelo repositório

        return order;
    }
}

