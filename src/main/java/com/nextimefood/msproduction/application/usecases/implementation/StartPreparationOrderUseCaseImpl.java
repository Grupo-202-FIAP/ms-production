package com.nextimefood.msproduction.application.usecases.implementation;

import com.nextimefood.msproduction.application.gateways.LoggerPort;
import com.nextimefood.msproduction.application.gateways.OrderRepositoryPort;
import com.nextimefood.msproduction.application.mapper.OrderMapper;
import com.nextimefood.msproduction.application.usecases.interfaces.StartPreparationOrderUseCase;
import com.nextimefood.msproduction.domain.entity.Order;
import com.nextimefood.msproduction.domain.order.OrderNotFoundException;
import com.nextimefood.msproduction.infrastructure.persistence.entity.OrderEntity;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class StartPreparationOrderUseCaseImpl implements StartPreparationOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final LoggerPort logger;
    private final OrderMapper orderMapper;

    @Override
    public OrderEntity execute(UUID orderId) {
        try {
            final var orderEntity = orderRepository.findById(orderId)
                    .orElseThrow(() -> new OrderNotFoundException(orderId));
            
            final var orderDomain = orderMapper.toDomain(orderEntity);
            orderDomain.startPreparation();
            
            final var updatedOrderEntity = orderMapper.toEntity(orderDomain);
            return orderRepository.save(updatedOrderEntity);
        } catch (OrderNotFoundException e) {
            logger.warn("[StartPreparationOrderUseCase] {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("[StartPreparationOrderUseCase] Erro ao iniciar preparação do pedido com id={}", orderId, e);
            throw e;
        }
    }
}
