package com.customers.kata_bbog.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object — Identificador único de un Customer.
 * Inmutable, encapsula un UUID.
 */
public final class CustomerId {

    private final String value;

    private CustomerId(String value) {
        this.value = value;
    }

    /** Genera un nuevo ID aleatorio. */
    public static CustomerId generate() {
        return new CustomerId(UUID.randomUUID().toString());
    }

    /** Reconstruye un ID a partir de un valor existente (ej: desde BD). */
    public static CustomerId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El ID de cliente no puede ser nulo o vacío");
        }
        return new CustomerId(value);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomerId that)) return false;
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
