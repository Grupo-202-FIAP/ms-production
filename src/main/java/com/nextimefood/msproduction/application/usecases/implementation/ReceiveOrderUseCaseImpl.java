package com.nextimefood.msproduction.application.usecases.implementation;

import com.nextimefood.msproduction.application.gateways.LoggerPort;
import com.nextimefood.msproduction.application.gateways.OrderRepositoryPort;
import com.nextimefood.msproduction.application.mapper.OrderMapper;
import com.nextimefood.msproduction.application.usecases.interfaces.ReceiveOrderUseCase;
import com.nextimefood.msproduction.domain.entity.Order;
import com.nextimefood.msproduction.domain.enums.OrderStatus;
import com.nextimefood.msproduction.domain.order.OrderConflictException;
import com.nextimefood.msproduction.infrastructure.persistence.entity.OrderEntity;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ReceiveOrderUseCaseImpl implements ReceiveOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final LoggerPort logger;
    private final OrderMapper mapper;

    @Override
    public OrderEntity execute(Order order) {
        try {
            final Optional<OrderEntity> existingOrder = orderRepository.findById(order.getId());

            if (existingOrder.isPresent()) {
                final var existing = existingOrder.get();
                if (!OrderStatus.RECEIVED.equals(existing.getStatus())) {
                    throw new OrderConflictException(order.getId(), existing.getStatus());
                }
                // Se já está RECEIVED, pode atualizar normalmente
                logger.info("[ReceiveOrderUseCase] Pedido com id={} já existe com status RECEIVED. Atualizando registro.", order.getId());
            }

            order.receive();
            final var orderEntity = mapper.toEntity(order);
            return orderRepository.save(orderEntity);
        } catch (OrderConflictException e) {
            logger.warn("[ReceiveOrderUseCase] {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("[ReceiveOrderUseCase] Erro ao receber pedido com id={}", order.getId(), e);
            throw e;
        }
    }
}
