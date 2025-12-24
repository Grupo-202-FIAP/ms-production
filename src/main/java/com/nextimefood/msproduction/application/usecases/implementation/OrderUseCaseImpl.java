package com.nextimefood.msproduction.application.usecases.implementation;

import com.nextimefood.msproduction.application.gateways.OrderRepositoryPort;
import com.nextimefood.msproduction.application.usecases.interfaces.OrderUseCase;
import com.nextimefood.msproduction.domain.enums.OrderStatus;
import com.nextimefood.msproduction.infrastructure.persistence.entity.Order;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OrderUseCaseImpl implements OrderUseCase {

    private final OrderRepositoryPort orderRepository;

    @Override
    public Order execute(Order order) {
        order.setStatus(OrderStatus.PREPARING);
        return orderRepository.save(order);
    }

    @Override
    public Order saveOrderAsReceived(Order order) {
        order.setStatus(OrderStatus.RECEIVED);
        return orderRepository.save(order);
    }

    @Override
    public Order cancelOrder(Order order) {
        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }
}
