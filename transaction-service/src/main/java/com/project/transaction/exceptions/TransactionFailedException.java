package com.project.transaction.exceptions;

public class TransactionFailedException extends RuntimeException{
    public TransactionFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
