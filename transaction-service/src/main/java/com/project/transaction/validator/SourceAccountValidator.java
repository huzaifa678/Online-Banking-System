package com.project.transaction.validator;

import com.project.transaction.client.AccountClient;
import com.project.transaction.exceptions.AccountClosedException;
import com.project.transaction.exceptions.AccountNotFoundException;
import com.project.transaction.exceptions.InsufficientFundsException;
import com.project.transaction.model.TransactionTypes;
import com.project.transaction.model.dto.TransactionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SourceAccountValidator extends TransactionValidator {
    
    private final AccountClient accountClient;
    
    @Override
    public void validate(TransactionDto transactionDto) {
        TransactionTypes type = transactionDto.getTransactionType();
        
        if (type == TransactionTypes.WITHDRAWAL || type == TransactionTypes.TRANSFER) {
            String sourceAccountId = transactionDto.getSource_accountId();
            
            log.info("Validating source account: {}", sourceAccountId);
            
            if (!accountClient.doesAccountExists(sourceAccountId)) {
                throw new AccountNotFoundException("The source Account with ID: " + sourceAccountId + " does not exist");
            }
            
            if (!accountClient.accountBalance(sourceAccountId, transactionDto.getAmount())) {
                throw new InsufficientFundsException("You do not have enough balance to make the transaction");
            }
            
            if (accountClient.isAccountClosed(sourceAccountId)) {
                throw new AccountClosedException("The source Account with ID: " + sourceAccountId + " is closed");
            }
            
            log.info("Source account validation passed for account: {}", sourceAccountId);
        }
        
        validateNext(transactionDto);
    }
} 