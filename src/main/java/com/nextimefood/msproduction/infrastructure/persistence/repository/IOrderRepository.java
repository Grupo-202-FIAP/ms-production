package com.nextimefood.msproduction.infrastructure.persistence.repository;

import com.nextimefood.msproduction.infrastructure.persistence.entity.Order;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IOrderRepository extends JpaRepository<Order, UUID> {
}
