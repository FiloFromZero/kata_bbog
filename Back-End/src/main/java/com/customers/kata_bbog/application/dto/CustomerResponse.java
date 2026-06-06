package com.customers.kata_bbog.application.dto;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para un cliente.
 * Se utiliza tanto para el caso de uso Create como para List.
 */
public record CustomerResponse(
    String id,
    String name,
    String email,
    LocalDateTime createdAt
) {
}
