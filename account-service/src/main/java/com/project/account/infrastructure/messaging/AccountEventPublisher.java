package com.project.account.infrastructure.messaging;

import com.project.account.application.port.out.AccountEventPublisherPort;
import com.project.account.domain.event.AccountClosedDomainEvent;
import com.project.account.domain.event.AccountOpenedDomainEvent;
import com.project.account.domain.event.DomainEvent;
import com.project.account.event.AccountClosedEvent;
import com.project.account.event.AccountCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class AccountEventPublisher implements AccountEventPublisherPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publishAll(List<DomainEvent> events) {
        events.forEach(this::publish);
    }

    private void publish(DomainEvent event) {
        if (event instanceof AccountOpenedDomainEvent opened) {
            AccountCreatedEvent wire = new AccountCreatedEvent();
            wire.setAccountId(opened.accountId());
            wire.setUserEmail(opened.userEmail());
            log.info("Publishing AccountCreatedEvent {} to topic account-created", wire);
            kafkaTemplate.send("account-created", wire);
        } else if (event instanceof AccountClosedDomainEvent closed) {
            AccountClosedEvent wire = new AccountClosedEvent();
            wire.setAccountId(closed.accountId());
            wire.setUserEmail(closed.userEmail());
            log.info("Publishing AccountClosedEvent {} to topic account-closed", wire);
            kafkaTemplate.send("account-closed", wire);
        } else {
            log.warn("No wire mapping for domain event {}", event.getClass().getSimpleName());
        }
    }
}
