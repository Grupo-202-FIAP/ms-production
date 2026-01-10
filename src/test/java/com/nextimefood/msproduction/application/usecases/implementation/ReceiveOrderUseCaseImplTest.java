package com.nextimefood.msproduction.application.usecases.implementation;

import com.nextimefood.msproduction.application.gateways.LoggerPort;
import com.nextimefood.msproduction.application.gateways.OrderRepositoryPort;
import com.nextimefood.msproduction.application.mapper.OrderMapper;
import com.nextimefood.msproduction.domain.entity.Order;
import com.nextimefood.msproduction.domain.enums.OrderStatus;
import com.nextimefood.msproduction.infrastructure.persistence.entity.OrderEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
}

