package com.project.payment.domain.exception;

/**
 * Raised when a payment cannot proceed because of an account precondition
 * (missing/closed account or insufficient balance). The web adapter maps any such
 * failure to HTTP 400, matching the previous behaviour.
 */
public class PaymentValidationException extends RuntimeException {
    public PaymentValidationException(String message) {
        super(message);
    }
}
