package com.customers.kata_bbog.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Aggregate Root — Entidad principal del dominio.
 * Representa un cliente registrado en el sistema.
 * Sin dependencias de Spring, JPA, ni ningún framework externo.
 */
public final class Customer {

    private final CustomerId id;
    private final CustomerName name;
    private final CustomerEmail email;
    private final LocalDateTime createdAt;

    private Customer(CustomerId id, CustomerName name, CustomerEmail email, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
    }

    /**
     * Factory method — Crea un nuevo Customer con validaciones.
     * Genera automáticamente un ID único y marca la fecha de creación.
     */
    public static Customer create(String name, String email) {
        return new Customer(
            CustomerId.generate(),
            CustomerName.of(name),
            CustomerEmail.of(email),
            LocalDateTime.now()
        );
    }

    /**
     * Factory method — Reconstruye un Customer desde persistencia.
     */
    public static Customer reconstitute(String id, String name, String email, LocalDateTime createdAt) {
        return new Customer(
            CustomerId.of(id),
            CustomerName.of(name),
            CustomerEmail.of(email),
            createdAt
        );
    }

    /**
     * Devuelve un nuevo Customer con los detalles actualizados.
     */
    public Customer updateDetails(String newName, String newEmail) {
        return new Customer(
            this.id,
            CustomerName.of(newName),
            CustomerEmail.of(newEmail),
            this.createdAt
        );
    }

    public CustomerId getId() {
        return id;
    }

    public CustomerName getName() {
        return name;
    }

    public CustomerEmail getEmail() {
        return email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Customer{id=" + id + ", name=" + name + ", email=" + email + "}";
    }
}
