package com.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the TalonOneApp Spring Boot application.
 * This application integrates with Talon.One's Integration API to manage personalized rewards and discounts
 * for an e-commerce platform, and connects to a PostgreSQL database for persistence.
 */
@SpringBootApplication
public class AppApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }
}
