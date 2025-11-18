package com.nextime.msproduction.domain;

import com.nextime.msproduction.domain.enums.ProductionStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ProductionOrder {
    private UUID id;
    private String orderIdentifier;
    private UUID originalOrderId;
    private BigDecimal totalPrice;
    private ProductionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ProductionStep> steps;

    public ProductionOrder() {
    }

    public ProductionOrder(
            UUID id,
            String orderIdentifier,
            UUID originalOrderId,
            BigDecimal totalPrice,
            ProductionStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            List<ProductionStep> steps) {
        this.id = id;
        this.orderIdentifier = orderIdentifier;
        this.originalOrderId = originalOrderId;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.steps = steps;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getOrderIdentifier() {
        return orderIdentifier;
    }

    public void setOrderIdentifier(String orderIdentifier) {
        this.orderIdentifier = orderIdentifier;
    }

    public UUID getOriginalOrderId() {
        return originalOrderId;
    }

    public void setOriginalOrderId(UUID originalOrderId) {
        this.originalOrderId = originalOrderId;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public ProductionStatus getStatus() {
        return status;
    }

    public void setStatus(ProductionStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<ProductionStep> getSteps() {
        return steps;
    }

    public void setSteps(List<ProductionStep> steps) {
        this.steps = steps;
    }

    public boolean canFinish() {
        return status == ProductionStatus.READY;
    }

    public void finish() {
        if (!canFinish()) {
            throw new IllegalStateException("Order cannot be finished. Current status: " + status);
        }
        this.status = ProductionStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }
}


