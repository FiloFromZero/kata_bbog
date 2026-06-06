package com.customers.kata_bbog.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data JPA Repository para CustomerJpaEntity.
 * Spring genera la implementación automáticamente.
 */
public interface CustomerJpaRepository extends JpaRepository<CustomerJpaEntity, UUID> {

    boolean existsByEmail(String email);
}
