package com.project.account.domain.exception;

public class ClosedAccountException extends RuntimeException {
    public ClosedAccountException(String message) {
        super(message);
    }
}
