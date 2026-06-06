package com.customers.kata_bbog.application.service;

import com.customers.kata_bbog.application.dto.CreateCustomerCommand;
import com.customers.kata_bbog.application.dto.CustomerResponse;
import com.customers.kata_bbog.domain.exception.CustomerAlreadyExistsException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateCustomerService Tests")
class CreateCustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CreateCustomerService service;

    // ─────────────────────────────────────────────
    // 🔴 2.1 — Tests PRIMERO (TDD Red Phase)
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("Debe crear un cliente correctamente cuando el email no existe")
    void shouldCreateCustomerSuccessfully() {
        // GIVEN
        CreateCustomerCommand command = new CreateCustomerCommand("Juan Pérez", "juan@email.com");
        when(customerRepository.existsByEmail("juan@email.com")).thenReturn(false);

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        when(customerRepository.save(captor.capture())).thenAnswer(inv -> captor.getValue());

        // WHEN
        CustomerResponse response = service.create(command);

        // THEN
        assertNotNull(response);
        assertNotNull(response.id());
        assertEquals("Juan Pérez", response.name());
        assertEquals("juan@email.com", response.email());
        assertNotNull(response.createdAt());

        verify(customerRepository).existsByEmail("juan@email.com");
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Debe lanzar CustomerAlreadyExistsException cuando el email ya está registrado")
    void shouldThrowWhenEmailAlreadyExists() {
        // GIVEN
        CreateCustomerCommand command = new CreateCustomerCommand("Pedro", "juan@email.com");
        when(customerRepository.existsByEmail("juan@email.com")).thenReturn(true);

        // WHEN / THEN
        assertThrows(CustomerAlreadyExistsException.class, () -> service.create(command));

        verify(customerRepository).existsByEmail("juan@email.com");
        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar InvalidCustomerDataException cuando el nombre está vacío")
    void shouldThrowWhenNameIsEmpty() {
        // GIVEN
        CreateCustomerCommand command = new CreateCustomerCommand("", "juan@email.com");
        when(customerRepository.existsByEmail("juan@email.com")).thenReturn(false);

        // WHEN / THEN
        assertThrows(InvalidCustomerDataException.class, () -> service.create(command));
        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar InvalidCustomerDataException cuando el email es inválido")
    void shouldThrowWhenEmailIsInvalid() {
        // GIVEN
        CreateCustomerCommand command = new CreateCustomerCommand("Juan", "no-es-email");
        when(customerRepository.existsByEmail("no-es-email")).thenReturn(false);

        // WHEN / THEN
        assertThrows(InvalidCustomerDataException.class, () -> service.create(command));
        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("El email debe ser normalizado a minúsculas al crear el cliente")
    void shouldNormalizeEmailToLowercase() {
        // GIVEN
        CreateCustomerCommand command = new CreateCustomerCommand("Ana", "ANA@EMAIL.COM");
        when(customerRepository.existsByEmail("ANA@EMAIL.COM")).thenReturn(false);

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        when(customerRepository.save(captor.capture())).thenAnswer(inv -> captor.getValue());

        // WHEN
        CustomerResponse response = service.create(command);

        // THEN
        assertEquals("ana@email.com", response.email());
    }
}
