package com.nextimefood.msproduction.application.usecases.implementation;

import com.nextimefood.msproduction.application.gateways.LoggerPort;
import com.nextimefood.msproduction.application.gateways.OrderRepositoryPort;
import com.nextimefood.msproduction.application.usecases.interfaces.CancelOrderUseCase;
import com.nextimefood.msproduction.domain.order.OrderNotFoundException;
import com.nextimefood.msproduction.infrastructure.persistence.entity.Order;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CancelOrderUseCaseImpl implements CancelOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final LoggerPort logger;

    @Override
    public Order execute(UUID orderId) {
        try {
            final var order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new OrderNotFoundException(orderId));
            order.cancel();
            return orderRepository.save(order);
        } catch (OrderNotFoundException e) {
            logger.warn("[CancelOrderUseCase] {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("[CancelOrderUseCase] Erro ao cancelar pedido com id={}", orderId, e);
            throw e;
        }
    }
}
