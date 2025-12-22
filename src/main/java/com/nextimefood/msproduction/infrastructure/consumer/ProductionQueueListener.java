package com.nextimefood.msproduction.infrastructure.consumer;

import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
public class ProductionQueueListener {

    @SqsListener("${sqs.queues.production-queue.name}")
    public void listen(String message) {
        System.out.println(message);
    }

}