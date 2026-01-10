package com.nextimefood.msproduction.domain.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    RECEIVED("RECEIVED"),
    PREPARING("PREPARING"),
    READY("READY"),
    COMPLETED("COMPLETED"),
    CANCELLED("CANCELLED");

    private final String status;

    OrderStatus(String status) {
        this.status = status;
    }
}
