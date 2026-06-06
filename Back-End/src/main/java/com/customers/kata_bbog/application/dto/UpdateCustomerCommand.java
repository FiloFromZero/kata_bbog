package com.customers.kata_bbog.application.dto;

/**
 * Comando de entrada para el caso de uso UpdateCustomer.
 * Datos crudos del request HTTP, aún sin validar a nivel de dominio.
 */
public record UpdateCustomerCommand(String name, String email) {
}
