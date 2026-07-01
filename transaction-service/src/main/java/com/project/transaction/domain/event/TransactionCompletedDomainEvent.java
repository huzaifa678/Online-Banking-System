package com.project.transaction.domain.event;


public record TransactionCompletedDomainEvent(String status) implements DomainEvent {
}
