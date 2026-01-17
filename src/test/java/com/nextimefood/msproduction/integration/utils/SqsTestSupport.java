package com.nextimefood.msproduction.integration.utils;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;

@Component
public class SqsTestSupport {

    private final SqsClient sqsClient;

    public SqsTestSupport(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    public String resolveQueueUrl(SqsClient sqsClient, String queueName) {
        return sqsClient.getQueueUrl(
            GetQueueUrlRequest.builder()
                .queueName(queueName)
                .build()
        ).queueUrl();
    }
}
