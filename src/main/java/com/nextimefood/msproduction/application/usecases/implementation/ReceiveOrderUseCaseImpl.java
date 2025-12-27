package com.nextimefood.msproduction.application.usecases.implementation;

import com.nextimefood.msproduction.application.gateways.LoggerPort;
import com.nextimefood.msproduction.application.gateways.OrderRepositoryPort;
import com.nextimefood.msproduction.application.usecases.interfaces.ReceiveOrderUseCase;
import com.nextimefood.msproduction.infrastructure.persistence.entity.Order;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ReceiveOrderUseCaseImpl implements ReceiveOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final LoggerPort logger;

    @Override
    public Order execute(Order order) {
        try {
            order.receive();
            return orderRepository.save(order);
        } catch (Exception e) {
            logger.error("[ReceiveOrderUseCase] Erro ao receber pedido com id={}", order.getId(), e);
            throw e;
        }
    }
}
