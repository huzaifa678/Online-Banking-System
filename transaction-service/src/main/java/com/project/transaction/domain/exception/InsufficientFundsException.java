package com.project.transaction.domain.exception;

/**
 * Raised when a source account lacks the funds for a withdrawal or transfer.
 * The web adapter maps this to HTTP 400 (as the previous {@code @ResponseStatus}
 * did), so the domain stays free of framework annotations.
 */
public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
