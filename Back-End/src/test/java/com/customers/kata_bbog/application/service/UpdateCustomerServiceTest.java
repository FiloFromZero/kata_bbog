package com.customers.kata_bbog.application.service;

import com.customers.kata_bbog.application.dto.CustomerResponse;
import com.customers.kata_bbog.application.dto.UpdateCustomerCommand;
import com.customers.kata_bbog.domain.exception.CustomerAlreadyExistsException;
import com.customers.kata_bbog.domain.exception.CustomerNotFoundException;
import com.customers.kata_bbog.domain.exception.InvalidCustomerDataException;
import com.customers.kata_bbog.domain.model.Customer;
import com.customers.kata_bbog.domain.port.out.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateCustomerService Tests")
class UpdateCustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private UpdateCustomerService service;

    @Test
    @DisplayName("Debe actualizar un cliente correctamente sin cambiar el email")
    void shouldUpdateCustomerSuccessfullyWithoutChangingEmail() {
        // GIVEN
        String customerId = "uuid-123";
        Customer customer = Customer.reconstitute(customerId, "Juan", "juan@email.com", LocalDateTime.now());
        UpdateCustomerCommand command = new UpdateCustomerCommand("Juan Modificado", "juan@email.com");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        when(customerRepository.save(captor.capture())).thenAnswer(inv -> captor.getValue());

        // WHEN
        CustomerResponse response = service.update(customerId, command);

        // THEN
        assertNotNull(response);
        assertEquals(customerId, response.id());
        assertEquals("Juan Modificado", response.name());
        assertEquals("juan@email.com", response.email());

        verify(customerRepository).findById(customerId);
        verify(customerRepository, never()).existsByEmail(anyString());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Debe actualizar un cliente correctamente cambiando el email si no existe duplicado")
    void shouldUpdateCustomerSuccessfullyWithNewEmail() {
        // GIVEN
        String customerId = "uuid-123";
        Customer customer = Customer.reconstitute(customerId, "Juan", "juan@email.com", LocalDateTime.now());
        UpdateCustomerCommand command = new UpdateCustomerCommand("Juan", "nuevo@email.com");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.existsByEmail("nuevo@email.com")).thenReturn(false);
        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        when(customerRepository.save(captor.capture())).thenAnswer(inv -> captor.getValue());

        // WHEN
        CustomerResponse response = service.update(customerId, command);

        // THEN
        assertNotNull(response);
        assertEquals("nuevo@email.com", response.email());

        verify(customerRepository).findById(customerId);
        verify(customerRepository).existsByEmail("nuevo@email.com");
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Debe lanzar CustomerAlreadyExistsException al cambiar el email a uno ya registrado por otro cliente")
    void shouldThrowWhenNewEmailAlreadyExists() {
        // GIVEN
        String customerId = "uuid-123";
        Customer customer = Customer.reconstitute(customerId, "Juan", "juan@email.com", LocalDateTime.now());
        UpdateCustomerCommand command = new UpdateCustomerCommand("Juan", "otro@email.com");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.existsByEmail("otro@email.com")).thenReturn(true);

        // WHEN / THEN
        assertThrows(CustomerAlreadyExistsException.class, () -> service.update(customerId, command));

        verify(customerRepository).findById(customerId);
        verify(customerRepository).existsByEmail("otro@email.com");
        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar CustomerNotFoundException cuando el cliente a actualizar no existe")
    void shouldThrowWhenCustomerToUpdateDoesNotExist() {
        // GIVEN
        String customerId = "uuid-not-exists";
        UpdateCustomerCommand command = new UpdateCustomerCommand("Juan", "juan@email.com");
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThrows(CustomerNotFoundException.class, () -> service.update(customerId, command));

        verify(customerRepository).findById(customerId);
        verify(customerRepository, never()).existsByEmail(anyString());
        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar InvalidCustomerDataException si los nuevos datos son inválidos")
    void shouldThrowWhenNewNameIsEmpty() {
        // GIVEN
        String customerId = "uuid-123";
        Customer customer = Customer.reconstitute(customerId, "Juan", "juan@email.com", LocalDateTime.now());
        UpdateCustomerCommand command = new UpdateCustomerCommand("", "juan@email.com");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        // WHEN / THEN
        assertThrows(InvalidCustomerDataException.class, () -> service.update(customerId, command));
        verify(customerRepository, never()).save(any());
    }
}
