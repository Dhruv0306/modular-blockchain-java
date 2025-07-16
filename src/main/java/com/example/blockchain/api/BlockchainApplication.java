package com.example.blockchain.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for the Blockchain API
 * This class serves as the entry point for the Spring Boot application
 * and enables auto-configuration through @SpringBootApplication annotation
 */
@SpringBootApplication
public class BlockchainApplication {
    /**
     * Main method that starts the Spring Boot application
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        // Initialize and run the Spring application
        SpringApplication.run(BlockchainApplication.class, args);
    }
}
