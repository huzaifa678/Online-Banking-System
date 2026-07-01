package com.project.payment.domain.exception;

/**
 * Raised when the external payment gateway cannot be reached / has failed even
 * after retries, or when its circuit breaker is open. Signals that the payment
 * outcome is unknown and the caller should try again later, rather than silently
 * recording the payment as failed.
 */
public class PaymentGatewayException extends RuntimeException {
    public PaymentGatewayException(String message, Throwable cause) {
        super(message, cause);
    }
}
