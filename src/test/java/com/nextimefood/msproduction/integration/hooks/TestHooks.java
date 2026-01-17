package com.nextimefood.msproduction.integration.hooks;

import com.nextimefood.msproduction.application.gateways.OrderRepositoryPort;
import com.nextimefood.msproduction.integration.utils.SqsTestSupport;
import io.cucumber.java.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

public class TestHooks {

    @Autowired
    private OrderRepositoryPort orderRepositoryPort;

    @Autowired
    private SqsClient sqsClient;

    @Autowired
    private SqsTestSupport sqsTestSupport;

    @Value("${spring.sqs.queues.production-queue}")
    private String productionQueue;

    @Value("${spring.sqs.queues.production-callback-queue}")
    private String productionCallbackQueue;

    @Before
    public void setUp() {
        // Limpar mensagens das filas antes de cada cenário
        // Usamos receiveMessage em loop ao invés de purgeQueue
        // pois purgeQueue tem limitação de 1 chamada a cada 60 segundos
        try {
            drainQueue(productionQueue);
            drainQueue(productionCallbackQueue);
            
            // Pequena pausa para garantir que as mensagens foram processadas
            Thread.sleep(100);
        } catch (Exception e) {
            // Ignorar erros
            System.out.println("Aviso: Não foi possível limpar as filas: " + e.getMessage());
        }
    }

    private void drainQueue(String queueName) {
        try {
            String queueUrl = sqsTestSupport.resolveQueueUrl(sqsClient, queueName);
            
            // Consumir todas as mensagens disponíveis
            boolean hasMessages = true;
            int maxAttempts = 5;
            int attempt = 0;
            
            while (hasMessages && attempt < maxAttempts) {
                var response = sqsClient.receiveMessage(
                    ReceiveMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .maxNumberOfMessages(10)
                        .waitTimeSeconds(0)
                        .visibilityTimeout(0)
                        .build()
                );
                
                hasMessages = !response.messages().isEmpty();
                
                // Deletar mensagens recebidas
                response.messages().forEach(message -> {
                    try {
                        sqsClient.deleteMessage(
                            DeleteMessageRequest.builder()
                                .queueUrl(queueUrl)
                                .receiptHandle(message.receiptHandle())
                                .build()
                        );
                    } catch (Exception e) {
                        // Ignorar erros ao deletar
                    }
                });
                
                attempt++;
            }
        } catch (Exception e) {
            // Ignorar erros individuais
            System.out.println("Aviso: Não foi possível limpar a fila " + queueName + ": " + e.getMessage());
        }
    }
}
