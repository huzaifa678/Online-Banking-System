package com.project.account.application.port.out;

import com.project.account.domain.event.DomainEvent;

import java.util.List;

/**
 * Driven port for publishing account domain events. The infrastructure adapter
 * translates each domain event into its Avro wire form and sends it to Kafka.
 */
public interface AccountEventPublisherPort {

    void publishAll(List<DomainEvent> events);
}
