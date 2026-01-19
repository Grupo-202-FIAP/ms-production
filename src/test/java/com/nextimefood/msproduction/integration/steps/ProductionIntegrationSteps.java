package com.nextimefood.msproduction.integration.steps;

import com.nextimefood.msproduction.application.gateways.OrderRepositoryPort;
import com.nextimefood.msproduction.domain.entity.Event;
import com.nextimefood.msproduction.domain.entity.Order;
import com.nextimefood.msproduction.domain.enums.EventSource;
import com.nextimefood.msproduction.domain.enums.OrderStatus;
import com.nextimefood.msproduction.domain.enums.PaymentStatus;
import com.nextimefood.msproduction.domain.enums.SagaStatus;
import com.nextimefood.msproduction.infrastructure.persistence.entity.OrderEntity;
import com.nextimefood.msproduction.integration.consumer.ConsumeMessage;
import com.nextimefood.msproduction.integration.fixtures.EventFixture;
import com.nextimefood.msproduction.integration.fixtures.OrderFixture;
import com.nextimefood.msproduction.integration.utils.SqsTestSupport;
import com.nextimefood.msproduction.utils.JsonConverter;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.UUID;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

public class ProductionIntegrationSteps {

    private final ConsumeMessage consumeMessage;
    private final SqsTestSupport sqsTestSupport;
    private final JsonConverter jsonConverter;
    private final SqsClient sqsClient;
    private final OrderRepositoryPort orderRepositoryPort;

    private String lastQueueName;
    private Event lastEvent;
    private Order lastOrder;
    private boolean orderShouldNotExist = false;

    public ProductionIntegrationSteps(
        ConsumeMessage consumeMessage,
        SqsTestSupport sqsTestSupport,
        JsonConverter jsonConverter,
        SqsClient sqsClient,
        OrderRepositoryPort orderRepositoryPort
    ) {
        this.consumeMessage = consumeMessage;
        this.sqsTestSupport = sqsTestSupport;
        this.jsonConverter = jsonConverter;
        this.sqsClient = sqsClient;
        this.orderRepositoryPort = orderRepositoryPort;
    }

    @Dado("que um evento válido é enviado para a fila {word} com o status {word} e o paymentStatus {word}")
    public void queUmEventoValidoEhEnviadoParaAFila(String queueName, String status, String paymentStatus) {
        String normalizedQueueName = queueName.replace("_", "-");
        lastQueueName = normalizedQueueName;

        String queueUrl = sqsTestSupport.resolveQueueUrl(sqsClient, normalizedQueueName);

        // Usar Fixtures para criar objetos de teste
        Order order = OrderFixture.create(paymentStatus);
        Event event = EventFixture.create(status, order);

        lastOrder = order;
        lastEvent = event;

        // Enviar mensagem para a fila
        sqsClient.sendMessage(
            SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(jsonConverter.toJson(event))
                .build()
        );
    }

    @E("o pedido não existe no repositório")
    public void oPedidoNaoExisteNoRepositorio() {
        orderShouldNotExist = true;
    }

    @Quando("o evento é recebido pelo serviço de produção")
    public void oEventoERecebidoPeloServicoDeProducao() {
        // Salvar o pedido no banco ANTES do processamento quando necessário
        // Isso simula o estado correto para cada cenário
        if (shouldSaveOrderBeforeProcessing()) {
            OrderEntity orderEntity = OrderEntity.builder()
                .id(lastOrder.getId())
                .transactionId(lastOrder.getTransactionId())
                .identifier(lastOrder.getIdentifier())
                .customerId(lastOrder.getCustomerId())
                .paymentStatus(lastOrder.getPaymentStatus())
                .status(lastOrder.getStatus())
                .createdAt(lastOrder.getCreatedAt())
                .updatedAt(lastOrder.getUpdatedAt())
                .build();
            
            orderRepositoryPort.save(orderEntity);
        }
        
        // O @SqsListener irá consumir automaticamente
        // Não fazemos nada aqui - teste 100% integração real
    }

    /**
     * Determina se o pedido deve ser salvo antes do processamento.
     * Esta lógica prepara o estado correto para cada cenário.
     */
    private boolean shouldSaveOrderBeforeProcessing() {
        if (orderShouldNotExist) {
            return false;
        }

        SagaStatus sagaStatus = SagaStatus.valueOf(lastEvent.getStatus());
        PaymentStatus paymentStatus = lastOrder.getPaymentStatus();

        // SUCCESS + PENDING: ReceiveOrderUseCase cria o pedido
        if (sagaStatus == SagaStatus.SUCCESS && paymentStatus == PaymentStatus.PENDING) {
            return false;
        }

        // SUCCESS + PROCESSED: StartPreparationOrderUseCase precisa que exista
        if (sagaStatus == SagaStatus.SUCCESS && paymentStatus == PaymentStatus.PROCESSED) {
            return true;
        }

        // FAIL ou ROLLBACK_PENDING: CancelOrderUseCase precisa que exista
        if (sagaStatus == SagaStatus.FAIL || sagaStatus == SagaStatus.ROLLBACK_PENDING) {
            return true;
        }

        return false;
    }

    @Entao("o pedido deve ser processado com o status {word}")
    public void oPedidoDeveSerProcessadoComOStatus(String expectedStatus) {
        OrderStatus expectedOrderStatus = OrderStatus.valueOf(expectedStatus);

        // Usar Awaitility para aguardar processamento assíncrono
        await()
            .atMost(10, SECONDS)
            .pollInterval(500, java.util.concurrent.TimeUnit.MILLISECONDS)
            .untilAsserted(() -> {
                var orderOptional = orderRepositoryPort.findById(lastOrder.getId());

                assertTrue(orderOptional.isPresent(),
                    "Pedido deveria existir no repositório após processamento");

                var savedOrder = orderOptional.get();
                assertEquals(expectedOrderStatus, savedOrder.getStatus(),
                    String.format("Status do pedido deveria ser %s mas foi %s",
                        expectedStatus, savedOrder.getStatus()));
            });
    }

    @Entao("o pedido deve ser cancelado com o status {word}")
    public void oPedidoDeveSerCanceladoComOStatus(String expectedStatus) {
        OrderStatus expectedOrderStatus = OrderStatus.valueOf(expectedStatus);

        if (!orderShouldNotExist) {
            // Usar Awaitility para aguardar processamento assíncrono
            await()
                .atMost(10, SECONDS)
                .pollInterval(500, java.util.concurrent.TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    var orderOptional = orderRepositoryPort.findById(lastOrder.getId());

                    assertTrue(orderOptional.isPresent(),
                        "Pedido deveria existir no repositório após cancelamento");

                    var savedOrder = orderOptional.get();
                    assertEquals(expectedOrderStatus, savedOrder.getStatus(),
                        String.format("Status do pedido deveria ser %s mas foi %s",
                            expectedStatus, savedOrder.getStatus()));
                });
        }
    }

    @E("o evento deve ser publicado na fila de callback {word} com o status {word}")
    public void oEventoDeveSerPublicadoNaFilaDeCallbackComOStatus(String callbackQueue, String callbackStatus) {
        String normalizedCallbackQueue = callbackQueue.replace("_", "-");
        String callbackQueueUrl = sqsTestSupport.resolveQueueUrl(sqsClient, normalizedCallbackQueue);

        // Usar Awaitility para aguardar callback
        await()
            .atMost(10, SECONDS)
            .pollInterval(500, java.util.concurrent.TimeUnit.MILLISECONDS)
            .untilAsserted(() -> {
                var messages = sqsClient.receiveMessage(
                    ReceiveMessageRequest.builder()
                        .queueUrl(callbackQueueUrl)
                        .maxNumberOfMessages(1)
                        .visibilityTimeout(0)
                        .build()
                ).messages();

                assertFalse(messages.isEmpty(),
                    String.format("Deveria existir mensagem na fila de callback %s",
                        normalizedCallbackQueue));

                Event callbackEvent = jsonConverter.toEvent(messages.get(0).body());

                assertNotNull(callbackEvent, "Evento de callback não deveria ser nulo");
                
                assertEquals(callbackStatus, callbackEvent.getStatus(),
                    String.format("Status do evento de callback deveria ser %s mas foi %s",
                        callbackStatus, callbackEvent.getStatus()));
                
                assertEquals(EventSource.PRODUCTION.getSource(), callbackEvent.getSource(),
                    String.format("Source do evento de callback deveria ser %s mas foi %s",
                        EventSource.PRODUCTION.getSource(), callbackEvent.getSource()));

                assertNotNull(callbackEvent.getHistory(),
                    "Histórico do evento não deveria ser nulo");
                
                assertFalse(callbackEvent.getHistory().isEmpty(),
                    "Histórico do evento não deveria estar vazio");

                boolean hasProductionHistory = callbackEvent.getHistory().stream()
                    .anyMatch(h -> EventSource.PRODUCTION.getSource().equals(h.getSource()));

                assertTrue(hasProductionHistory,
                    "Deveria existir uma entrada do PRODUCTION no histórico do evento");
            });
    }
}
