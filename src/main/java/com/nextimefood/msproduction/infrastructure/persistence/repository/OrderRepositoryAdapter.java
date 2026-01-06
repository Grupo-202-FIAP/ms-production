package com.nextimefood.msproduction.infrastructure.persistence.repository;

import com.nextimefood.msproduction.application.gateways.LoggerPort;
import com.nextimefood.msproduction.application.gateways.OrderRepositoryPort;
import com.nextimefood.msproduction.domain.order.OrderPersistenceException;
import com.nextimefood.msproduction.infrastructure.persistence.entity.OrderEntity;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class OrderRepositoryAdapter implements OrderRepositoryPort {

    private final OrderRepository orderRepository;
    private final LoggerPort logger;

    @Override
    public OrderEntity save(OrderEntity orderEntity) {
        try {
            return orderRepository.save(orderEntity);
        } catch (Exception e) {
            logger.error("[OrderRepositoryAdapter] Erro ao salvar pedido com id={}", orderEntity.getId(), e);
            throw new OrderPersistenceException("Falha ao salvar pedido no banco de dados", e);
        }
    }

    @Override
    public Optional<OrderEntity> findById(UUID id) {
        try {
            return orderRepository.findById(id);
        } catch (Exception e) {
            logger.error("[OrderRepositoryAdapter] Erro ao buscar pedido com id={}", id, e);
            throw new OrderPersistenceException("Falha ao buscar pedido no banco de dados", e);
        }
    }

    @Override
    public Page<OrderEntity> findAll(Pageable pageable) {
        try {
            return orderRepository.findAll(pageable);
        } catch (Exception e) {
            logger.error("[OrderRepositoryAdapter] Erro ao listar pedidos", e);
            throw new OrderPersistenceException("Falha ao listar pedidos no banco de dados", e);
        }
    }
}
