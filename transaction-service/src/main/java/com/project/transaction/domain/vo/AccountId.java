package com.project.transaction.domain.vo;

import java.util.Objects;

/**
 * Identity value object for an account referenced by a transaction. A transaction
 * may legitimately omit a source (deposit) or destination (withdrawal), so the
 * aggregate holds nullable {@code AccountId} references; when present the value
 * must be non-blank.
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

    /** Builds an {@code AccountId} or {@code null} if the raw value is absent. */
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
