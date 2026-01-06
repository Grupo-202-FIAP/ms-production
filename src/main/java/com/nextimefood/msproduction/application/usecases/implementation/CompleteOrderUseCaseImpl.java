package com.nextimefood.msproduction.application.usecases.implementation;

import com.nextimefood.msproduction.application.gateways.LoggerPort;
import com.nextimefood.msproduction.application.gateways.OrderRepositoryPort;
import com.nextimefood.msproduction.application.mapper.OrderMapper;
import com.nextimefood.msproduction.application.usecases.interfaces.CompleteOrderUseCase;
import com.nextimefood.msproduction.domain.entity.Order;
import com.nextimefood.msproduction.domain.order.OrderNotFoundException;
import com.nextimefood.msproduction.infrastructure.persistence.entity.OrderEntity;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CompleteOrderUseCaseImpl implements CompleteOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final LoggerPort logger;
    private final OrderMapper orderMapper;

    @Override
    public OrderEntity execute(UUID orderId) {
        try {
            final var orderEntity = orderRepository.findById(orderId)
                    .orElseThrow(() -> new OrderNotFoundException(orderId));
            
            final var orderDomain = orderMapper.toDomain(orderEntity);
            orderDomain.complete();
            
            final var updatedOrderEntity = orderMapper.toEntity(orderDomain);
            return orderRepository.save(updatedOrderEntity);
        } catch (OrderNotFoundException e) {
            logger.warn("[CompleteOrderUseCase] {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("[CompleteOrderUseCase] Erro ao completar pedido com id={}", orderId, e);
            throw e;
        }
    }
}
