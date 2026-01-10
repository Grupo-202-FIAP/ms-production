package com.nextimefood.msproduction.application.gateways;

import com.nextimefood.msproduction.infrastructure.persistence.entity.OrderEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepositoryPort {
    OrderEntity save(OrderEntity orderEntity);

    Optional<OrderEntity> findById(UUID id);

    Page<OrderEntity> findAll(Pageable pageable);
}
