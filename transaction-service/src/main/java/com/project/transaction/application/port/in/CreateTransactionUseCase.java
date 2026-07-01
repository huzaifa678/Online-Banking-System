package com.project.transaction.application.port.in;

import com.project.transaction.application.dto.TransactionDto;

/**
 * Driving port for creating (processing) a transaction.
 */
public interface CreateTransactionUseCase {

    TransactionDto createTransaction(TransactionDto transactionDto);
}
