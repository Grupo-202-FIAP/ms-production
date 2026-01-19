package com.nextimefood.msproduction.integration.fixtures;

import com.nextimefood.msproduction.domain.entity.Event;
import com.nextimefood.msproduction.domain.entity.History;
import com.nextimefood.msproduction.domain.entity.Order;
import com.nextimefood.msproduction.domain.enums.EventSource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EventFixture {

    public static Event create(String sagaStatus, Order order) {
        List<History> history = new ArrayList<>();
        history.add(History.builder()
            .source(EventSource.ORCHESTRATOR.getSource())
            .status(sagaStatus)
            .message("Evento de teste")
            .createdAt(LocalDateTime.now())
            .build());

        return Event.builder()
            .id(UUID.randomUUID())
            .transactionId(order.getTransactionId())
            .orderId(order.getId())
            .payload(order)
            .status(sagaStatus)
            .source(EventSource.ORCHESTRATOR.getSource())
            .history(history)
            .createdAt(LocalDateTime.now())
            .build();
    }
}
