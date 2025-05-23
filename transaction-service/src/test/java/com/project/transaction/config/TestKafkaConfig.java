package com.project.transaction.config;

import com.project.transaction.event.TransactionCreatedEvent;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;

@TestConfiguration
public class TestKafkaConfig {

    @Bean
    @Primary
    public KafkaTemplate<String, TransactionCreatedEvent> kafkaTemplate() {
        return Mockito.mock(KafkaTemplate.class);
    }
}

