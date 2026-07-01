package com.project.account.domain.model;

import com.project.account.domain.event.AccountClosedDomainEvent;
import com.project.account.domain.event.AccountOpenedDomainEvent;
import com.project.account.domain.event.DomainEvent;
import com.project.account.domain.exception.ClosedAccountException;
import com.project.account.domain.exception.DuplicateAccountTypesException;
import com.project.account.domain.exception.SameStatusException;
import com.project.account.domain.vo.AccountId;
import com.project.account.domain.vo.AccountStatus;
import com.project.account.domain.vo.AccountType;
import com.project.account.domain.vo.Email;
import com.project.account.domain.vo.Money;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class encapsulates the Account data model as a domain value object. It represents a bank account with its associated properties and behaviors.
 */
public class Account {

    private final AccountId id;
    private AccountType type;
    private Money balance;
    private AccountStatus status;
    private final Email userEmail;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    private Account(AccountId id, AccountType type, Money balance, AccountStatus status, Email userEmail) {
        this.id = id;
        this.type = type;
        this.balance = balance;
        this.status = status;
        this.userEmail = userEmail;
    }

    /**
     * Rebuild an aggregate from already-persisted state. Raises no events.
     */
    public static Account reconstitute(AccountId id, AccountType type, Money balance,
                                       AccountStatus status, Email userEmail) {
        return new Account(id, type, balance, status, userEmail);
    }

    /**
     * Open a brand-new account. Starts {@link AccountStatus#ACTIVE} and raises an
     * {@link AccountOpenedDomainEvent}.
     */
    public static Account open(AccountId id, AccountType type, Money balance, Email userEmail) {
        Money initialBalance = balance == null ? Money.ZERO : balance;
        Account account = new Account(id, type, initialBalance, AccountStatus.ACTIVE, userEmail);
        account.registerEvent(new AccountOpenedDomainEvent(id.value(), userEmail.value()));
        return account;
    }

    /**
     * Enforce the rules for re-requesting an account of a type that already exists
     * for the user (formerly the {@code AccountValidator} chain):
     * closed → rejected, active → duplicate, inactive → reactivated.
     */
    public void ensureCanReRegister() {
        switch (status) {
            case CLOSED -> throw new ClosedAccountException(
                    "Account is closed for user: " + userEmail.value());
            case ACTIVE -> throw new DuplicateAccountTypesException(
                    "Account already exists and is active: " + userEmail.value());
            case INACTIVE -> reactivate();
        }
    }

    public void close() {
        if (status == AccountStatus.CLOSED) {
            throw new IllegalStateException("Account is already closed");
        }
        status = AccountStatus.CLOSED;
        registerEvent(new AccountClosedDomainEvent(id.value(), userEmail.value()));
    }

    public void changeStatus(AccountStatus newStatus) {
        if (status == newStatus) {
            throw new SameStatusException("This status for the account already exists");
        }
        status = newStatus;
    }

    public void reactivate() {
        status = AccountStatus.ACTIVE;
    }

    /** Overwrite mutable details (backing the full-update endpoint). */
    public void updateDetails(AccountType newType, Money newBalance, AccountStatus newStatus) {
        this.type = newType;
        this.balance = newBalance;
        this.status = newStatus;
    }

    public boolean hasSufficientBalance(Money amount) {
        return balance.isGreaterThanOrEqualTo(amount);
    }

    public boolean isClosed() {
        return status == AccountStatus.CLOSED;
    }

    /** Correct banking semantics: increase the balance. */
    public void deposit(Money amount) {
        this.balance = this.balance.add(amount);
    }

    /** Correct banking semantics: decrease the balance. */
    public void withdraw(Money amount) {
        this.balance = this.balance.subtract(amount);
    }

    public AccountId id() {
        return id;
    }

    public AccountType type() {
        return type;
    }

    public Money balance() {
        return balance;
    }

    public AccountStatus status() {
        return status;
    }

    public Email userEmail() {
        return userEmail;
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
