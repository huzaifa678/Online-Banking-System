package com.project.account.domain.exception;

public class SameStatusException extends RuntimeException {
    public SameStatusException(String message) {
        super(message);
    }
}
