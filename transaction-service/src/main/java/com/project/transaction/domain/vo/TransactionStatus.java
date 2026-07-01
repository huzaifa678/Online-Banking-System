package com.project.transaction.domain.vo;

/**
 * Lifecycle status of a transaction. Value object (enum); constant names kept
 * identical to the previous {@code model.Status}.
 */
public enum TransactionStatus {
    PENDING,
    COMPLETED,
    FAILED,
    CANCELLED
}
