package com.customers.kata_bbog.domain.model;

import com.customers.kata_bbog.domain.exception.InvalidCustomerDataException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Customer Domain Tests")
class CustomerTest {

    // ─────────────────────────────────────────────
    // 🔴 1.1 — Tests PRIMERO (TDD Red Phase)
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("Debe crear un cliente con datos válidos")
    void shouldCreateCustomerWithValidData() {
        Customer customer = Customer.create("Juan Pérez", "juan@email.com");

        assertNotNull(customer);
        assertNotNull(customer.getId());
        assertEquals("Juan Pérez", customer.getName().value());
        assertEquals("juan@email.com", customer.getEmail().value());
        assertNotNull(customer.getCreatedAt());
    }

    @Test
    @DisplayName("Debe rechazar nombre vacío")
    void shouldRejectEmptyName() {
        assertThrows(InvalidCustomerDataException.class,
            () -> Customer.create("", "juan@email.com"),
            "El nombre no puede estar vacío"
        );
    }

    @Test
    @DisplayName("Debe rechazar nombre nulo")
    void shouldRejectNullName() {
        assertThrows(InvalidCustomerDataException.class,
            () -> Customer.create(null, "juan@email.com"),
            "El nombre no puede ser nulo"
        );
    }

    @Test
    @DisplayName("Debe rechazar email inválido")
    void shouldRejectInvalidEmail() {
        assertThrows(InvalidCustomerDataException.class,
            () -> Customer.create("Juan", "no-es-un-email"),
            "El email debe tener un formato válido"
        );
    }

    @Test
    @DisplayName("Debe rechazar email nulo")
    void shouldRejectNullEmail() {
        assertThrows(InvalidCustomerDataException.class,
            () -> Customer.create("Juan", null),
            "El email no puede ser nulo"
        );
    }

    @Test
    @DisplayName("Debe rechazar nombre que supera el máximo de caracteres")
    void shouldRejectNameExceedingMaxLength() {
        String longName = "A".repeat(101);
        assertThrows(InvalidCustomerDataException.class,
            () -> Customer.create(longName, "juan@email.com"),
            "El nombre no puede superar los 100 caracteres"
        );
    }

    @Test
    @DisplayName("Debe generar un ID único por cada cliente")
    void shouldGenerateUniqueId() {
        Customer customer1 = Customer.create("Juan", "juan@email.com");
        Customer customer2 = Customer.create("María", "maria@email.com");

        assertNotEquals(customer1.getId().value(), customer2.getId().value());
    }

    // ─────────────────────────────────────────────
    // Value Object: CustomerId
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("Dos CustomerIds con el mismo valor deben ser iguales")
    void shouldBeEqualWhenSameId() {
        String uuid = java.util.UUID.randomUUID().toString();
        CustomerId id1 = CustomerId.of(uuid);
        CustomerId id2 = CustomerId.of(uuid);

        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    // ─────────────────────────────────────────────
    // Value Object: CustomerEmail
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("Emails iguales deben ser iguales como Value Object")
    void shouldBeEqualWhenSameEmail() {
        CustomerEmail email1 = CustomerEmail.of("test@email.com");
        CustomerEmail email2 = CustomerEmail.of("test@email.com");

        assertEquals(email1, email2);
    }

    @Test
    @DisplayName("Debe aceptar email con subdominio")
    void shouldAcceptEmailWithSubdomain() {
        assertDoesNotThrow(() -> CustomerEmail.of("user@mail.domain.com"));
    }

    // ─────────────────────────────────────────────
    // Value Object: CustomerName
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("Nombres iguales deben ser iguales como Value Object")
    void shouldBeEqualWhenSameName() {
        CustomerName name1 = CustomerName.of("Juan");
        CustomerName name2 = CustomerName.of("Juan");

        assertEquals(name1, name2);
    }

    @Test
    @DisplayName("Debe aceptar nombre con exactamente 100 caracteres")
    void shouldAcceptNameWithExactlyMaxLength() {
        String maxName = "A".repeat(100);
        assertDoesNotThrow(() -> CustomerName.of(maxName));
    }
}
