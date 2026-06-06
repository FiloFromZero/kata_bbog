package com.customers.kata_bbog.domain.exception;

public class InvalidCustomerDataException extends RuntimeException {

    public InvalidCustomerDataException(String message) {
        super(message);
    }
}
