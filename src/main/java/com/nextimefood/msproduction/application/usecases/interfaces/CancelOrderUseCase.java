package com.nextimefood.msproduction.application.usecases.interfaces;

import com.nextimefood.msproduction.infrastructure.persistence.entity.Order;
import java.util.UUID;

public interface CancelOrderUseCase {
    Order execute(UUID orderId);
}
