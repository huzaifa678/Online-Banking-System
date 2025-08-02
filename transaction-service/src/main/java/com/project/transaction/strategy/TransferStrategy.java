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

public class TransferStrategy implements TransactionStrategy {
    
    private final AccountClient accountClient;
    
    @Override
    public void execute(TransactionDto transactionDto) {
        String sourceAccountId = transactionDto.getSource_accountId();
        String destinationAccountId = transactionDto.getDestination_accountId();
        BigDecimal amount = transactionDto.getAmount();
        
        log.info("Executing transfer strategy from account: {} to account: {} with amount: {}", 
                sourceAccountId, destinationAccountId, amount);
        
        accountClient.debitAccountBalance(amount, sourceAccountId);
        accountClient.creditAccountBalance(amount, destinationAccountId);
        
        log.info("Transfer completed successfully from account: {} to account: {}", 
                sourceAccountId, destinationAccountId);
    }
    
    @Override
    public boolean supports(TransactionTypes type) {
        return TransactionTypes.TRANSFER == type;
    }
} 