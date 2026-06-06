package com.customers.kata_bbog.application.service;

import com.customers.kata_bbog.application.dto.CustomerResponse;
import com.customers.kata_bbog.domain.model.Customer;
import com.customers.kata_bbog.domain.port.out.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListCustomersService Tests")
class ListCustomersServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private ListCustomersService service;

    @Test
    @DisplayName("Debe retornar lista de clientes mapeada a DTOs")
    void shouldReturnListOfCustomersMappedToDto() {
        // GIVEN
        Customer customer1 = Customer.create("Juan", "juan@email.com");
        Customer customer2 = Customer.create("María", "maria@email.com");
        when(customerRepository.findAll()).thenReturn(List.of(customer1, customer2));

        // WHEN
        List<CustomerResponse> result = service.listAll();

        // THEN
        assertEquals(2, result.size());

        CustomerResponse first = result.get(0);
        assertEquals(customer1.getId().value(), first.id());
        assertEquals("Juan", first.name());
        assertEquals("juan@email.com", first.email());
        assertNotNull(first.createdAt());

        verify(customerRepository).findAll();
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay clientes")
    void shouldReturnEmptyListWhenNoCustomers() {
        // GIVEN
        when(customerRepository.findAll()).thenReturn(List.of());

        // WHEN
        List<CustomerResponse> result = service.listAll();

        // THEN
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(customerRepository).findAll();
    }

    @Test
    @DisplayName("Cada cliente en la respuesta debe tener ID, nombre, email y fecha")
    void shouldMapAllFieldsCorrectly() {
        // GIVEN
        Customer customer = Customer.create("Ana Gómez", "ana@email.com");
        when(customerRepository.findAll()).thenReturn(List.of(customer));

        // WHEN
        List<CustomerResponse> result = service.listAll();

        // THEN
        assertEquals(1, result.size());
        CustomerResponse response = result.get(0);
        assertNotNull(response.id());
        assertEquals("Ana Gómez", response.name());
        assertEquals("ana@email.com", response.email());
        assertNotNull(response.createdAt());
    }
}
