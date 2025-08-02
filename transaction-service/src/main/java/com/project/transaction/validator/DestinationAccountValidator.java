package com.project.transaction.validator;

import com.project.transaction.client.AccountClient;
import com.project.transaction.exceptions.AccountClosedException;
import com.project.transaction.exceptions.AccountNotFoundException;
import com.project.transaction.model.TransactionTypes;
import com.project.transaction.model.dto.TransactionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DestinationAccountValidator extends TransactionValidator {
    
    private final AccountClient accountClient;
    
    @Override
    public void validate(TransactionDto transactionDto) {
        TransactionTypes type = transactionDto.getTransactionType();
        
        if (type == TransactionTypes.DEPOSIT || type == TransactionTypes.TRANSFER) {
            String destinationAccountId = transactionDto.getDestination_accountId();
            
            log.info("Validating destination account: {}", destinationAccountId);
            
            if (!accountClient.doesAccountExists(destinationAccountId)) {
                throw new AccountNotFoundException("The destination Account with ID: " + destinationAccountId + " does not exist");
            }
            
            if (accountClient.isAccountClosed(destinationAccountId)) {
                throw new AccountClosedException("The destination Account with ID: " + destinationAccountId + " is closed");
            }
            
            log.info("Destination account validation passed for account: {}", destinationAccountId);
        }
        
        validateNext(transactionDto);
    }
} 