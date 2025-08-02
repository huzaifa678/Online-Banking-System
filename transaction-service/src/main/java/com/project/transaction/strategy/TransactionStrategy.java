package com.project.transaction.strategy;

import com.project.transaction.model.dto.TransactionDto;

public interface TransactionStrategy {
    void execute(TransactionDto transactionDto);
    boolean supports(com.project.transaction.model.TransactionTypes type);
} 