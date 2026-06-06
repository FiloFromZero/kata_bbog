package com.customers.kata_bbog.application.dto;

/**
 * Comando de entrada para el caso de uso CreateCustomer.
 * Datos crudos del request HTTP, aún sin validar a nivel de dominio.
 */
public record CreateCustomerCommand(String name, String email) {
}
