package com.nextimefood.msproduction.application.usecases.implementation;

import com.nextimefood.msproduction.application.gateways.LoggerPort;
import com.nextimefood.msproduction.application.gateways.OrderRepositoryPort;
import com.nextimefood.msproduction.application.usecases.interfaces.ListOrdersUseCase;
import com.nextimefood.msproduction.infrastructure.persistence.entity.OrderEntity;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ListOrdersUseCaseImpl implements ListOrdersUseCase {

    private final OrderRepositoryPort orderRepository;
    private final LoggerPort logger;

    @Override
    public Page<OrderEntity> execute(Pageable pageable) {
        try {
            logger.info("[ListOrdersUseCase] Listando pedidos com paginação. page={}, size={}",
                    pageable.getPageNumber(), pageable.getPageSize());
            final var orders = orderRepository.findAll(pageable);
            logger.info("[ListOrdersUseCase] Pedidos encontrados: totalElements={}, totalPages={}",
                    orders.getTotalElements(), orders.getTotalPages());
            return orders;
        } catch (Exception e) {
            logger.error("[ListOrdersUseCase] Erro ao listar pedidos", e);
            throw e;
        }
    }
}

