package com.nextimefood.msproduction.domain.enums;

import lombok.Getter;

@Getter
public enum EventSource {
    ORCHESTRATOR("ORCHESTRATOR"),
    PAYMENT("PAYMENT"),
    PRODUCTION("PRODUCTION");

    private final String source;

    EventSource(String source) {
        this.source = source;
    }
}
