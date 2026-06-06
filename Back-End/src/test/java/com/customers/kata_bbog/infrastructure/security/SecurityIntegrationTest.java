package com.customers.kata_bbog.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Base64;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Security Integration Tests")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Debe retornar 401 al intentar acceder a rutas protegidas sin autenticación")
    void shouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Debe obtener token JWT usando HTTP Basic Auth válida en /auth/login")
    void shouldObtainTokenWithBasicAuth() throws Exception {
        String authHeader = "Basic " + Base64.getEncoder().encodeToString("admin-test:test-password-123".getBytes());

        mockMvc.perform(post("/auth/login")
                        .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @DisplayName("Debe retornar 401 en /auth/login con credenciales HTTP Basic inválidas")
    void shouldReturn401WithInvalidBasicAuth() throws Exception {
        String authHeader = "Basic " + Base64.getEncoder().encodeToString("admin-test:wrong-password".getBytes());

        mockMvc.perform(post("/auth/login")
                        .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Debe retornar 200 con un token JWT válido")
    void shouldReturn200WithValidToken() throws Exception {
        // Step 1: Login to get token
        String authHeader = "Basic " + Base64.getEncoder().encodeToString("admin-test:test-password-123".getBytes());

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = loginResult.getResponse().getContentAsString();
        Map<?, ?> responseMap = objectMapper.readValue(responseBody, Map.class);
        String token = (String) responseMap.get("token");

        // Step 2: Access protected resource
        mockMvc.perform(get("/api/customers")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Debe retornar 401 con un token JWT inválido")
    void shouldReturn401WithInvalidToken() throws Exception {
        mockMvc.perform(get("/api/customers")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token-string"))
                .andExpect(status().isUnauthorized());
    }
}
