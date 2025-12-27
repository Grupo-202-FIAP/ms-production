package com.nextimefood.msproduction.application.usecases.interfaces;

import com.nextimefood.msproduction.infrastructure.persistence.entity.Order;

public interface ReceiveOrderUseCase {
    Order execute(Order order);
}
