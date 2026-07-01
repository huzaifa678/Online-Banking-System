package com.project.payment.domain.event;


public record PaymentProcessedDomainEvent(String status) implements DomainEvent {
}
