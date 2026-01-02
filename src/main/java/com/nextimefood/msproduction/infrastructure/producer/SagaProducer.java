package com.nextimefood.msproduction.infrastructure.producer;

import com.nextimefood.msproduction.application.gateways.LoggerPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@RequiredArgsConstructor
@Component
public class SagaProducer {

    private final LoggerPort logger;
    private final SqsAsyncClient sqsAsyncClient;

    @Value("${spring.sqs.queues.production-queue}")
    private String productionQueue;

    public void sendMessage(String payload) {
        try {
            logger.info("[sendMessage] Enviando mensagem para a fila: {} com valores: {}", productionQueue, payload);
            sqsAsyncClient.sendMessage(builder -> builder.queueUrl(productionQueue).messageBody(payload));
        } catch (Exception e) {
            logger.error("[sendMessage] Falha ao enviar mensagem para a fila: {} com valores: {}", productionQueue, e);
        }
    }
}
