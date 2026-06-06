package com.customers.kata_bbog.infrastructure.adapter.out.persistence;

import com.customers.kata_bbog.domain.model.Customer;

import java.util.UUID;

/**
 * Mapper — Convierte entre Customer (dominio) y CustomerJpaEntity (infraestructura).
 * Mantiene la separación entre capas.
 */
public class CustomerMapper {

    private CustomerMapper() {
        // Utility class
    }

    /** Dominio → JPA Entity */
    public static CustomerJpaEntity toJpaEntity(Customer customer) {
        return new CustomerJpaEntity(
            UUID.fromString(customer.getId().value()),
            customer.getName().value(),
            customer.getEmail().value(),
            customer.getCreatedAt()
        );
    }

    /** JPA Entity → Dominio */
    public static Customer toDomain(CustomerJpaEntity entity) {
        return Customer.reconstitute(
            entity.getId().toString(),
            entity.getName(),
            entity.getEmail(),
            entity.getCreatedAt()
        );
    }
}
