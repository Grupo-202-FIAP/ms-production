package com.nextime.msproduction.application.usecase.impl;

import com.nextime.msproduction.application.dto.ProductionOrderResponse;
import com.nextime.msproduction.application.mapper.ProductionOrderMapper;
import com.nextime.msproduction.application.usecase.FinishProductionOrderUseCase;
import com.nextime.msproduction.domain.ProductionOrder;
import com.nextime.msproduction.domain.repository.ProductionOrderRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class FinishProductionOrderUseCaseImpl implements FinishProductionOrderUseCase {

    private final ProductionOrderRepository productionOrderRepository;
    private final ProductionOrderMapper productionOrderMapper;

    public FinishProductionOrderUseCaseImpl(
            ProductionOrderRepository productionOrderRepository,
            ProductionOrderMapper productionOrderMapper) {
        this.productionOrderRepository = productionOrderRepository;
        this.productionOrderMapper = productionOrderMapper;
    }

    @Override
    public ProductionOrderResponse execute(UUID orderId) {
        ProductionOrder order = productionOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Production order not found: " + orderId));

        order.finish();
        ProductionOrder savedOrder = productionOrderRepository.save(order);
        return productionOrderMapper.toResponse(savedOrder);
    }
}


