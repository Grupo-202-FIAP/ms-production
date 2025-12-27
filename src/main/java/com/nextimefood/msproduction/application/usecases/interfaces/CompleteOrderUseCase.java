package com.nextimefood.msproduction.application.usecases.interfaces;

import com.nextimefood.msproduction.infrastructure.persistence.entity.Order;
import java.util.UUID;

public interface CompleteOrderUseCase {
    Order execute(UUID orderId);
}
