package com.customers.kata_bbog.application.service;

import com.customers.kata_bbog.application.dto.CustomerResponse;
import com.customers.kata_bbog.application.dto.UpdateCustomerCommand;
import com.customers.kata_bbog.domain.exception.CustomerAlreadyExistsException;
import com.customers.kata_bbog.domain.exception.CustomerNotFoundException;
import com.customers.kata_bbog.domain.model.Customer;
import com.customers.kata_bbog.domain.port.in.UpdateCustomerUseCase;
import com.customers.kata_bbog.domain.port.out.CustomerRepository;

/**
 * Caso de uso: Actualizar Cliente.
 */
public class UpdateCustomerService implements UpdateCustomerUseCase {

    private final CustomerRepository customerRepository;

    public UpdateCustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public CustomerResponse update(String id, UpdateCustomerCommand command) {
        // 1. Obtener cliente existente
        Customer existingCustomer = customerRepository.findById(id)
            .orElseThrow(() -> new CustomerNotFoundException(id));

        // 2. Verificar que el nuevo email no exista para otro cliente (si cambia)
        if (!existingCustomer.getEmail().value().equalsIgnoreCase(command.email()) &&
            customerRepository.existsByEmail(command.email())) {
            throw new CustomerAlreadyExistsException(command.email());
        }

        // 3. Crear nuevo estado (la validación de datos ocurre en los Value Objects del dominio)
        Customer updatedCustomer = existingCustomer.updateDetails(command.name(), command.email());

        // 4. Guardar cambios en persistencia
        Customer saved = customerRepository.save(updatedCustomer);

        // 5. Retornar DTO de respuesta
        return toResponse(saved);
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
            customer.getId().value(),
            customer.getName().value(),
            customer.getEmail().value(),
            customer.getCreatedAt()
        );
    }
}
