package com.project.account.domain.event;


public record AccountClosedDomainEvent(String accountId, String userEmail) implements DomainEvent {
}
