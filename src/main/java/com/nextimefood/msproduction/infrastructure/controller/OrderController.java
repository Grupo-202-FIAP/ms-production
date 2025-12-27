package com.nextimefood.msproduction.infrastructure.controller;

import com.nextimefood.msproduction.application.gateways.LoggerPort;
import com.nextimefood.msproduction.application.usecases.interfaces.CompleteOrderUseCase;
import com.nextimefood.msproduction.application.usecases.interfaces.ReadyOrderUseCase;
import com.nextimefood.msproduction.infrastructure.persistence.entity.Order;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final LoggerPort logger;
    private final CompleteOrderUseCase completeOrderUseCase;
    private final ReadyOrderUseCase readyOrderUseCase;

    @PutMapping("{orderId}/complete")
    public ResponseEntity<Order> complete(@PathVariable UUID orderId) {
        try {
            logger.info("[OrderController] Atualizando status do pedido para COMPLETED com id={}", orderId);
            final var updatedOrder = completeOrderUseCase.execute(orderId);
            logger.info("[OrderController] Status do pedido atualizado: id={}, novoStatus={}", updatedOrder.getId(), updatedOrder.getStatus());
            return ResponseEntity.ok().body(updatedOrder);
        } catch (Exception e) {
            logger.error("[OrderController] Erro ao atualizar status do pedido para COMPLETED com id={}", orderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("{orderId}/ready")
    public ResponseEntity<Order> ready(@PathVariable UUID orderId) {
        try {
            logger.info("[OrderController] Atualizando status do pedido para READY com id={}", orderId);
            final var updatedOrder = readyOrderUseCase.execute(orderId);
            logger.info("[OrderController] Status do pedido atualizado: id={}, novoStatus={}", updatedOrder.getId(), updatedOrder.getStatus());
            return ResponseEntity.ok().body(updatedOrder);
        } catch (Exception e) {
            logger.error("[OrderController] Erro ao atualizar status do pedido para READY com id={}", orderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
