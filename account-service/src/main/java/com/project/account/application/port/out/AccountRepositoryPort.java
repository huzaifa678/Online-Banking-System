package com.project.account.application.port.out;

import com.project.account.domain.model.Account;
import com.project.account.domain.vo.AccountId;
import com.project.account.domain.vo.AccountType;

import java.util.List;
import java.util.Optional;

/**
 * Driven port for persisting and loading {@link Account} aggregates. Implemented
 * by a persistence adapter in the infrastructure layer.
 */
public interface AccountRepositoryPort {

    Account save(Account account);

    Optional<Account> findById(AccountId id);

    Optional<Account> findByUserEmailAndAccountType(String userEmail, AccountType accountType);

    List<Account> findAll();

    boolean existsById(AccountId id);

    void deleteById(AccountId id);
}
