package com.project.payment.domain.model;

import com.project.payment.domain.event.DomainEvent;
import com.project.payment.domain.event.PaymentProcessedDomainEvent;
import com.project.payment.domain.vo.AccountId;
import com.project.payment.domain.vo.Money;
import com.project.payment.domain.vo.PaymentId;
import com.project.payment.domain.vo.PaymentMethod;
import com.project.payment.domain.vo.PaymentStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class encapsulates the Payment data model as a domain value object. It represents a payment transaction with its associated properties and behaviors.
 */
public class Payment {

    private PaymentId paymentId;
    private final AccountId sourceAccountId;
    private final AccountId destinationAccountId;
    private final Money amount;
    private final PaymentMethod method;
    private PaymentStatus status;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    private Payment(PaymentId paymentId, AccountId sourceAccountId, AccountId destinationAccountId,
                    Money amount, PaymentMethod method, PaymentStatus status) {
        this.paymentId = paymentId;
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
        this.method = method;
        this.status = status;
    }

    /**
     * Start a new payment (no id yet — assigned on persistence, no status until the
     * gateway outcome is known).
     */
    public static Payment initiate(AccountId sourceAccountId, AccountId destinationAccountId,
                                   Money amount, PaymentMethod method) {
        return new Payment(null, sourceAccountId, destinationAccountId, amount, method, null);
    }

    /**
     * Rebuild an aggregate from persisted state.
     */
    public static Payment reconstitute(PaymentId paymentId, AccountId sourceAccountId,
                                       AccountId destinationAccountId, Money amount,
                                       PaymentMethod method, PaymentStatus status) {
        return new Payment(paymentId, sourceAccountId, destinationAccountId, amount, method, status);
    }

    public void complete() {
        transitionTo(PaymentStatus.COMPLETED);
    }

    public void pending() {
        transitionTo(PaymentStatus.PENDING);
    }

    public void fail() {
        transitionTo(PaymentStatus.FAILED);
    }

    private void transitionTo(PaymentStatus newStatus) {
        this.status = newStatus;
        registerEvent(new PaymentProcessedDomainEvent(newStatus.name()));
    }

    public boolean isCompleted() {
        return status == PaymentStatus.COMPLETED;
    }

    public PaymentId paymentId() {
        return paymentId;
    }

    public AccountId sourceAccountId() {
        return sourceAccountId;
    }

    public AccountId destinationAccountId() {
        return destinationAccountId;
    }

    public Money amount() {
        return amount;
    }

    public PaymentMethod method() {
        return method;
    }

    public PaymentStatus status() {
        return status;
    }

    public List<DomainEvent> domainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearEvents() {
        domainEvents.clear();
    }

    private void registerEvent(DomainEvent event) {
        domainEvents.add(event);
    }
}
