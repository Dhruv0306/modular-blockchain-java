package com.example.blockchain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class MainTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    void testMainWithDefaultConfig() {
        Main.main(new String[] {});
        // Just verify it runs without exception
    }

    @Test
    void testMainWithConfigFile() {
        Main.main(new String[] { "blockchain.properties" });
        // Just verify it runs without exception
    }

    @Test
    void testMainWithEnvironmentVariable() {
        System.setProperty("BLOCKCHAIN_ENV", "test");
        try {
            Main.main(new String[] {});
        } finally {
            System.clearProperty("BLOCKCHAIN_ENV");
        }
    }
}