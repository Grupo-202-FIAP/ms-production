package com.nextimefood.msproduction.application.usecases.interfaces;


import com.nextimefood.msproduction.infrastructure.persistence.entity.Order;

public interface OrderUseCase {
    Order execute(Order order);

    Order saveOrderAsReceived(Order order);

    Order cancelOrder(Order order);
}
