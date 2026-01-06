package com.nextimefood.msproduction.application.usecases.interfaces;

import com.nextimefood.msproduction.domain.entity.Order;
import com.nextimefood.msproduction.infrastructure.persistence.entity.OrderEntity;

public interface ReceiveOrderUseCase {
    OrderEntity execute(Order order);
}
