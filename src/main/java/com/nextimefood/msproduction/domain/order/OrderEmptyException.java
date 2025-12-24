package com.nextimefood.msproduction.domain.order;

public class OrderEmptyException extends RuntimeException {
    public OrderEmptyException() {
        super("[Exception] [Order] Nenhum pedido encontrado");
    }
}
