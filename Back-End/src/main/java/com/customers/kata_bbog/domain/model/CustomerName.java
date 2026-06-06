package com.customers.kata_bbog.domain.model;

import com.customers.kata_bbog.domain.exception.InvalidCustomerDataException;

import java.util.Objects;

/**
 * Value Object — Nombre de un Customer.
 * Inmutable. Valida que no sea vacío ni supere 100 caracteres.
 */
public final class CustomerName {

    private static final int MAX_LENGTH = 100;

    private final String value;

    private CustomerName(String value) {
        this.value = value;
    }

    public static CustomerName of(String value) {
        validate(value);
        return new CustomerName(value.trim());
    }

    private static void validate(String value) {
        if (value == null) {
            throw new InvalidCustomerDataException("El nombre del cliente no puede ser nulo");
        }
        if (value.isBlank()) {
            throw new InvalidCustomerDataException("El nombre del cliente no puede estar vacío");
        }
        if (value.trim().length() > MAX_LENGTH) {
            throw new InvalidCustomerDataException(
                "El nombre del cliente no puede superar los " + MAX_LENGTH + " caracteres"
            );
        }
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomerName that)) return false;
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
