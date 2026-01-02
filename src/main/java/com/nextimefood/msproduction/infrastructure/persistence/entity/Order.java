package com.nextimefood.msproduction.infrastructure.persistence.entity;

import com.nextimefood.msproduction.domain.enums.OrderStatus;
import com.nextimefood.msproduction.domain.enums.PaymentStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    private static final int END_ID = 4;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID transactionId;
    private String identifier;
    private UUID customerId;
    private PaymentStatus paymentStatus;
    private OrderStatus status;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void receive() {
        this.status = OrderStatus.RECEIVED;
    }

    public void cancel() {
        this.status = OrderStatus.CANCELLED;
    }

    public void startPreparation() {
        this.status = OrderStatus.PREPARING;
    }

    public void ready() {
        this.status = OrderStatus.READY;
    }

    public void complete() {
        this.status = OrderStatus.COMPLETED;
    }

}
