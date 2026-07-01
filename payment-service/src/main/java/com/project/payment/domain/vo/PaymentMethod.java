package com.project.payment.domain.vo;

/**
 * Payment instrument type. Value object (enum); constant names kept identical to
 * the previous {@code model.paymentMethod} so external JSON is unchanged.
 */
public enum PaymentMethod {
    CREDIT_CARD,
    DEBIT_CARD
}
