package com.customers.kata_bbog.application.service;

import com.customers.kata_bbog.application.dto.CreateCustomerCommand;
import com.customers.kata_bbog.application.dto.CustomerResponse;
import com.customers.kata_bbog.domain.exception.CustomerAlreadyExistsException;
import com.customers.kata_bbog.domain.model.Customer;
import com.customers.kata_bbog.domain.port.in.CreateCustomerUseCase;
import com.customers.kata_bbog.domain.port.out.CustomerRepository;

/**
 * Caso de uso: Crear Cliente.
 * Orquesta la lógica de negocio:
 *   1. Verifica unicidad de email
 *   2. Crea la entidad de dominio (con validaciones internas)
 *   3. Persiste a través del puerto de salida
 *   4. Retorna DTO de respuesta
 */
public class CreateCustomerService implements CreateCustomerUseCase {

    private final CustomerRepository customerRepository;

    public CreateCustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public CustomerResponse create(CreateCustomerCommand command) {
        // 1. Verificar unicidad de email antes de crear la entidad
        if (customerRepository.existsByEmail(command.email())) {
            throw new CustomerAlreadyExistsException(command.email());
        }

        // 2. Crear entidad de dominio (las validaciones ocurren aquí via Value Objects)
        Customer customer = Customer.create(command.name(), command.email());

        // 3. Persistir a través del puerto de salida
        Customer saved = customerRepository.save(customer);

        // 4. Mapear a DTO de respuesta
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
