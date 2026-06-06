package com.customers.kata_bbog.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupLogger implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(StartupLogger.class);

    @Value("${app.environment-message}")
    private String envMessage;

    @Value("${server.port:8080}")
    private String port;

    @Override
    public void run(ApplicationArguments args) {
        log.info("============================================");
        log.info("  {} ", envMessage);
        log.info("  Puerto: {}", port);
        log.info("============================================");
    }
}
