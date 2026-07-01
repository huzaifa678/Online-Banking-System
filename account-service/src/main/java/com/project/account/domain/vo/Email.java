package com.project.account.domain.vo;

import java.util.Objects;

/**
 * Identity value object representing an email address. It encapsulates the email as a string and provides validation to ensure it is not null or blank.
 */
public final class Email {

    private final String value;

    private Email(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        this.value = value;
    }

    public static Email of(String value) {
        return new Email(value);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email email)) return false;
        return value.equals(email.value);
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
