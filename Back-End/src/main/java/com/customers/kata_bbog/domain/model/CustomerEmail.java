package com.customers.kata_bbog.domain.model;

import com.customers.kata_bbog.domain.exception.InvalidCustomerDataException;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object — Email de un Customer.
 * Inmutable. Valida el formato de email mediante regex RFC 5322 simplificado.
 */
public final class CustomerEmail {

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final String value;

    private CustomerEmail(String value) {
        this.value = value;
    }

    public static CustomerEmail of(String value) {
        validate(value);
        return new CustomerEmail(value.toLowerCase().trim());
    }

    private static void validate(String value) {
        if (value == null) {
            throw new InvalidCustomerDataException("El email del cliente no puede ser nulo");
        }
        if (value.isBlank()) {
            throw new InvalidCustomerDataException("El email del cliente no puede estar vacío");
        }
        if (!EMAIL_PATTERN.matcher(value.trim()).matches()) {
            throw new InvalidCustomerDataException(
                "El email '" + value + "' no tiene un formato válido"
            );
        }
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomerEmail that)) return false;
        return Objects.equals(value, that.value);
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
