package com.nextimefood.msproduction.infrastructure.persistence.entity;

import com.nextimefood.msproduction.domain.enums.OrderStatus;
import com.nextimefood.msproduction.domain.enums.PaymentStatus;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Order {
    private static final int END_ID = 4;
    @Id
    private UUID id;
    private UUID transactionId;
    private String identifier;
    private UUID customerId;
    private PaymentStatus paymentStatus;
    private OrderStatus status;
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
