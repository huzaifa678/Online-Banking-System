package com.project.transaction.application.port.out;

import com.project.transaction.domain.event.DomainEvent;

import java.util.List;

/**
 * Driven port for publishing transaction domain events. The infrastructure
 * adapter translates each domain event into its Avro wire form and sends it to
 * Kafka.
 */
public interface TransactionEventPublisherPort {

    void publishAll(List<DomainEvent> events);
}
