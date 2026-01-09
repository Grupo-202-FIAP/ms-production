package com.nextimefood.msproduction.infrastructure.messaging.consumer;

import static com.nextimefood.msproduction.domain.enums.EventSource.PRODUCTION;

import com.nextimefood.msproduction.application.gateways.LoggerPort;
import com.nextimefood.msproduction.application.gateways.OrderRepositoryPort;
import com.nextimefood.msproduction.application.usecases.interfaces.CancelOrderUseCase;
import com.nextimefood.msproduction.application.usecases.interfaces.ReceiveOrderUseCase;
import com.nextimefood.msproduction.application.usecases.interfaces.StartPreparationOrderUseCase;
import com.nextimefood.msproduction.domain.enums.SagaStatus;
import com.nextimefood.msproduction.domain.order.OrderConflictException;
import com.nextimefood.msproduction.domain.order.OrderEventNotSupportedException;
import com.nextimefood.msproduction.domain.entity.Event;
import com.nextimefood.msproduction.domain.entity.History;
import com.nextimefood.msproduction.infrastructure.messaging.producer.SagaProducer;
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
    private final OrderRepositoryPort orderRepository;

    public void handle(Event event) {
        try {
            switch (SagaStatus.valueOf(event.getStatus())) {
                case SUCCESS -> handleSuccess(event);
                case FAIL, ROLLBACK_PENDING -> handleRollback(event);
                default -> throw new OrderEventNotSupportedException(event.getStatus());
            }
        } catch (OrderConflictException ex) {
            logger.warn("[ProductionEventHandler] Conflito detectado. Não será disparado callback. transactionId={}, message={}", event.getTransactionId(), ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("[ProductionEventHandler] Erro ao processar evento, iniciando rollback. transactionId={}", event.getTransactionId(), ex);
            publishCallback(event, "Erro ao processar evento, iniciando rollback", SagaStatus.ROLLBACK_PENDING);
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
                event.getPayload().setStatus(received.getStatus());
                publishCallback(event, "Pedido recebido", SagaStatus.SUCCESS);
            }
            case PROCESSED -> {
                logger.info("[ProductionEventHandler] Iniciando produção");
                final var started = startPreparationOrderUseCase.execute(order.getId());
                event.getPayload().setStatus(started.getStatus());
                publishCallback(event, "Pedido em produção", SagaStatus.SUCCESS);
            }
            default -> throw new OrderEventNotSupportedException(paymentStatus.name());
        }
    }

    private void handleRollback(Event event) {
        logger.info("[ProductionEventHandler] Executando rollback do pedido. orderId={}", event.getOrderId());
        final var orderOptional = orderRepository.findById(event.getOrderId());
        if (orderOptional.isEmpty()) {
            publishCallback(event, "Pedido cancelado (rollback)", SagaStatus.FAIL);
        } else {
            final var canceled = cancelOrderUseCase.execute(event.getOrderId());
            event.getPayload().setStatus(canceled.getStatus());
            publishCallback(event, "Pedido cancelado (rollback)", SagaStatus.FAIL);
        }
    }

    private void publishCallback(Event event, String message, SagaStatus sagaStatus) {
        final var updatedEvent = updateEvent(event, message, sagaStatus);
        sagaProducer.sendMessage(jsonConverter.toJson(updatedEvent));
    }

    private Event updateEvent(Event event, String message, SagaStatus sagaStatus) {
        final var history = new ArrayList<>(event.getHistory());

        history.add(new History(PRODUCTION.getSource(), event.getPayload().getStatus().name(), message, LocalDateTime.now()));

//        event.setPayload(order);
        event.setHistory(history);
        event.setStatus(sagaStatus.name());
        event.setSource(PRODUCTION.getSource());

        return event;
    }
}
