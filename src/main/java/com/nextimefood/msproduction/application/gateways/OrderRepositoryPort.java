package com.nextimefood.msproduction.application.gateways;

import com.nextimefood.msproduction.infrastructure.persistence.entity.Order;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepositoryPort {
    Order save(Order order);

    Optional<Order> findById(UUID id);
}
