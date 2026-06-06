package com.customers.kata_bbog.infrastructure.adapter.in.web;

import com.customers.kata_bbog.application.dto.CreateCustomerCommand;
import com.customers.kata_bbog.application.dto.UpdateCustomerCommand;
import com.customers.kata_bbog.application.dto.CustomerResponse;
import com.customers.kata_bbog.domain.port.in.CreateCustomerUseCase;
import com.customers.kata_bbog.domain.port.in.DeleteCustomerUseCase;
import com.customers.kata_bbog.domain.port.in.ListCustomersUseCase;
import com.customers.kata_bbog.domain.port.in.UpdateCustomerUseCase;
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
    private final DeleteCustomerUseCase deleteCustomerUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;

    public CustomerController(
            CreateCustomerUseCase createCustomerUseCase,
            ListCustomersUseCase listCustomersUseCase,
            DeleteCustomerUseCase deleteCustomerUseCase,
            UpdateCustomerUseCase updateCustomerUseCase) {
        this.createCustomerUseCase = createCustomerUseCase;
        this.listCustomersUseCase = listCustomersUseCase;
        this.deleteCustomerUseCase = deleteCustomerUseCase;
        this.updateCustomerUseCase = updateCustomerUseCase;
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

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        deleteCustomerUseCase.delete(id);
    }

    @PutMapping("/{id}")
    public CustomerResponse update(@PathVariable String id, @Valid @RequestBody UpdateCustomerCommand command) {
        return updateCustomerUseCase.update(id, command);
    }
}
