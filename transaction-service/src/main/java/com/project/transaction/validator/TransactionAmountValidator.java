package com.project.transaction.validator;

import com.project.transaction.exceptions.TransactionAmountLimitException;
import com.project.transaction.model.dto.TransactionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class TransactionAmountValidator extends TransactionValidator {
    
    private static final BigDecimal MAX_TRANSACTION_AMOUNT = new BigDecimal("1000000");
    private static final BigDecimal MIN_TRANSACTION_AMOUNT = new BigDecimal("1");
    
    @Override
    public void validate(TransactionDto transactionDto) {
        BigDecimal amount = transactionDto.getAmount();
        
        log.info("Validating transaction amount: {}", amount);
        
        if (amount.compareTo(MIN_TRANSACTION_AMOUNT) < 0) {
            throw new TransactionAmountLimitException("Transaction amount must be at least " + MIN_TRANSACTION_AMOUNT);
        }
        
        if (amount.compareTo(MAX_TRANSACTION_AMOUNT) > 0) {
            throw new TransactionAmountLimitException("Transaction amount cannot exceed " + MAX_TRANSACTION_AMOUNT);
        }
        
        log.info("Transaction amount validation passed: {}", amount);
        
        validateNext(transactionDto);
    }
} 