package com.nextimefood.msproduction.application.usecases.interfaces;

import com.nextimefood.msproduction.infrastructure.persistence.entity.OrderEntity;
import java.util.UUID;

public interface ReadyOrderUseCase {
    OrderEntity execute(UUID orderId);
}

