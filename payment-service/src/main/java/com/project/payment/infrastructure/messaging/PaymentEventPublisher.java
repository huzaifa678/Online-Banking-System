package com.project.payment.infrastructure.messaging;

import com.project.payment.application.port.out.PaymentEventPublisherPort;
import com.project.payment.domain.event.DomainEvent;
import com.project.payment.domain.event.PaymentProcessedDomainEvent;
import com.project.payment.event.PaymentCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventPublisher implements PaymentEventPublisherPort {

    private final KafkaTemplate<String, PaymentCreatedEvent> kafkaTemplate;

    @Override
    public void publishAll(List<DomainEvent> events) {
        events.forEach(this::publish);
    }

    private void publish(DomainEvent event) {
        if (event instanceof PaymentProcessedDomainEvent processed) {
            PaymentCreatedEvent wire = new PaymentCreatedEvent();
            wire.setStatus(processed.status());
            log.info("Publishing PaymentCreatedEvent to Kafka topic 'payment-created': {}", wire);
            kafkaTemplate.send("payment-created", wire);
        } else {
            log.warn("No wire mapping for domain event {}", event.getClass().getSimpleName());
        }
    }
}
