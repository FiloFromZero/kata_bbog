package com.customers.kata_bbog.bdd.steps;

import com.customers.kata_bbog.application.dto.CreateCustomerCommand;
import com.customers.kata_bbog.infrastructure.adapter.out.persistence.CustomerJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CreateCustomerSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerJpaRepository jpaRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void cleanDatabase() {
        jpaRepository.deleteAll();
    }

    @When("envío una petición POST a {string} con:")
    public void envioUnaPeticionPOSTACon(String path, io.cucumber.datatable.DataTable dataTable) throws Exception {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        Map<String, String> data = rows.get(0);
        CreateCustomerCommand command = new CreateCustomerCommand(data.get("name"), data.get("email"));

        CucumberSpringConfiguration.latestResponse = mockMvc.perform(post(path)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + CucumberSpringConfiguration.token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)));
    }

    @Then("la respuesta debe tener código {int}")
    public void laRespuestaDebeTenerCodigo(int statusCode) throws Exception {
        CucumberSpringConfiguration.latestResponse.andExpect(status().is(statusCode));
    }

    @And("la respuesta debe contener el nombre {string}")
    public void laRespuestaDebeContenerElNombre(String expectedName) throws Exception {
        CucumberSpringConfiguration.latestResponse.andExpect(jsonPath("$.name").value(expectedName));
    }

    @And("la respuesta debe contener el email {string}")
    public void laRespuestaDebeContenerElEmail(String expectedEmail) throws Exception {
        CucumberSpringConfiguration.latestResponse.andExpect(jsonPath("$.email").value(expectedEmail));
    }

    @Given("existe un cliente con email {string}")
    public void existeUnClienteConEmail(String email) throws Exception {
        CreateCustomerCommand command = new CreateCustomerCommand("Existing Customer", email);
        mockMvc.perform(post("/api/customers")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + CucumberSpringConfiguration.token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated());
    }
}
