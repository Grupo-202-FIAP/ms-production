package com.nextimefood.msproduction.infrastructure.producer;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.stereotype.Component;

@Component
public class ProductionQueuePublisher {

    private final SqsTemplate sqsTemplate;

    public ProductionQueuePublisher(SqsTemplate sqsTemplate) {
        this.sqsTemplate = sqsTemplate;
    }

    public void sendMessage(String queueName, Object message) {
        // Sends the message to the specified queue
        sqsTemplate.send(to -> to.queue(queueName).payload(message));
    }
}
