package com.nextimefood.msproduction.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextimefood.msproduction.application.gateways.LoggerPort;
import com.nextimefood.msproduction.domain.order.OrderConversionException;
import com.nextimefood.msproduction.domain.entity.Event;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JsonConverter {
    private final ObjectMapper objectMapper;
    private final LoggerPort logger;

    public String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            logger.error("[toJson] Falha para converter objeto para JSON: {}", e);
            throw new OrderConversionException("Falha para converter objeto para JSON", e);
        }
    }

    public Event toEvent(String json) {
        try {
            return objectMapper.readValue(json, Event.class);
        } catch (Exception e) {
            logger.error("[toEvent] Falha para converter JSON para Event: {}", e);
            throw new OrderConversionException("Falha para converter JSON para Event", e);
        }
    }

}
