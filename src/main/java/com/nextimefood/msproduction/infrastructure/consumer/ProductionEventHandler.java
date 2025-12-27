package com.nextimefood.msproduction.infrastructure.consumer;

import static com.nextimefood.msproduction.domain.enums.EventSource.PRODUCTION;

import com.nextimefood.msproduction.application.gateways.LoggerPort;
import com.nextimefood.msproduction.application.usecases.interfaces.CancelOrderUseCase;
import com.nextimefood.msproduction.application.usecases.interfaces.ReceiveOrderUseCase;
import com.nextimefood.msproduction.application.usecases.interfaces.StartPreparationOrderUseCase;
import com.nextimefood.msproduction.domain.enums.SagaStatus;
import com.nextimefood.msproduction.domain.order.OrderEventNotSupportedException;
import com.nextimefood.msproduction.infrastructure.persistence.entity.Event;
import com.nextimefood.msproduction.infrastructure.persistence.entity.History;
import com.nextimefood.msproduction.infrastructure.persistence.entity.Order;
import com.nextimefood.msproduction.infrastructure.producer.SagaProducer;
import com.nextimefood.msproduction.utils.JsonConverter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ProductionEventHandler {

    private final LoggerPort logger;
    private final ReceiveOrderUseCase receiveOrderUseCase;
    private final StartPreparationOrderUseCase startPreparationOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final SagaProducer sagaProducer;
    private final JsonConverter jsonConverter;

    public void handle(Event event) {
        try {
            switch (SagaStatus.valueOf(event.getStatus())) {
                case SUCCESS -> handleSuccess(event);
                case FAIL, ROLLBACK_PENDING -> handleRollback(event);
                default -> throw new OrderEventNotSupportedException(event.getStatus());
            }
        } catch (Exception ex) {
            logger.error("[ProductionEventHandler] Erro ao processar evento, iniciando rollback. transactionId={}", event.getTransactionId(), ex);
            publishRollback(event, ex.getMessage());
            throw ex;
        }
    }

    private void handleSuccess(Event event) {
        final var order = event.getPayload();
        final var paymentStatus = order.getPaymentStatus();

        switch (paymentStatus) {
            case PENDING -> {
                logger.info("[ProductionEventHandler] Pedido recebido");
                final var received = receiveOrderUseCase.execute(order);
                publishNext(event, received, "Pedido recebido");
            }
            case PROCESSED -> {
                logger.info("[ProductionEventHandler] Iniciando produção");
                final var started = startPreparationOrderUseCase.execute(order.getId());
                publishNext(event, started, "Pedido em produção");
            }
            default -> throw new OrderEventNotSupportedException(paymentStatus.name());
        }
    }

    private void handleRollback(Event event) {
        logger.info("[ProductionEventHandler] Executando rollback do pedido. orderId={}", event.getOrderId());

        final var canceled = cancelOrderUseCase.execute(event.getOrderId());
        publishNext(event, canceled, "Pedido cancelado (rollback)");
    }

    private void publishNext(Event event, Order order, String message) {
        final var updatedEvent = updateEvent(event, order, message, SagaStatus.SUCCESS);
        sagaProducer.sendMessage(jsonConverter.toJson(updatedEvent));
    }

    private void publishRollback(Event event, String reason) {
        try {
            final var canceled = cancelOrderUseCase.execute(event.getOrderId());
            final var rollbackEvent = updateEvent(event, canceled, "Rollback iniciado: " + reason, SagaStatus.ROLLBACK_PENDING);

            sagaProducer.sendMessage(jsonConverter.toJson(rollbackEvent));
        } catch (Exception ex) {
            logger.error("[ProductionEventHandler] Falha crítica ao publicar rollback. transactionId={}", event.getTransactionId(), ex);
            throw ex;
        }
    }

    private Event updateEvent(Event event, Order order, String message, SagaStatus sagaStatus) {
        final var history = new ArrayList<>(event.getHistory());

        history.add(new History(PRODUCTION.getSource(), order.getStatus().name(), message, LocalDateTime.now()));

        event.setPayload(order);
        event.setHistory(history);
        event.setStatus(sagaStatus.name());
        event.setSource(PRODUCTION.getSource());

        return event;
    }
}
