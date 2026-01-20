package com.nextimefood.msproduction.application.usecases.implementation;

import com.nextimefood.msproduction.application.gateways.LoggerPort;
import com.nextimefood.msproduction.application.gateways.OrderRepositoryPort;
import com.nextimefood.msproduction.domain.enums.OrderStatus;
import com.nextimefood.msproduction.infrastructure.persistence.entity.OrderEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários para ListOrdersUseCaseImpl")
class ListOrdersUseCaseImplTest {

    @Mock
    private OrderRepositoryPort orderRepository;

    @Mock
    private LoggerPort logger;

    @InjectMocks
    private ListOrdersUseCaseImpl listOrdersUseCase;

    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Deve listar pedidos com paginação com sucesso")
    void deveListarPedidosComPaginacaoComSucesso() {
        // Given
        OrderEntity order1 = OrderEntity.builder()
                .id(UUID.randomUUID())
                .status(OrderStatus.RECEIVED)
                .build();
        OrderEntity order2 = OrderEntity.builder()
                .id(UUID.randomUUID())
                .status(OrderStatus.PREPARING)
                .build();
        
        List<OrderEntity> orders = Arrays.asList(order1, order2);
        Page<OrderEntity> pageOrders = new PageImpl<>(orders, pageable, 2);
        
        when(orderRepository.findAll(pageable)).thenReturn(pageOrders);

        // When
        Page<OrderEntity> result = listOrdersUseCase.execute(pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().contains(order1));
        assertTrue(result.getContent().contains(order2));
        
        verify(orderRepository, times(1)).findAll(pageable);
        verify(logger, times(1)).info(
                eq("[ListOrdersUseCase] Listando pedidos com paginação. page={}, size={}"),
                eq(0), eq(10)
        );
        verify(logger, times(1)).info(
                eq("[ListOrdersUseCase] Pedidos encontrados: totalElements={}, totalPages={}"),
                eq(2L), eq(1)
        );
        verify(logger, never()).error(anyString(), any(Exception.class));
    }

    @Test
    @DisplayName("Deve retornar página vazia quando não houver pedidos")
    void deveRetornarPaginaVaziaQuandoNaoHouverPedidos() {
        // Given
        Page<OrderEntity> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 0);
        when(orderRepository.findAll(pageable)).thenReturn(emptyPage);

        // When
        Page<OrderEntity> result = listOrdersUseCase.execute(pageable);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
        assertTrue(result.getContent().isEmpty());
        
        verify(orderRepository, times(1)).findAll(pageable);
        verify(logger, times(1)).info(
                eq("[ListOrdersUseCase] Listando pedidos com paginação. page={}, size={}"),
                eq(0), eq(10)
        );
        verify(logger, times(1)).info(
                eq("[ListOrdersUseCase] Pedidos encontrados: totalElements={}, totalPages={}"),
                eq(0L), eq(0)
        );
        verify(logger, never()).error(anyString(), any(Exception.class));
    }

    @Test
    @DisplayName("Deve listar apenas um pedido")
    void deveListarApenasUmPedido() {
        // Given
        OrderEntity order = OrderEntity.builder()
                .id(UUID.randomUUID())
                .status(OrderStatus.RECEIVED)
                .build();
        
        List<OrderEntity> orders = Arrays.asList(order);
        Page<OrderEntity> pageOrders = new PageImpl<>(orders, pageable, 1);
        
        when(orderRepository.findAll(pageable)).thenReturn(pageOrders);

        // When
        Page<OrderEntity> result = listOrdersUseCase.execute(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getContent().size());
        assertEquals(order, result.getContent().get(0));
        
        verify(orderRepository, times(1)).findAll(pageable);
        verify(logger, never()).error(anyString(), any(Exception.class));
    }

    @Test
    @DisplayName("Deve listar pedidos com múltiplas páginas")
    void deveListarPedidosComMultiplasPaginas() {
        // Given
        Pageable firstPage = PageRequest.of(0, 5);
        List<OrderEntity> orders = IntStream.range(0, 5)
                .mapToObj(i -> OrderEntity.builder()
                        .id(UUID.randomUUID())
                        .status(OrderStatus.RECEIVED)
                        .build())
                .collect(Collectors.toList());
        
        Page<OrderEntity> pageOrders = new PageImpl<>(orders, firstPage, 12);
        when(orderRepository.findAll(firstPage)).thenReturn(pageOrders);

        // When
        Page<OrderEntity> result = listOrdersUseCase.execute(firstPage);

        // Then
        assertNotNull(result);
        assertEquals(12, result.getTotalElements());
        assertEquals(3, result.getTotalPages()); // 12 elements / 5 per page = 3 pages
        assertEquals(5, result.getContent().size());
        
        verify(orderRepository, times(1)).findAll(firstPage);
        verify(logger, times(1)).info(
                eq("[ListOrdersUseCase] Listando pedidos com paginação. page={}, size={}"),
                eq(0), eq(5)
        );
        verify(logger, times(1)).info(
                eq("[ListOrdersUseCase] Pedidos encontrados: totalElements={}, totalPages={}"),
                eq(12L), eq(3)
        );
    }

    @Test
    @DisplayName("Deve listar pedidos da segunda página")
    void deveListarPedidosDaSegundaPagina() {
        // Given
        Pageable secondPage = PageRequest.of(1, 10);
        List<OrderEntity> orders = IntStream.range(0, 5)
                .mapToObj(i -> OrderEntity.builder()
                        .id(UUID.randomUUID())
                        .status(OrderStatus.PREPARING)
                        .build())
                .collect(Collectors.toList());
        
        Page<OrderEntity> pageOrders = new PageImpl<>(orders, secondPage, 15);
        when(orderRepository.findAll(secondPage)).thenReturn(pageOrders);

        // When
        Page<OrderEntity> result = listOrdersUseCase.execute(secondPage);

        // Then
        assertNotNull(result);
        assertEquals(15, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        assertEquals(5, result.getContent().size());
        
        verify(orderRepository, times(1)).findAll(secondPage);
        verify(logger, times(1)).info(
                eq("[ListOrdersUseCase] Listando pedidos com paginação. page={}, size={}"),
                eq(1), eq(10)
        );
    }

    @Test
    @DisplayName("Deve lançar exceção e logar erro quando repositório falhar")
    void deveLancarExcecaoQuandoRepositorioFalhar() {
        // Given
        RuntimeException repositoryException = new RuntimeException("Erro no banco de dados");
        when(orderRepository.findAll(pageable)).thenThrow(repositoryException);

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> listOrdersUseCase.execute(pageable)
        );

        assertEquals("Erro no banco de dados", exception.getMessage());
        verify(orderRepository, times(1)).findAll(pageable);
        verify(logger, times(1)).info(
                eq("[ListOrdersUseCase] Listando pedidos com paginação. page={}, size={}"),
                eq(0), eq(10)
        );
        verify(logger, times(1)).error(
                eq("[ListOrdersUseCase] Erro ao listar pedidos"),
                eq(repositoryException)
        );
    }

    @Test
    @DisplayName("Deve listar pedidos com diferentes tamanhos de página")
    void deveListarPedidosComDiferentesTamanhosDePagina() {
        // Given
        Pageable customPageable = PageRequest.of(0, 20);
        List<OrderEntity> orders = IntStream.range(0, 20)
                .mapToObj(i -> OrderEntity.builder()
                        .id(UUID.randomUUID())
                        .status(OrderStatus.READY)
                        .build())
                .collect(Collectors.toList());
        
        Page<OrderEntity> pageOrders = new PageImpl<>(orders, customPageable, 20);
        when(orderRepository.findAll(customPageable)).thenReturn(pageOrders);

        // When
        Page<OrderEntity> result = listOrdersUseCase.execute(customPageable);

        // Then
        assertNotNull(result);
        assertEquals(20, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(20, result.getContent().size());
        
        verify(orderRepository, times(1)).findAll(customPageable);
        verify(logger, times(1)).info(
                eq("[ListOrdersUseCase] Listando pedidos com paginação. page={}, size={}"),
                eq(0), eq(20)
        );
    }

    @Test
    @DisplayName("Deve listar pedidos com diferentes status")
    void deveListarPedidosComDiferentesStatus() {
        // Given
        OrderEntity order1 = OrderEntity.builder()
                .id(UUID.randomUUID())
                .status(OrderStatus.RECEIVED)
                .build();
        OrderEntity order2 = OrderEntity.builder()
                .id(UUID.randomUUID())
                .status(OrderStatus.PREPARING)
                .build();
        OrderEntity order3 = OrderEntity.builder()
                .id(UUID.randomUUID())
                .status(OrderStatus.READY)
                .build();
        OrderEntity order4 = OrderEntity.builder()
                .id(UUID.randomUUID())
                .status(OrderStatus.CANCELLED)
                .build();
        
        List<OrderEntity> orders = Arrays.asList(order1, order2, order3, order4);
        Page<OrderEntity> pageOrders = new PageImpl<>(orders, pageable, 4);
        
        when(orderRepository.findAll(pageable)).thenReturn(pageOrders);

        // When
        Page<OrderEntity> result = listOrdersUseCase.execute(pageable);

        // Then
        assertNotNull(result);
        assertEquals(4, result.getTotalElements());
        assertEquals(OrderStatus.RECEIVED, result.getContent().get(0).getStatus());
        assertEquals(OrderStatus.PREPARING, result.getContent().get(1).getStatus());
        assertEquals(OrderStatus.READY, result.getContent().get(2).getStatus());
        assertEquals(OrderStatus.CANCELLED, result.getContent().get(3).getStatus());
        
        verify(orderRepository, times(1)).findAll(pageable);
    }
}
