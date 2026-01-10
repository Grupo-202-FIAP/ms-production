package com.nextimefood.msproduction.application.usecases.interfaces;

import com.nextimefood.msproduction.infrastructure.persistence.entity.OrderEntity;
import java.util.UUID;

public interface StartPreparationOrderUseCase {
    OrderEntity execute(UUID orderId);
}
