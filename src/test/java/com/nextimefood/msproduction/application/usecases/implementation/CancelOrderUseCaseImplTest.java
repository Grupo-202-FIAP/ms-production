package com.nextimefood.msproduction.application.usecases.implementation;

import com.nextimefood.msproduction.application.gateways.LoggerPort;
import com.nextimefood.msproduction.application.gateways.OrderRepositoryPort;
import com.nextimefood.msproduction.application.mapper.OrderMapper;
import com.nextimefood.msproduction.domain.entity.Order;
import com.nextimefood.msproduction.domain.enums.OrderStatus;
import com.nextimefood.msproduction.domain.order.OrderNotFoundException;
import com.nextimefood.msproduction.infrastructure.persistence.entity.OrderEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários para CancelOrderUseCaseImpl")
class CancelOrderUseCaseImplTest {

    @Mock
    private OrderRepositoryPort orderRepository;

    @Mock
    private LoggerPort logger;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private CancelOrderUseCaseImpl cancelOrderUseCase;

    private UUID orderId;
    private OrderEntity orderEntity;
    private Order orderDomain;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        orderEntity = OrderEntity.builder()
                .id(orderId)
                .status(OrderStatus.RECEIVED)
                .build();
        
        orderDomain = Order.builder()
                .id(orderId)
                .status(OrderStatus.RECEIVED)
                .build();
    }

    @Test
    @DisplayName("Deve cancelar pedido com sucesso")
    void deveCancelarPedidoComSucesso() {
        // Given
        OrderEntity updatedOrderEntity = OrderEntity.builder()
                .id(orderId)
                .status(OrderStatus.CANCELLED)
                .build();
        
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderEntity));
        when(orderMapper.toDomain(orderEntity)).thenReturn(orderDomain);
        when(orderMapper.toEntity(any(Order.class))).thenReturn(updatedOrderEntity);
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(updatedOrderEntity);

        // When
        OrderEntity result = cancelOrderUseCase.execute(orderId);

        // Then
        assertNotNull(result);
        assertEquals(OrderStatus.CANCELLED, result.getStatus());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderMapper, times(1)).toDomain(orderEntity);
        verify(orderMapper, times(1)).toEntity(any(Order.class));
        verify(orderRepository, times(1)).save(any(OrderEntity.class));
        verify(logger, never()).warn(anyString(), any());
        verify(logger, never()).error(anyString(), any(), any());
    }

    @Test
    @DisplayName("Deve lançar OrderNotFoundException quando pedido não for encontrado")
    void deveLancarOrderNotFoundExceptionQuandoPedidoNaoForEncontrado() {
        // Given
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // When & Then
        OrderNotFoundException exception = assertThrows(
                OrderNotFoundException.class,
                () -> cancelOrderUseCase.execute(orderId)
        );

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains(orderId.toString()));
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(any(OrderEntity.class));
        verify(logger, times(1)).warn(anyString(), anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção e logar erro quando ocorrer erro genérico")
    void deveLancarExcecaoQuandoOcorrerErroGenerico() {
        // Given
        RuntimeException genericException = new RuntimeException("Erro genérico");
        when(orderRepository.findById(orderId)).thenThrow(genericException);

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> cancelOrderUseCase.execute(orderId)
        );

        assertEquals(genericException, exception);
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(any(OrderEntity.class));
        verify(logger, times(1)).error(anyString(), eq(orderId), eq(genericException));
    }

    @Test
    @DisplayName("Deve lançar exceção quando save falhar")
    void deveLancarExcecaoQuandoSaveFalhar() {
        // Given
        RuntimeException saveException = new RuntimeException("Erro ao salvar");
        OrderEntity updatedOrderEntity = OrderEntity.builder()
                .id(orderId)
                .status(OrderStatus.CANCELLED)
                .build();
        
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderEntity));
        when(orderMapper.toDomain(orderEntity)).thenReturn(orderDomain);
        when(orderMapper.toEntity(any(Order.class))).thenReturn(updatedOrderEntity);
        when(orderRepository.save(any(OrderEntity.class))).thenThrow(saveException);

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> cancelOrderUseCase.execute(orderId)
        );

        assertEquals(saveException, exception);
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderMapper, times(1)).toDomain(orderEntity);
        verify(orderMapper, times(1)).toEntity(any(Order.class));
        verify(orderRepository, times(1)).save(any(OrderEntity.class));
        verify(logger, times(1)).error(anyString(), eq(orderId), eq(saveException));
    }
}

