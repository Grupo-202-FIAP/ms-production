package com.nextimefood.msproduction.infrastructure.persistence.repository;

import com.nextimefood.msproduction.infrastructure.persistence.entity.OrderEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
}
