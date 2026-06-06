package com.customers.kata_bbog.application.service;

import com.customers.kata_bbog.domain.exception.CustomerNotFoundException;
import com.customers.kata_bbog.domain.port.in.DeleteCustomerUseCase;
import com.customers.kata_bbog.domain.port.out.CustomerRepository;

/**
 * Caso de uso: Eliminar Cliente.
 */
public class DeleteCustomerService implements DeleteCustomerUseCase {

    private final CustomerRepository customerRepository;

    public DeleteCustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public void delete(String id) {
        if (customerRepository.findById(id).isEmpty()) {
            throw new CustomerNotFoundException(id);
        }
        customerRepository.deleteById(id);
    }
}
