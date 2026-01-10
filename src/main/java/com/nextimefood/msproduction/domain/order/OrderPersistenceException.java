package com.nextimefood.msproduction.domain.order;

public class OrderPersistenceException extends RuntimeException {
    public OrderPersistenceException(String message, Throwable cause) {
        super("[Exception] [Order] " + message, cause);
    }
}

