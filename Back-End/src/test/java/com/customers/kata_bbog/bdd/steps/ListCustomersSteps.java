package com.customers.kata_bbog.bdd.steps;

import com.customers.kata_bbog.application.dto.CreateCustomerCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ListCustomersSteps {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Given("existen los siguientes clientes:")
    public void existenLosSiguientesClientes(io.cucumber.datatable.DataTable dataTable) throws Exception {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> row : rows) {
            CreateCustomerCommand command = new CreateCustomerCommand(row.get("name"), row.get("email"));
            mockMvc.perform(post("/api/customers")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + CucumberSpringConfiguration.token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(command)))
                    .andExpect(status().isCreated());
        }
    }

    @When("envío una petición GET a {string}")
    public void envioUnaPeticionGETA(String path) throws Exception {
        CucumberSpringConfiguration.latestResponse = mockMvc.perform(get(path)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + CucumberSpringConfiguration.token));
    }

    @And("la respuesta debe contener {int} clientes")
    public void laRespuestaDebeContenerClientes(int count) throws Exception {
        CucumberSpringConfiguration.latestResponse.andExpect(jsonPath("$.length()").value(count));
    }
}
