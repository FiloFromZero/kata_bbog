package com.customers.kata_bbog.bdd.steps;

import com.customers.kata_bbog.infrastructure.adapter.out.persistence.CustomerJpaRepository;
import com.customers.kata_bbog.infrastructure.adapter.out.persistence.CustomerJpaEntity;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

public class DeleteCustomerSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerJpaRepository jpaRepository;

    @When("envío una petición DELETE a {string} para el cliente con email {string}")
    public void envioUnaPeticionDELETEAParaElClienteConEmail(String path, String email) throws Exception {
        CustomerJpaEntity entity = jpaRepository.findByEmail(email)
                .orElseThrow(() -> new AssertionError("Cliente no encontrado para el email: " + email));

        CucumberSpringConfiguration.latestResponse = mockMvc.perform(delete(path + "/" + entity.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + CucumberSpringConfiguration.token));
    }

    @When("envío una petición DELETE a {string}")
    public void envioUnaPeticionDELETEA(String path) throws Exception {
        CucumberSpringConfiguration.latestResponse = mockMvc.perform(delete(path)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + CucumberSpringConfiguration.token));
    }
}
