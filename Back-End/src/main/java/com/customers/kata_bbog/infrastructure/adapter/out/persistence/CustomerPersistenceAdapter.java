package com.customers.kata_bbog.infrastructure.adapter.out.persistence;

import com.customers.kata_bbog.domain.model.Customer;
import com.customers.kata_bbog.domain.port.out.CustomerRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador de persistencia (Driven / Lado derecho).
 * Implementa el puerto de salida CustomerRepository usando Spring Data JPA.
 */
@Component
public class CustomerPersistenceAdapter implements CustomerRepository {

    private final CustomerJpaRepository jpaRepository;

    public CustomerPersistenceAdapter(CustomerJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Customer save(Customer customer) {
        CustomerJpaEntity entity = CustomerMapper.toJpaEntity(customer);
        CustomerJpaEntity saved = jpaRepository.save(entity);
        return CustomerMapper.toDomain(saved);
    }

    @Override
    public List<Customer> findAll() {
        return jpaRepository.findAll()
            .stream()
            .map(CustomerMapper::toDomain)
            .toList();
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public Optional<Customer> findById(String id) {
        return jpaRepository.findById(UUID.fromString(id))
            .map(CustomerMapper::toDomain);
    }

    @Override
    public void deleteById(String id) {
        jpaRepository.deleteById(UUID.fromString(id));
    }
}
