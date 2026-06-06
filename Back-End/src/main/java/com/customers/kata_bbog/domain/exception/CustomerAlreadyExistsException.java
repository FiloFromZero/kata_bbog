package com.customers.kata_bbog.domain.exception;

public class CustomerAlreadyExistsException extends RuntimeException {

    public CustomerAlreadyExistsException(String email) {
        super("Ya existe un cliente registrado con el email: " + email);
    }
}
