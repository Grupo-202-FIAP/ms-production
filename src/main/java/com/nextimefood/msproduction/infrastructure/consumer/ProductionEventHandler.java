package com.nextimefood.msproduction.infrastructure.consumer;

import static com.nextimefood.msproduction.domain.enums.OrderStatus.RECEIVED;
import static com.nextimefood.msproduction.domain.enums.PaymentStatus.EXPIRED;
import static com.nextimefood.msproduction.domain.enums.PaymentStatus.PROCESSED;

import com.nextimefood.msproduction.application.gateways.LoggerPort;
import com.nextimefood.msproduction.application.usecases.interfaces.OrderUseCase;
import com.nextimefood.msproduction.infrastructure.persistence.entity.Event;
import com.nextimefood.msproduction.infrastructure.persistence.entity.History;
import com.nextimefood.msproduction.infrastructure.persistence.entity.Order;
import com.nextimefood.msproduction.infrastructure.producer.SagaProducer;
import com.nextimefood.msproduction.utils.JsonConverter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ProductionEventHandler {

    private final LoggerPort logger;
    private final OrderUseCase orderUseCase;
    private final SagaProducer sagaProducer;
    private final JsonConverter jsonConverter;

    private static Event updateHistory(Event event, Order order, String message) {
        final var history = event.getHistory();
        final var updatedHistory = new History("ms-production", order.getStatus().toString(), message, event.getCreatedAt());
        history.add(updatedHistory);
        event.setHistory(history);
        return event;
    }

    public void handle(Event event) {
        final var order = event.getPayload();
        switch (event.getStatus()) {
            case "ROLLBACK" -> {
                final var canceledOrder = orderUseCase.cancelOrder(order);
                final var updatedEvent = updateHistory(event, canceledOrder, "Pedido Recebido");
                sagaProducer.sendMessage(jsonConverter.toJson(updatedEvent));
            }
            case PAYMENT -> {
                handlePaymentEvent(order);
            }
            default -> throw new RuntimeException("Tipo de evento não suportado");
        }
    }

    private Order handleOrderEvent(Order order) {
        final var status = order.getStatus();

        if (RECEIVED.equals(status)) {
            logger.info("[ProductionEventHandler] Ordem recebida, salvando");
            return orderUseCase.saveOrderAsReceived(order);
        }

        throw new RuntimeException("Status de ordem não suportado");
    }

    private Order handlePaymentEvent(Order order) {
        final var status = order.getPaymentStatus();

        if (PROCESSED.equals(status)) {
            logger.info("[ProductionEventHandler] Pagamento aprovado, iniciando producao");
            orderUseCase.execute(order);
            return;
        }

        if (EXPIRED.equals(status)) {
            logger.info("[ProductionEventHandler] Pagamento recusado, cancelando pedido");
            orderUseCase.cancelOrder(order);
            return;
        }

        throw new RuntimeException("Evento de pagamento expirado ignorado");
    }

}
