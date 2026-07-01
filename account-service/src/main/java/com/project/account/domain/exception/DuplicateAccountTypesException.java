package com.project.account.domain.exception;

public class DuplicateAccountTypesException extends RuntimeException {
    public DuplicateAccountTypesException(String message) {
        super(message);
    }
}
