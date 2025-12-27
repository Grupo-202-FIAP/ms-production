package com.nextimefood.msproduction.domain.enums;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    PROCESSED("PROCESSED"),
    PENDING("PENDING"),
    EXPIRED("EXPIRED");

    private final String status;

    PaymentStatus(String status) {
        this.status = status;
    }

}
