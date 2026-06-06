package com.customers.kata_bbog.application.service;

import com.customers.kata_bbog.domain.exception.CustomerNotFoundException;
import com.customers.kata_bbog.domain.model.Customer;
import com.customers.kata_bbog.domain.port.out.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteCustomerService Tests")
class DeleteCustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private DeleteCustomerService service;

    @Test
    @DisplayName("Debe eliminar un cliente correctamente cuando existe")
    void shouldDeleteCustomerSuccessfully() {
        // GIVEN
        String customerId = "uuid-123";
        Customer customer = Customer.reconstitute(customerId, "Juan", "juan@email.com", java.time.LocalDateTime.now());
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        // WHEN
        service.delete(customerId);

        // THEN
        verify(customerRepository).findById(customerId);
        verify(customerRepository).deleteById(customerId);
    }

    @Test
    @DisplayName("Debe lanzar CustomerNotFoundException cuando el cliente no existe")
    void shouldThrowWhenCustomerDoesNotExist() {
        // GIVEN
        String customerId = "uuid-not-exists";
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThrows(CustomerNotFoundException.class, () -> service.delete(customerId));

        verify(customerRepository).findById(customerId);
        verify(customerRepository, never()).deleteById(anyString());
    }
}
