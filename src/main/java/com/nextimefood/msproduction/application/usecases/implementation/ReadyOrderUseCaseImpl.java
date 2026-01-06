package com.nextimefood.msproduction.application.usecases.implementation;

import com.nextimefood.msproduction.application.gateways.LoggerPort;
import com.nextimefood.msproduction.application.gateways.OrderRepositoryPort;
import com.nextimefood.msproduction.application.mapper.OrderMapper;
import com.nextimefood.msproduction.application.usecases.interfaces.ReadyOrderUseCase;
import com.nextimefood.msproduction.domain.entity.Order;
import com.nextimefood.msproduction.domain.order.OrderNotFoundException;
import com.nextimefood.msproduction.infrastructure.persistence.entity.OrderEntity;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ReadyOrderUseCaseImpl implements ReadyOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final LoggerPort logger;
    private final OrderMapper orderMapper;

    @Override
    public OrderEntity execute(UUID orderId) {
        try {
            final var orderEntity = orderRepository.findById(orderId)
                    .orElseThrow(() -> new OrderNotFoundException(orderId));
            
            final var orderDomain = orderMapper.toDomain(orderEntity);
            orderDomain.ready();
            
            final var updatedOrderEntity = orderMapper.toEntity(orderDomain);
            return orderRepository.save(updatedOrderEntity);
        } catch (OrderNotFoundException e) {
            logger.warn("[ReadyOrderUseCase] {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("[ReadyOrderUseCase] Erro ao marcar pedido como ready com id={}", orderId, e);
            throw e;
        }
    }
}

