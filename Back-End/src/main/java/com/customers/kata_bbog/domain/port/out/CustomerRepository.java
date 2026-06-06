package com.customers.kata_bbog.domain.port.out;

import com.customers.kata_bbog.domain.model.Customer;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida (Driven Port).
 * Define el contrato de persistencia de clientes.
 * Implementado por el adaptador de infraestructura.
 */
public interface CustomerRepository {

    Customer save(Customer customer);

    List<Customer> findAll();

    boolean existsByEmail(String email);

    Optional<Customer> findById(String id);

    void deleteById(String id);
}
