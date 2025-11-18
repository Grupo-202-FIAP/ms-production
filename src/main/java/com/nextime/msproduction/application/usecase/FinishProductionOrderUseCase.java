package com.nextime.msproduction.application.usecase;

import com.nextime.msproduction.application.dto.ProductionOrderResponse;
import java.util.UUID;

public interface FinishProductionOrderUseCase {
    ProductionOrderResponse execute(UUID orderId);
}


