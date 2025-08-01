package com.project.transaction.exceptions;

public class TransactionAmountLimitException extends RuntimeException {
    
    public TransactionAmountLimitException(String message) {
        super(message);
    }
    
    public TransactionAmountLimitException(String message, Throwable cause) {
        super(message, cause);
    }
} 