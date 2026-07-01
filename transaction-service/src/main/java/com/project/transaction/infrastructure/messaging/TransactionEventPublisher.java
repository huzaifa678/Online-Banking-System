package com.project.transaction.infrastructure.messaging;

import com.project.transaction.application.port.out.TransactionEventPublisherPort;
import com.project.transaction.domain.event.DomainEvent;
import com.project.transaction.domain.event.TransactionCompletedDomainEvent;
import com.project.transaction.event.TransactionCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Messaging adapter implementing {@link TransactionEventPublisherPort}. Translates
 * transaction domain events into their Avro wire form and publishes to the
 * {@code transaction-created} topic (unchanged from before).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionEventPublisher implements TransactionEventPublisherPort {

    private final KafkaTemplate<String, TransactionCreatedEvent> kafkaTemplate;

    @Override
    public void publishAll(List<DomainEvent> events) {
        events.forEach(this::publish);
    }

    private void publish(DomainEvent event) {
        if (event instanceof TransactionCompletedDomainEvent completed) {
            TransactionCreatedEvent wire = new TransactionCreatedEvent();
            wire.setStatus(completed.status());
            log.info("Publishing TransactionCreatedEvent {} to topic transaction-created", wire);
            kafkaTemplate.send("transaction-created", wire);
        } else {
            log.warn("No wire mapping for domain event {}", event.getClass().getSimpleName());
        }
    }
}
