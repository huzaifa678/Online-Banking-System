package com.project.transaction.exceptions;

public class AccountClosedException extends RuntimeException{
    public AccountClosedException(String message) {
        super(message);
    }
}
