package com.project.account.exceptions;

public class DuplicateAccountTypesException extends RuntimeException{
    public DuplicateAccountTypesException(String message) {
        super(message);
    }
}
