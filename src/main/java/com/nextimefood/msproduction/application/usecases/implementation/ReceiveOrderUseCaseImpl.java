package com.nextimefood.msproduction.application.usecases.implementation;

import com.nextimefood.msproduction.application.gateways.LoggerPort;
import com.nextimefood.msproduction.application.gateways.OrderRepositoryPort;
import com.nextimefood.msproduction.application.mapper.OrderMapper;
import com.nextimefood.msproduction.application.usecases.interfaces.ReceiveOrderUseCase;
import com.nextimefood.msproduction.domain.entity.Order;
import com.nextimefood.msproduction.infrastructure.persistence.entity.OrderEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ReceiveOrderUseCaseImpl implements ReceiveOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final LoggerPort logger;
    private final OrderMapper mapper;

    //TODO: Verificar se o pedido já existe no banco
    @Override
    public OrderEntity execute(Order order) {
        try {
            order.receive();
            final var orderEntity = mapper.toEntity(order);
            return orderRepository.save(orderEntity);
        } catch (Exception e) {
            logger.error("[ReceiveOrderUseCase] Erro ao receber pedido com id={}", order.getId(), e);
            throw e;
        }
    }
}
