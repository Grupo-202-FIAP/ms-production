package com.nextimefood.msproduction.application.gateways;

import com.nextimefood.msproduction.infrastructure.persistence.entity.Order;

public interface OrderRepositoryPort {
    Order save(Order order);
}
