package com.customers.kata_bbog.infrastructure.adapter.in.web;

import com.customers.kata_bbog.application.dto.CreateCustomerCommand;
import com.customers.kata_bbog.application.dto.UpdateCustomerCommand;
import com.customers.kata_bbog.application.dto.CustomerResponse;
import com.customers.kata_bbog.domain.exception.CustomerAlreadyExistsException;
import com.customers.kata_bbog.domain.exception.CustomerNotFoundException;
import com.customers.kata_bbog.domain.exception.InvalidCustomerDataException;
import com.customers.kata_bbog.domain.port.in.CreateCustomerUseCase;
import com.customers.kata_bbog.domain.port.in.DeleteCustomerUseCase;
import com.customers.kata_bbog.domain.port.in.ListCustomersUseCase;
import com.customers.kata_bbog.domain.port.in.UpdateCustomerUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = CustomerController.class,
    excludeAutoConfiguration = SecurityAutoConfiguration.class
)
@DisplayName("CustomerController Integration Tests")
class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateCustomerUseCase createCustomerUseCase;

    @MockitoBean
    private ListCustomersUseCase listCustomersUseCase;

    @MockitoBean
    private DeleteCustomerUseCase deleteCustomerUseCase;

    @MockitoBean
    private UpdateCustomerUseCase updateCustomerUseCase;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    @DisplayName("POST /api/customers — Debe crear cliente y retornar 201")
    void shouldCreateCustomerAndReturn201() throws Exception {
        // GIVEN
        CreateCustomerCommand command = new CreateCustomerCommand("Juan Pérez", "juan@email.com");
        CustomerResponse response = new CustomerResponse(
            "uuid-123", "Juan Pérez", "juan@email.com", LocalDateTime.now()
        );
        when(createCustomerUseCase.create(any())).thenReturn(response);

        // WHEN / THEN
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("uuid-123"))
            .andExpect(jsonPath("$.name").value("Juan Pérez"))
            .andExpect(jsonPath("$.email").value("juan@email.com"))
            .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("POST /api/customers — Debe retornar 409 cuando email ya existe")
    void shouldReturn409WhenEmailAlreadyExists() throws Exception {
        // GIVEN
        CreateCustomerCommand command = new CreateCustomerCommand("Pedro", "juan@email.com");
        when(createCustomerUseCase.create(any()))
            .thenThrow(new CustomerAlreadyExistsException("juan@email.com"));

        // WHEN / THEN
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.title").value("Cliente duplicado"));
    }

    @Test
    @DisplayName("POST /api/customers — Debe retornar 400 con datos inválidos")
    void shouldReturn400WithInvalidData() throws Exception {
        // GIVEN
        CreateCustomerCommand command = new CreateCustomerCommand("", "no-es-email");
        when(createCustomerUseCase.create(any()))
            .thenThrow(new InvalidCustomerDataException("El nombre del cliente no puede estar vacío"));

        // WHEN / THEN
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").value("Datos de cliente inválidos"));
    }

    @Test
    @DisplayName("GET /api/customers — Debe retornar lista de clientes con 200")
    void shouldReturnCustomerListWith200() throws Exception {
        // GIVEN
        List<CustomerResponse> customers = List.of(
            new CustomerResponse("id-1", "Juan", "juan@email.com", LocalDateTime.now()),
            new CustomerResponse("id-2", "María", "maria@email.com", LocalDateTime.now())
        );
        when(listCustomersUseCase.listAll()).thenReturn(customers);

        // WHEN / THEN
        mockMvc.perform(get("/api/customers"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].name").value("Juan"))
            .andExpect(jsonPath("$[1].name").value("María"));
    }

    @Test
    @DisplayName("GET /api/customers — Debe retornar lista vacía con 200")
    void shouldReturnEmptyListWith200() throws Exception {
        // GIVEN
        when(listCustomersUseCase.listAll()).thenReturn(List.of());

        // WHEN / THEN
        mockMvc.perform(get("/api/customers"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("DELETE /api/customers/{id} — Debe eliminar cliente y retornar 204")
    void shouldDeleteCustomerAndReturn204() throws Exception {
        // WHEN / THEN
        mockMvc.perform(delete("/api/customers/uuid-123"))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/customers/{id} — Debe retornar 404 si el cliente no existe")
    void shouldReturn404OnDeleteWhenCustomerNotFound() throws Exception {
        // GIVEN
        doThrow(new CustomerNotFoundException("uuid-123")).when(deleteCustomerUseCase).delete("uuid-123");

        // WHEN / THEN
        mockMvc.perform(delete("/api/customers/uuid-123"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title").value("Cliente no encontrado"));
    }

    @Test
    @DisplayName("PUT /api/customers/{id} — Debe actualizar cliente y retornar 200")
    void shouldUpdateCustomerAndReturn200() throws Exception {
        // GIVEN
        UpdateCustomerCommand command = new UpdateCustomerCommand("Juan Modificado", "juan@email.com");
        CustomerResponse response = new CustomerResponse(
            "uuid-123", "Juan Modificado", "juan@email.com", LocalDateTime.now()
        );
        when(updateCustomerUseCase.update(any(), any())).thenReturn(response);

        // WHEN / THEN
        mockMvc.perform(put("/api/customers/uuid-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("uuid-123"))
            .andExpect(jsonPath("$.name").value("Juan Modificado"))
            .andExpect(jsonPath("$.email").value("juan@email.com"));
    }

    @Test
    @DisplayName("PUT /api/customers/{id} — Debe retornar 404 si el cliente no existe")
    void shouldReturn404OnUpdateWhenCustomerNotFound() throws Exception {
        // GIVEN
        UpdateCustomerCommand command = new UpdateCustomerCommand("Juan", "juan@email.com");
        when(updateCustomerUseCase.update(any(), any()))
            .thenThrow(new CustomerNotFoundException("uuid-123"));

        // WHEN / THEN
        mockMvc.perform(put("/api/customers/uuid-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title").value("Cliente no encontrado"));
    }

    @Test
    @DisplayName("PUT /api/customers/{id} — Debe retornar 409 si el email ya existe")
    void shouldReturn409OnUpdateWhenEmailExists() throws Exception {
        // GIVEN
        UpdateCustomerCommand command = new UpdateCustomerCommand("Juan", "juan@email.com");
        when(updateCustomerUseCase.update(any(), any()))
            .thenThrow(new CustomerAlreadyExistsException("juan@email.com"));

        // WHEN / THEN
        mockMvc.perform(put("/api/customers/uuid-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.title").value("Cliente duplicado"));
    }

    @Test
    @DisplayName("PUT /api/customers/{id} — Debe retornar 400 con datos inválidos")
    void shouldReturn400OnUpdateWithInvalidData() throws Exception {
        // GIVEN
        UpdateCustomerCommand command = new UpdateCustomerCommand("", "no-es-email");
        when(updateCustomerUseCase.update(any(), any()))
            .thenThrow(new InvalidCustomerDataException("El nombre del cliente no puede estar vacío"));

        // WHEN / THEN
        mockMvc.perform(put("/api/customers/uuid-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").value("Datos de cliente inválidos"));
    }
}
