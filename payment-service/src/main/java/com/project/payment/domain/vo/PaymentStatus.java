package com.project.payment.domain.vo;

/**
 * Lifecycle status of a payment. Value object (enum); constant names kept
 * identical to the previous {@code model.Status}.
 */
public enum PaymentStatus {
    PENDING,
    COMPLETED,
    FAILED
}
