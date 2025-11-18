package com.nextime.msproduction.infrastructure.persistence.entity;

import com.nextime.msproduction.domain.enums.ProductionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "production_orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductionOrderEntity {
    @Id
    private UUID id;

    @Column(name = "order_identifier", nullable = false, unique = true)
    private String orderIdentifier;

    @Column(name = "original_order_id", nullable = false)
    private UUID originalOrderId;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProductionStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

