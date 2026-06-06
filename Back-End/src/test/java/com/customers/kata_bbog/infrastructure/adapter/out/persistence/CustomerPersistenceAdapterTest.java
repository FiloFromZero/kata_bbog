package com.customers.kata_bbog.infrastructure.adapter.out.persistence;

import com.customers.kata_bbog.domain.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(CustomerPersistenceAdapter.class)
@ActiveProfiles("test")
@DisplayName("CustomerPersistenceAdapter Integration Tests")
class CustomerPersistenceAdapterTest {

    @Autowired
    private CustomerPersistenceAdapter adapter;

    @Autowired
    private CustomerJpaRepository jpaRepository;

    @BeforeEach
    void setUp() {
        jpaRepository.deleteAll();
    }

    @Test
    @DisplayName("Debe guardar y recuperar un cliente correctamente")
    void shouldSaveAndRetrieveCustomer() {
        // GIVEN
        Customer customer = Customer.create("Juan Pérez", "juan@email.com");

        // WHEN
        Customer saved = adapter.save(customer);

        // THEN
        assertNotNull(saved);
        assertEquals(customer.getId().value(), saved.getId().value());
        assertEquals("Juan Pérez", saved.getName().value());
        assertEquals("juan@email.com", saved.getEmail().value());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    @DisplayName("Debe retornar todos los clientes guardados")
    void shouldFindAllCustomers() {
        // GIVEN
        adapter.save(Customer.create("Juan", "juan@email.com"));
        adapter.save(Customer.create("María", "maria@email.com"));

        // WHEN
        List<Customer> customers = adapter.findAll();

        // THEN
        assertEquals(2, customers.size());
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay clientes")
    void shouldReturnEmptyListWhenNoCustomers() {
        // WHEN
        List<Customer> customers = adapter.findAll();

        // THEN
        assertTrue(customers.isEmpty());
    }

    @Test
    @DisplayName("Debe detectar email existente correctamente")
    void shouldDetectExistingEmail() {
        // GIVEN
        adapter.save(Customer.create("Juan", "juan@email.com"));

        // WHEN / THEN
        assertTrue(adapter.existsByEmail("juan@email.com"));
        assertFalse(adapter.existsByEmail("otro@email.com"));
    }
}
