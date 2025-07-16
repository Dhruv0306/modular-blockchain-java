package com.example.blockchain.examples;

import com.example.blockchain.core.config.ChainConfig;
import com.example.blockchain.examples.DemoRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class DemoRunnerTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private ChainConfig config;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        config = ChainConfig.getInstance("blockchain.properties");
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    void testRunDefaultBlockchainExample() {
        DemoRunner demoRunner = new DemoRunner();
        demoRunner.runDefaultBlockchainExample(config);
        // Just verify it runs without exception
    }

    @Test
    void testRunCustomGenesisBlockchainExample() {
        DemoRunner demoRunner = new DemoRunner();
        demoRunner.runCustomGenesisBlockchainExample(config);
        // Just verify it runs without exception
    }
}