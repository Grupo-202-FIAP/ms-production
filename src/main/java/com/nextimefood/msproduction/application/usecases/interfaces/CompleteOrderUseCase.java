package com.nextimefood.msproduction.application.usecases.interfaces;

import com.nextimefood.msproduction.infrastructure.persistence.entity.OrderEntity;
import java.util.UUID;

public interface CompleteOrderUseCase {
    OrderEntity execute(UUID orderId);
}
