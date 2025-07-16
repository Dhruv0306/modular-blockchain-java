/**
 * Test class for DemoRunner which verifies the execution of blockchain examples.
 * This class tests both default and custom genesis blockchain initialization.
 */
package com.example.blockchain.examples;

import com.example.blockchain.core.config.ChainConfig;
import com.example.blockchain.examples.DemoRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class DemoRunnerTest {
    // Stream to capture System.out for verification
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    // Store original System.out to restore after tests
    private final PrintStream originalOut = System.out;
    // Configuration for the blockchain
    private ChainConfig config;

    /**
     * Set up test environment before each test.
     * Redirects System.out to ByteArrayOutputStream and initializes blockchain config.
     */
    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        config = ChainConfig.getInstance("blockchain.properties");
    }

    /**
     * Clean up after each test by restoring the original System.out.
     */
    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    /**
     * Test the execution of default blockchain example.
     * Verifies that the method runs without throwing any exceptions.
     */
    @Test
    void testRunDefaultBlockchainExample() {
        DemoRunner demoRunner = new DemoRunner();
        demoRunner.runDefaultBlockchainExample(config);
        // Just verify it runs without exception
    }

    /**
     * Test the execution of custom genesis blockchain example.
     * Verifies that the method runs without throwing any exceptions.
     */
    @Test
    void testRunCustomGenesisBlockchainExample() {
        DemoRunner demoRunner = new DemoRunner();
        demoRunner.runCustomGenesisBlockchainExample(config);
        // Just verify it runs without exception
    }
}
