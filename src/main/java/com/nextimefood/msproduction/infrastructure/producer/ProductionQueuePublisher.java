package com.nextimefood.msproduction.infrastructure.producer;

import com.nextimefood.msproduction.application.gateways.LoggerPort;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ProductionQueuePublisher {

    private final SqsTemplate sqsTemplate;
    private final LoggerPort logger;

    public void sendMessage(String queueName, Object message) {
        try {
            logger.info("[ProductionQueuePublisher] Enviando mensagem para a fila: {}", queueName);
            sqsTemplate.send(to -> to.queue(queueName).payload(message));
            logger.info("[ProductionQueuePublisher] Mensagem enviada com sucesso para a fila: {}", queueName);
        } catch (Exception e) {
            logger.error("[ProductionQueuePublisher] Falha ao enviar mensagem para a fila: {}", queueName, e);
            throw e;
        }
    }
}
