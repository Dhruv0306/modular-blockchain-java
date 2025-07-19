/**
 * Test class for DemoRunner which verifies the execution of blockchain examples.
 * This class tests both default and custom genesis blockchain initialization.
 */
package com.example.blockchain.examples;

import com.example.blockchain.consensus.Consensus;
import com.example.blockchain.core.chain.Blockchain;
import com.example.blockchain.core.config.ChainConfig;
import com.example.blockchain.core.model.Block;
import com.example.blockchain.transactions.FinancialTransaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
    
    /**
     * Test that the output contains expected log messages.
     * This verifies that the blockchain operations are being logged correctly.
     */
    @Test
    void testOutputContainsExpectedMessages() {
        DemoRunner demoRunner = new DemoRunner();
        demoRunner.runDefaultBlockchainExample(config);
        
        String output = outContent.toString();
        assertTrue(output.contains("Genesis block:"), "Output should mention genesis block");
        assertTrue(output.contains("Mining block"), "Output should mention mining");
        assertTrue(output.contains("Block mined"), "Output should confirm block was mined");
        assertTrue(output.contains("Final blockchain state:"), "Output should show final state");
    }
}
