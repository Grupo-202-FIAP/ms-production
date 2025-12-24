package com.nextimefood.msproduction.domain.order;

import java.util.UUID;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(UUID orderId) {
        super("[Exception] [Order] Pedido não encontrado: " + orderId);
    }
}
