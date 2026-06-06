package com.customers.kata_bbog.domain.port.in;

import com.customers.kata_bbog.application.dto.CustomerResponse;
import com.customers.kata_bbog.application.dto.UpdateCustomerCommand;

/**
 * Puerto de entrada (Driving Port) — Caso de uso: Actualizar Cliente.
 * Implementado por la capa de aplicación (UpdateCustomerService).
 */
public interface UpdateCustomerUseCase {

    CustomerResponse update(String id, UpdateCustomerCommand command);
}
