package com.customers.kata_bbog.bdd.steps;

import com.customers.kata_bbog.application.dto.UpdateCustomerCommand;
import com.customers.kata_bbog.infrastructure.adapter.out.persistence.CustomerJpaRepository;
import com.customers.kata_bbog.infrastructure.adapter.out.persistence.CustomerJpaEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class UpdateCustomerSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerJpaRepository jpaRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @When("envío una petición PUT a {string} para el cliente con email {string} con:")
    public void envioUnaPeticionPUTAParaElClienteConEmailCon(String path, String oldEmail, io.cucumber.datatable.DataTable dataTable) throws Exception {
        CustomerJpaEntity entity = jpaRepository.findByEmail(oldEmail)
                .orElseThrow(() -> new AssertionError("Cliente no encontrado para el email: " + oldEmail));

        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        Map<String, String> data = rows.get(0);
        UpdateCustomerCommand command = new UpdateCustomerCommand(data.get("name"), data.get("email"));

        CucumberSpringConfiguration.latestResponse = mockMvc.perform(put(path + "/" + entity.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + CucumberSpringConfiguration.token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)));
    }

    @When("envío una petición PUT a {string} con:")
    public void envioUnaPeticionPUTACon(String path, io.cucumber.datatable.DataTable dataTable) throws Exception {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        Map<String, String> data = rows.get(0);
        UpdateCustomerCommand command = new UpdateCustomerCommand(data.get("name"), data.get("email"));

        CucumberSpringConfiguration.latestResponse = mockMvc.perform(put(path)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + CucumberSpringConfiguration.token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)));
    }
}
