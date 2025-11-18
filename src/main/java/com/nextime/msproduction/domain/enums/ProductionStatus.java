package com.nextime.msproduction.domain.enums;

public enum ProductionStatus {
    PENDING("PENDING"),
    IN_PREPARATION("IN_PREPARATION"),
    READY("READY"),
    COMPLETED("COMPLETED"),
    CANCELLED("CANCELLED");

    private final String status;

    ProductionStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}


