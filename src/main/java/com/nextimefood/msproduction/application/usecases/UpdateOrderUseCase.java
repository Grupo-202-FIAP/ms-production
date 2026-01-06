package com.nextimefood.msproduction.application.usecases;

import com.nextimefood.msproduction.domain.enums.OrderStatus;
import com.nextimefood.msproduction.infrastructure.persistence.entity.OrderEntity;
import java.util.UUID;

public interface UpdateOrderUseCase {
    OrderEntity execute(UUID orderID, OrderStatus status);
}
