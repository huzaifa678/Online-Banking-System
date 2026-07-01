package com.project.transaction.domain.model;

import com.project.transaction.domain.event.DomainEvent;
import com.project.transaction.domain.event.TransactionCompletedDomainEvent;
import com.project.transaction.domain.vo.AccountId;
import com.project.transaction.domain.vo.Money;
import com.project.transaction.domain.vo.TransactionStatus;
import com.project.transaction.domain.vo.TransactionType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class encapsulates the Transaction data model as a domain value object. It represents a financial transaction with its associated properties and behaviors.
 */
public class Transaction {

    private final String transactionId;
    private final AccountId sourceAccountId;
    private final AccountId destinationAccountId;
    private final Money amount;
    private final TransactionType type;
    private TransactionStatus status;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    private Transaction(String transactionId, AccountId sourceAccountId, AccountId destinationAccountId,
                        Money amount, TransactionType type, TransactionStatus status) {
        this.transactionId = transactionId;
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
        this.type = type;
        this.status = status;
    }

    /**
     * Start a new transaction. Begins in {@link TransactionStatus#PENDING}.
     */
    public static Transaction initiate(String transactionId, AccountId sourceAccountId,
                                       AccountId destinationAccountId, Money amount, TransactionType type) {
        return new Transaction(transactionId, sourceAccountId, destinationAccountId, amount, type,
                TransactionStatus.PENDING);
    }

    /**
     * Rebuild an aggregate from persisted state.
     */
    public static Transaction reconstitute(String transactionId, AccountId sourceAccountId,
                                           AccountId destinationAccountId, Money amount,
                                           TransactionType type, TransactionStatus status) {
        return new Transaction(transactionId, sourceAccountId, destinationAccountId, amount, type, status);
    }

    /** Mark the transaction completed and raise the completion event. */
    public void markCompleted() {
        this.status = TransactionStatus.COMPLETED;
        registerEvent(new TransactionCompletedDomainEvent(status.name()));
    }

    public void markFailed() {
        this.status = TransactionStatus.FAILED;
    }

    public String transactionId() {
        return transactionId;
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

    public TransactionType type() {
        return type;
    }

    public TransactionStatus status() {
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
