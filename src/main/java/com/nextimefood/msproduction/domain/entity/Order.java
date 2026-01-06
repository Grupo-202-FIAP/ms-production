package com.nextimefood.msproduction.domain.entity;

import com.nextimefood.msproduction.domain.enums.OrderStatus;
import com.nextimefood.msproduction.domain.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private static final int END_ID = 4;
    private UUID id;
    private UUID transactionId;
    private String identifier;
    private BigDecimal totalPrice;
    private Integer totalItems;
    private UUID customerId;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private List<OrderItem> items;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
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
