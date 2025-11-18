package com.nextime.msproduction.domain;

import com.nextime.msproduction.domain.enums.ProductionStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public class ProductionStep {
    private UUID id;
    private UUID productionOrderId;
    private String productName;
    private Integer quantity;
    private ProductionStatus status;
    private String notes;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Integer priority;

    public ProductionStep() {
    }

    public ProductionStep(
            UUID id,
            UUID productionOrderId,
            String productName,
            Integer quantity,
            ProductionStatus status,
            String notes,
            LocalDateTime startedAt,
            LocalDateTime completedAt,
            Integer priority) {
        this.id = id;
        this.productionOrderId = productionOrderId;
        this.productName = productName;
        this.quantity = quantity;
        this.status = status;
        this.notes = notes;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.priority = priority;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProductionOrderId() {
        return productionOrderId;
    }

    public void setProductionOrderId(UUID productionOrderId) {
        this.productionOrderId = productionOrderId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public ProductionStatus getStatus() {
        return status;
    }

    public void setStatus(ProductionStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public void updateStatus(ProductionStatus newStatus) {
        if (newStatus == ProductionStatus.IN_PREPARATION && startedAt == null) {
            this.startedAt = LocalDateTime.now();
        }
        if (newStatus == ProductionStatus.READY && completedAt == null) {
            this.completedAt = LocalDateTime.now();
        }
        this.status = newStatus;
    }
}


