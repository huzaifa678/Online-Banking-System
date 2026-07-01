package com.project.account.domain.event;


public record AccountOpenedDomainEvent(String accountId, String userEmail) implements DomainEvent {
}
