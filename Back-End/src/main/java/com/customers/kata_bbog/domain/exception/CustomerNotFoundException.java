package com.customers.kata_bbog.domain.exception;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(String id) {
        super("No se encontró ningún cliente con el ID: " + id);
    }
}
