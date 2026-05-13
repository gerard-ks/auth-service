package dev.ks.authlayerarchitecture.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Getter
@Configuration
public class RQueueConfig {

    @Value("${auth-service.email-queue.max-attempts}")
    private int maxAttempts;

    @Value("${auth-service.email-queue.backoff-delay-ms}")
    private long backoffDelayMs;

    @Value("${auth-service.email-queue.job-deadline-ms}")
    private long jobDeadlineMs;

    @Value("${auth-service.email-queue.queue-name}")
    private String queueName;
}
