package com.project.transaction.domain.vo;

/**
 * The kind of transaction. Value object (enum); constant names kept identical to
 * the previous {@code model.TransactionTypes} so external JSON is unchanged.
 */
public enum TransactionType {
    DEPOSIT,
    WITHDRAWAL,
    TRANSFER
}
