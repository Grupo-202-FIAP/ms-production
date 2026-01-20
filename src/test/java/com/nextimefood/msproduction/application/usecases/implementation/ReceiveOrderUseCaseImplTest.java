package com.nextimefood.msproduction.application.usecases.implementation;

import com.nextimefood.msproduction.application.gateways.LoggerPort;
import com.nextimefood.msproduction.application.gateways.OrderRepositoryPort;
import com.nextimefood.msproduction.application.mapper.OrderMapper;
import com.nextimefood.msproduction.domain.entity.Order;
import com.nextimefood.msproduction.domain.enums.OrderStatus;
import com.nextimefood.msproduction.domain.order.OrderConflictException;
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
@DisplayName("Testes unitários para ReceiveOrderUseCaseImpl")
class ReceiveOrderUseCaseImplTest {

    @Mock
    private OrderRepositoryPort orderRepository;

    @Mock
    private LoggerPort logger;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private ReceiveOrderUseCaseImpl receiveOrderUseCase;

    private Order orderDomain;
    private OrderEntity orderEntity;

    @BeforeEach
    void setUp() {
        UUID orderId = UUID.randomUUID();
        orderDomain = Order.builder()
                .id(orderId)
                .status(OrderStatus.RECEIVED)
                .build();
        
        orderEntity = OrderEntity.builder()
                .id(orderId)
                .status(OrderStatus.RECEIVED)
                .build();
    }

    @Test
    @DisplayName("Deve receber pedido com sucesso")
    void deveReceberPedidoComSucesso() {
        // Given
        when(orderMapper.toEntity(any(Order.class))).thenReturn(orderEntity);
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(orderEntity);

        // When
        OrderEntity result = receiveOrderUseCase.execute(orderDomain);

        // Then
        assertNotNull(result);
        assertEquals(OrderStatus.RECEIVED, result.getStatus());
        verify(orderMapper, times(1)).toEntity(any(Order.class));
        verify(orderRepository, times(1)).save(any(OrderEntity.class));
        verify(logger, never()).error(anyString(), any(), any());
    }

    @Test
    @DisplayName("Deve lançar exceção e logar erro quando ocorrer erro ao salvar")
    void deveLancarExcecaoQuandoOcorrerErroAoSavar() {
        // Given
        RuntimeException saveException = new RuntimeException("Erro ao salvar");
        when(orderMapper.toEntity(any(Order.class))).thenReturn(orderEntity);
        when(orderRepository.save(any(OrderEntity.class))).thenThrow(saveException);

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> receiveOrderUseCase.execute(orderDomain)
        );

        assertEquals(saveException, exception);
        verify(orderMapper, times(1)).toEntity(any(Order.class));
        verify(orderRepository, times(1)).save(any(OrderEntity.class));
        verify(logger, times(1)).error(anyString(), eq(orderDomain.getId()), eq(saveException));
    }

    @Test
    @DisplayName("Deve lançar exceção quando método receive falhar")
    void deveLancarExcecaoQuandoMetodoReceiveFalhar() {
        // Given
        RuntimeException receiveException = new RuntimeException("Erro ao receber pedido");
        Order orderDomainWithError = spy(orderDomain);
        UUID orderId = orderDomainWithError.getId();
        doThrow(receiveException).when(orderDomainWithError).receive();

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> receiveOrderUseCase.execute(orderDomainWithError)
        );

        assertEquals(receiveException, exception);
        verify(orderDomainWithError, times(1)).receive();
        verify(orderMapper, never()).toEntity(any(Order.class));
        verify(orderRepository, never()).save(any(OrderEntity.class));
        verify(logger, times(1)).error(anyString(), eq(orderId), eq(receiveException));
    }

    @Test
    @DisplayName("Deve atualizar pedido quando já existe com status RECEIVED")
    void deveAtualizarPedidoQuandoJaExisteComStatusReceived() {
        // Given
        UUID orderId = UUID.randomUUID();
        Order newOrder = Order.builder()
                .id(orderId)
                .status(OrderStatus.RECEIVED)
                .build();

        OrderEntity existingEntity = OrderEntity.builder()
                .id(orderId)
                .status(OrderStatus.RECEIVED)
                .build();

        OrderEntity updatedEntity = OrderEntity.builder()
                .id(orderId)
                .status(OrderStatus.RECEIVED)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingEntity));
        when(orderMapper.toEntity(any(Order.class))).thenReturn(updatedEntity);
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(updatedEntity);

        // When
        OrderEntity result = receiveOrderUseCase.execute(newOrder);

        // Then
        assertNotNull(result);
        assertEquals(OrderStatus.RECEIVED, result.getStatus());
        verify(orderRepository, times(1)).findById(orderId);
        verify(logger, times(1)).info(anyString(), eq(orderId));
        verify(orderMapper, times(1)).toEntity(any(Order.class));
        verify(orderRepository, times(1)).save(any(OrderEntity.class));
        verify(logger, never()).warn(anyString());
        verify(logger, never()).error(anyString(), any(), any());
    }

    @Test
    @DisplayName("Deve lançar OrderConflictException quando pedido já existe com status diferente de RECEIVED")
    void deveLancarOrderConflictExceptionQuandoPedidoExisteComStatusDiferente() {
        // Given
        UUID orderId = UUID.randomUUID();
        Order newOrder = Order.builder()
                .id(orderId)
                .status(OrderStatus.RECEIVED)
                .build();

        OrderEntity existingEntity = OrderEntity.builder()
                .id(orderId)
                .status(OrderStatus.PREPARING)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingEntity));

        // When & Then
        OrderConflictException exception = assertThrows(
                OrderConflictException.class,
                () -> receiveOrderUseCase.execute(newOrder)
        );

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains(orderId.toString()));
        assertTrue(exception.getMessage().contains(OrderStatus.PREPARING.toString()));
        verify(orderRepository, times(1)).findById(orderId);
        verify(logger, times(1)).warn(anyString(), anyString());
        verify(orderMapper, never()).toEntity(any(Order.class));
        verify(orderRepository, never()).save(any(OrderEntity.class));
    }

    @Test
    @DisplayName("Deve processar pedido novo quando não existe no banco")
    void deveProcessarPedidoNovoQuandoNaoExiste() {
        // Given
        UUID orderId = UUID.randomUUID();
        Order newOrder = Order.builder()
                .id(orderId)
                .build();

        OrderEntity savedEntity = OrderEntity.builder()
                .id(orderId)
                .status(OrderStatus.RECEIVED)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        when(orderMapper.toEntity(any(Order.class))).thenReturn(savedEntity);
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(savedEntity);

        // When
        OrderEntity result = receiveOrderUseCase.execute(newOrder);

        // Then
        assertNotNull(result);
        assertEquals(OrderStatus.RECEIVED, result.getStatus());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderMapper, times(1)).toEntity(any(Order.class));
        verify(orderRepository, times(1)).save(any(OrderEntity.class));
        verify(logger, never()).info(anyString(), any());
        verify(logger, never()).warn(anyString());
        verify(logger, never()).error(anyString(), any(), any());
    }
}

