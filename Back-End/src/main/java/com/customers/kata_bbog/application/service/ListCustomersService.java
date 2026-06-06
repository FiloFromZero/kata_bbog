package com.customers.kata_bbog.application.service;

import com.customers.kata_bbog.application.dto.CustomerResponse;
import com.customers.kata_bbog.domain.model.Customer;
import com.customers.kata_bbog.domain.port.in.ListCustomersUseCase;
import com.customers.kata_bbog.domain.port.out.CustomerRepository;

import java.util.List;

/**
 * Caso de uso: Listar Clientes.
 * Orquesta la lógica de consulta:
 *   1. Obtiene todos los clientes a través del puerto de salida
 *   2. Mapea cada entidad de dominio a DTO de respuesta
 */
public class ListCustomersService implements ListCustomersUseCase {

    private final CustomerRepository customerRepository;

    public ListCustomersService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<CustomerResponse> listAll() {
        return customerRepository.findAll()
            .stream()
            .map(this::toResponse)
            .toList();
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
