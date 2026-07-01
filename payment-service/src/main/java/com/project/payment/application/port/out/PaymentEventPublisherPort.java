package com.project.payment.application.port.out;

import com.project.payment.domain.event.DomainEvent;

import java.util.List;


public interface PaymentEventPublisherPort {

    void publishAll(List<DomainEvent> events);
}
