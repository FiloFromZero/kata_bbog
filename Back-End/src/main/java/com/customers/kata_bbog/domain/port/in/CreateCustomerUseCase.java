package com.customers.kata_bbog.domain.port.in;

import com.customers.kata_bbog.application.dto.CreateCustomerCommand;
import com.customers.kata_bbog.application.dto.CustomerResponse;

/**
 * Puerto de entrada (Driving Port) — Caso de uso: Crear Cliente.
 * Implementado por la capa de aplicación (CreateCustomerService).
 */
public interface CreateCustomerUseCase {

    CustomerResponse create(CreateCustomerCommand command);
}
