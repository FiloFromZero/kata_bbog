package com.customers.kata_bbog.domain.port.in;

import com.customers.kata_bbog.application.dto.CustomerResponse;

import java.util.List;

/**
 * Puerto de entrada (Driving Port) — Caso de uso: Listar Clientes.
 * Implementado por la capa de aplicación (ListCustomersService).
 */
public interface ListCustomersUseCase {

    List<CustomerResponse> listAll();
}
