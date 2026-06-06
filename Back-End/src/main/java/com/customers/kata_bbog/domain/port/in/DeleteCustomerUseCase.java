package com.customers.kata_bbog.domain.port.in;

/**
 * Puerto de entrada (Driving Port) — Caso de uso: Eliminar Cliente.
 * Implementado por la capa de aplicación (DeleteCustomerService).
 */
public interface DeleteCustomerUseCase {

    void delete(String id);
}
