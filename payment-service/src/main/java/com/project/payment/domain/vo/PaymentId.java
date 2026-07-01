package com.project.payment.domain.vo;

import java.util.Objects;

/**
 * Identity value object for a payment. A payment is created before MongoDB
 * assigns its id, so the aggregate holds a nullable {@code PaymentId} until it is
 * persisted; when present the value must be non-blank.
 */
public final class PaymentId {

    private final String value;

    private PaymentId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("PaymentId cannot be null or blank");
        }
        this.value = value;
    }

    public static PaymentId of(String value) {
        return new PaymentId(value);
    }

    public static PaymentId ofNullable(String value) {
        return (value == null || value.isBlank()) ? null : new PaymentId(value);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PaymentId that)) return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
