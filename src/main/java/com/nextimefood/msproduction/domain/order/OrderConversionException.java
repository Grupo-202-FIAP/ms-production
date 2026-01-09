package com.nextimefood.msproduction.domain.order;

public class OrderConversionException extends RuntimeException {
    public OrderConversionException(String message, Throwable cause) {
        super("[Exception] [Order] " + message, cause);
    }
}

