package com.customers.kata_bbog.bdd.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Base64;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CommonSteps {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Given("el usuario está autenticado con un token JWT válido")
    public void elUsuarioEstaAutenticadoConUnTokenJWTValido() throws Exception {
        String authHeader = "Basic " + Base64.getEncoder().encodeToString("admin-test:test-password-123".getBytes());

        var result = mockMvc.perform(post("/auth/login")
                        .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        Map<?, ?> map = objectMapper.readValue(responseBody, Map.class);
        CucumberSpringConfiguration.token = (String) map.get("token");
    }
}
