package com.nextimefood.msproduction.domain.order;

public class OrderListEmptyException extends RuntimeException {
    public OrderListEmptyException() {
        super("[Exception] [Order] A lista de items não pode ser vazia");
    }
}
