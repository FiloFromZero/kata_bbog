package com.customers.kata_bbog.infrastructure.config;

import com.customers.kata_bbog.application.service.CreateCustomerService;
import com.customers.kata_bbog.application.service.ListCustomersService;
import com.customers.kata_bbog.domain.port.in.CreateCustomerUseCase;
import com.customers.kata_bbog.domain.port.in.ListCustomersUseCase;
import com.customers.kata_bbog.domain.port.out.CustomerRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de beans de dominio.
 * Inyección manual: los servicios de aplicación no llevan @Service
 * para mantener la capa de dominio/aplicación libre de Spring.
 */
@Configuration
public class BeanConfiguration {

    @Bean
    public CreateCustomerUseCase createCustomerUseCase(CustomerRepository customerRepository) {
        return new CreateCustomerService(customerRepository);
    }

    @Bean
    public ListCustomersUseCase listCustomersUseCase(CustomerRepository customerRepository) {
        return new ListCustomersService(customerRepository);
    }
}
