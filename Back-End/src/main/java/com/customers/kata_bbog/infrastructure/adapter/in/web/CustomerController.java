package com.customers.kata_bbog.infrastructure.adapter.in.web;

import com.customers.kata_bbog.application.dto.CreateCustomerCommand;
import com.customers.kata_bbog.application.dto.CustomerResponse;
import com.customers.kata_bbog.domain.port.in.CreateCustomerUseCase;
import com.customers.kata_bbog.domain.port.in.ListCustomersUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Adaptador REST (Driving / Lado izquierdo).
 * Expone los endpoints HTTP y delega la lógica a los puertos de entrada.
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CreateCustomerUseCase createCustomerUseCase;
    private final ListCustomersUseCase listCustomersUseCase;

    public CustomerController(
            CreateCustomerUseCase createCustomerUseCase,
            ListCustomersUseCase listCustomersUseCase) {
        this.createCustomerUseCase = createCustomerUseCase;
        this.listCustomersUseCase = listCustomersUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse create(@Valid @RequestBody CreateCustomerCommand command) {
        return createCustomerUseCase.create(command);
    }

    @GetMapping
    public List<CustomerResponse> listAll() {
        return listCustomersUseCase.listAll();
    }
}
