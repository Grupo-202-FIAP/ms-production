package com.nextimefood.msproduction.infrastructure.persistence.repository;

import com.nextimefood.msproduction.application.gateways.LoggerPort;
import com.nextimefood.msproduction.application.gateways.OrderRepositoryPort;
import com.nextimefood.msproduction.domain.order.OrderPersistenceException;
import com.nextimefood.msproduction.infrastructure.persistence.entity.Order;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class OrderRepositoryAdapter implements OrderRepositoryPort {

    private final OrderRepository orderRepository;
    private final LoggerPort logger;

    @Override
    public Order save(Order order) {
        try {
            return orderRepository.save(order);
        } catch (Exception e) {
            logger.error("[OrderRepositoryAdapter] Erro ao salvar pedido com id={}", order.getId(), e);
            throw new OrderPersistenceException("Falha ao salvar pedido no banco de dados", e);
        }
    }

    @Override
    public Optional<Order> findById(UUID id) {
        try {
            return orderRepository.findById(id);
        } catch (Exception e) {
            logger.error("[OrderRepositoryAdapter] Erro ao buscar pedido com id={}", id, e);
            throw new OrderPersistenceException("Falha ao buscar pedido no banco de dados", e);
        }
    }
}
