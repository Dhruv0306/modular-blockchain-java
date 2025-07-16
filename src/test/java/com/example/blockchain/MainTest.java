package com.example.blockchain;

import com.example.blockchain.examples.DemoRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Test class for the Main class of the blockchain application.
 * Tests various initialization scenarios and configurations.
 */
class MainTest {
    // Captures System.out for testing
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    // Stores original System.out to restore after tests
    private final PrintStream originalOut = System.out;
    // Mock object for DemoRunner
    private DemoRunner mockDemoRunner;

    /**
     * Sets up the test environment before each test.
     * Redirects System.out to capture output and creates mock objects.
     */
    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        mockDemoRunner = Mockito.mock(DemoRunner.class);
    }

    /**
     * Cleans up the test environment after each test.
     * Restores the original System.out.
     */
    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    /**
     * Tests the main method with default configuration.
     * No arguments are passed to main.
     */
    @Test
    void testMainWithDefaultConfig() {
        Main.main(new String[] {});
        // Just verify it runs without exception
    }

    /**
     * Tests the main method with a configuration file.
     * Passes blockchain.properties as argument.
     */
    @Test
    void testMainWithConfigFile() {
        Main.main(new String[] { "blockchain.properties" });
        // Just verify it runs without exception
    }

    /**
     * Tests the main method with environment variable configuration.
     * Sets BLOCKCHAIN_ENV environment variable to "test".
     */
    @Test
    void testMainWithEnvironmentVariable() {
        System.setProperty("BLOCKCHAIN_ENV", "test");
        try {
            Main.main(new String[] {});
        } finally {
            // Clean up environment variable after test
            System.clearProperty("BLOCKCHAIN_ENV");
        }
    }
}
