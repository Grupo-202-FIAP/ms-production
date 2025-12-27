package com.nextimefood.msproduction.application.usecases;

import com.nextimefood.msproduction.domain.enums.OrderStatus;
import com.nextimefood.msproduction.infrastructure.persistence.entity.Order;
import java.util.UUID;

public interface UpdateOrderUseCase {
    Order execute(UUID orderID, OrderStatus status);
}
