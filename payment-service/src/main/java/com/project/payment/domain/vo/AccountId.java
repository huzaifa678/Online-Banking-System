package com.project.payment.domain.vo;

import java.util.Objects;

/**
 * Identity value object for an account involved in a payment.
 */
public final class AccountId {

    private final String value;

    private AccountId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("AccountId cannot be null or blank");
        }
        this.value = value;
    }

    public static AccountId of(String value) {
        return new AccountId(value);
    }

    public static AccountId ofNullable(String value) {
        return (value == null || value.isBlank()) ? null : new AccountId(value);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountId that)) return false;
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
