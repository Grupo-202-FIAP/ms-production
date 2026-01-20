package com.nextimefood.msproduction.domain.order;

import com.nextimefood.msproduction.domain.enums.OrderStatus;
import java.util.UUID;

public class OrderConflictException extends RuntimeException {
    public OrderConflictException(UUID orderId, OrderStatus currentStatus) {
        super(String.format(
                "[Exception] [Order] Conflito: Pedido com id=%s já existe com status=%s. "
                        + "Não é possível receber um pedido que já está em processamento.",
                orderId, currentStatus)
        );
    }
}

