package com.nextimefood.msproduction.integration.fixtures;

import com.nextimefood.msproduction.domain.entity.Order;
import com.nextimefood.msproduction.domain.entity.OrderItem;
import com.nextimefood.msproduction.domain.entity.Product;
import com.nextimefood.msproduction.domain.enums.OrderStatus;
import com.nextimefood.msproduction.domain.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderFixture {

    public static Order create(String paymentStatus) {
        return create(PaymentStatus.valueOf(paymentStatus), OrderStatus.RECEIVED);
    }

    public static Order create(PaymentStatus paymentStatus, OrderStatus orderStatus) {
        Product product = Product.builder()
            .id(UUID.randomUUID())
            .name("Produto Teste")
            .unitPrice(new BigDecimal("50.00"))
            .build();

        OrderItem orderItem = OrderItem.builder()
            .id(UUID.randomUUID())
            .product(product)
            .quantity(2)
            .build();

        List<OrderItem> items = new ArrayList<>();
        items.add(orderItem);

        return Order.builder()
            .id(UUID.randomUUID())
            .transactionId(UUID.randomUUID())
            .identifier("ORDER-" + System.nanoTime())
            .totalPrice(new BigDecimal("100.00"))
            .totalItems(2)
            .customerId(UUID.randomUUID())
            .status(orderStatus)
            .paymentStatus(paymentStatus)
            .items(items)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }
}
