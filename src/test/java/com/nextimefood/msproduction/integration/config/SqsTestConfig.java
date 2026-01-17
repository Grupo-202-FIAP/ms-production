package com.nextimefood.msproduction.integration.config;

import com.nextimefood.msproduction.integration.consumer.ConsumeMessage;
import com.nextimefood.msproduction.integration.utils.SqsTestSupport;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.URI;

@TestConfiguration
public class SqsTestConfig {

    @Bean
    public SqsClient sqsTestClient() {
        return SqsClient.builder()
            .endpointOverride(URI.create("http://localhost:4566"))
            .region(Region.US_EAST_1)
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create("test", "test")
                )
            )
            .build();
    }

    @Bean
    public ConsumeMessage consumeMessage() {
        return new ConsumeMessage();
    }

    @Bean
    public SqsTestSupport sqsTestSupport(SqsClient sqsClient) {
        return new SqsTestSupport(sqsClient);
    }
}
