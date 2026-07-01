package com.project.transaction.application.port.in;

import com.project.transaction.application.dto.TransactionDto;

import java.util.List;

/**
 * Driving port for reading transactions.
 */
public interface GetTransactionUseCase {

    TransactionDto getTransactionById(String transactionId);

    List<TransactionDto> getAllTransactions();
}
