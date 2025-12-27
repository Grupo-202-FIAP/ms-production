package com.nextimefood.msproduction.infrastructure.consumer;

import com.nextimefood.msproduction.application.gateways.LoggerPort;
import com.nextimefood.msproduction.domain.order.OrderEventNotSupportedException;
import com.nextimefood.msproduction.utils.JsonConverter;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ProductionQueueListener {

    private final LoggerPort logger;
    private final JsonConverter jsonConverter;
    private final ProductionEventHandler eventHandler;

    @SqsListener("${sqs.queues.production-queue.name}")
    public void consumeMessage(String payload) {
        try {
            logger.info("[ProductionQueueListener] Mensagem recebida");
            final var event = jsonConverter.toEvent(payload);
            eventHandler.handle(event);
        } catch (OrderEventNotSupportedException ex) {
            logger.warn("[ProductionQueueListener] Evento ignorado por regra de negocio: {}", ex.getMessage());
        } catch (Exception ex) {
            logger.error("[ProductionQueueListener] Erro tecnico ao processar mensagem", ex);
            throw ex;
        }
    }
}
