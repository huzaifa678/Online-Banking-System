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
public class DepositStrategy implements TransactionStrategy {
    
    private final AccountClient accountClient;
    
    @Override
    public void execute(TransactionDto transactionDto) {
        String destinationAccountId = transactionDto.getDestination_accountId();
        BigDecimal amount = transactionDto.getAmount();
        
        log.info("Executing deposit strategy for account: {} with amount: {}", destinationAccountId, amount);
        
        accountClient.creditAccountBalance(amount, destinationAccountId);
        
        log.info("Deposit completed successfully for account: {}", destinationAccountId);
    }
    
    @Override
    public boolean supports(TransactionTypes type) {
        return TransactionTypes.DEPOSIT == type;
    }
} 