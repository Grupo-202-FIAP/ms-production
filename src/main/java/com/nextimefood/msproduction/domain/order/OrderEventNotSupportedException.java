package com.nextimefood.msproduction.domain.order;

public class OrderEventNotSupportedException extends RuntimeException {
    public OrderEventNotSupportedException(String eventStatus) {
        super("[Exception] [Order] Tipo de evento não suportado: " + eventStatus);
    }
}

