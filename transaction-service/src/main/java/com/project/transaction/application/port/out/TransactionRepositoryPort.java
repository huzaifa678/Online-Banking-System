package com.project.transaction.application.port.out;

import com.project.transaction.domain.model.Transaction;

import java.util.List;
import java.util.Optional;

/**
 * Driven port for persisting and loading {@link Transaction} aggregates.
 */
public interface TransactionRepositoryPort {

    Transaction save(Transaction transaction);

    Optional<Transaction> findById(String transactionId);

    List<Transaction> findAll();
}
