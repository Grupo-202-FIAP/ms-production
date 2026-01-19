package com.nextimefood.msproduction.integration.consumer;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.List;
import java.util.stream.Collectors;

public class ConsumeMessage {

    public List<String> receiveMessages(
        SqsClient sqsClient,
        String queueUrl,
        int maxMessages
    ) {
        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
            .queueUrl(queueUrl)
            .maxNumberOfMessages(maxMessages)
            .waitTimeSeconds(1)
            .visibilityTimeout(0)
            .build();

        var response = sqsClient.receiveMessage(request);

        return response.messages().stream()
            .map(message -> message.body())
            .collect(Collectors.toList());
    }

    public List<String> receiveMessages(SqsClient sqsClient, String queueUrl) {
        return receiveMessages(sqsClient, queueUrl, 10);
    }
}
