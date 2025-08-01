package com.project.transaction.strategy;

import com.project.transaction.client.AccountClient;
import com.project.transaction.model.dto.TransactionDto;
import com.project.transaction.model.TransactionTypes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class WithdrawalStrategy implements TransactionStrategy {
    
    private final AccountClient accountClient;
    
    @Override
    public void execute(TransactionDto transactionDto) {
        String sourceAccountId = transactionDto.getSource_accountId();
        BigDecimal amount = transactionDto.getAmount();
        
        log.info("Executing withdrawal strategy for account: {} with amount: {}", sourceAccountId, amount);
        
        // For withdrawal, we debit the source account
        accountClient.debitAccountBalance(amount, sourceAccountId);
        
        log.info("Withdrawal completed successfully for account: {}", sourceAccountId);
    }
    
    @Override
    public boolean supports(TransactionTypes type) {
        return TransactionTypes.WITHDRAWAL == type;
    }
} 