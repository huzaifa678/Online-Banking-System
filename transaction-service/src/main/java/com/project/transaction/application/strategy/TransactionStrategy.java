package com.project.transaction.application.strategy;

import com.project.transaction.domain.model.Transaction;
import com.project.transaction.domain.vo.TransactionType;


public interface TransactionStrategy {

    void execute(Transaction transaction);

    boolean supports(TransactionType type);
}
