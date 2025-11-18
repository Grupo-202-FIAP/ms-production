package com.nextime.msproduction.domain;

import com.nextime.msproduction.domain.enums.ProductionStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class KitchenQueue {
    private String id;
    private UUID productionOrderId;
    private String orderIdentifier;
    private ProductionStatus status;
    private Integer priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<QueueItem> items;

    public KitchenQueue() {
    }

    public KitchenQueue(
            String id,
            UUID productionOrderId,
            String orderIdentifier,
            ProductionStatus status,
            Integer priority,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            List<QueueItem> items) {
        this.id = id;
        this.productionOrderId = productionOrderId;
        this.orderIdentifier = orderIdentifier;
        this.status = status;
        this.priority = priority;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.items = items;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UUID getProductionOrderId() {
        return productionOrderId;
    }

    public void setProductionOrderId(UUID productionOrderId) {
        this.productionOrderId = productionOrderId;
    }

    public String getOrderIdentifier() {
        return orderIdentifier;
    }

    public void setOrderIdentifier(String orderIdentifier) {
        this.orderIdentifier = orderIdentifier;
    }

    public ProductionStatus getStatus() {
        return status;
    }

    public void setStatus(ProductionStatus status) {
        this.status = status;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
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

    public List<QueueItem> getItems() {
        return items;
    }

    public void setItems(List<QueueItem> items) {
        this.items = items;
    }

    public static class QueueItem {
        private String productName;
        private Integer quantity;
        private ProductionStatus status;

        public QueueItem() {
        }

        public QueueItem(String productName, Integer quantity, ProductionStatus status) {
            this.productName = productName;
            this.quantity = quantity;
            this.status = status;
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
    }
}


