package com.nextimefood.msproduction.domain.order;

import com.nextimefood.msproduction.domain.enums.OrderStatus;

public class OrderNotFoundStatusException extends RuntimeException {
    public OrderNotFoundStatusException(OrderStatus status) {
        super("[Exception] [Order] Pedido não encontrado com o status: " + status);
    }
}
