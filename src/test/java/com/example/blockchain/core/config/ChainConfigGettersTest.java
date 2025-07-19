package com.example.blockchain.core.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the getter methods in ChainConfig class.
 */
public class ChainConfigGettersTest {
    
    private File configFile;
    private ChainConfig chainConfig;
    
    @BeforeEach
    public void setUp() throws IOException, NoSuchFieldException, IllegalAccessException {
        // Create a config directory if it doesn't exist
        File configDir = new File("config");
        if (!configDir.exists()) {
            configDir.mkdir();
        }
        
        // Create a test config file in the config directory
        configFile = new File(configDir, "test-blockchain.properties");
        
        // Write test properties to the file
        try (FileWriter writer = new FileWriter(configFile)) {
            writer.write("blockchain.persistence.wallet_file=test-wallet.json\n");
            writer.write("blockchain.max_transactions_per_block=15\n");
        }
        
        // Reset the singleton instance to ensure we get a fresh config
        Field instanceField = ChainConfig.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
        
        // Create ChainConfig instance with the test config file
        chainConfig = ChainConfig.getInstance(configFile.getName());
    }
    
    @Test
    public void testGetPersistenceWalletFile() {
        // Test that getPersistenceWalletFile returns the correct value from config
        assertEquals("test-wallet.json", chainConfig.getPersistenceWalletFile());
    }
    
    @Test
    public void testGetMaxTransactionsPerBlock() {
        // Test that getMaxTransactionsPerBlock returns the correct value from config
        assertEquals(15, chainConfig.getMaxTransactionsPerBlock());
    }
    
    @Test
    public void testDefaultValues() throws IOException, NoSuchFieldException, IllegalAccessException {
        // Reset the singleton instance
        Field instanceField = ChainConfig.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
        
        // Create a config file with no relevant properties
        File emptyConfigFile = new File("config", "empty-config.properties");
        try (FileWriter writer = new FileWriter(emptyConfigFile)) {
            writer.write("# Empty config\n");
        }
        
        // Create ChainConfig with the empty config
        ChainConfig emptyConfig = ChainConfig.getInstance(emptyConfigFile.getName());
        
        // Test default values are used when not specified in config
        assertEquals("wallet-data.json", emptyConfig.getPersistenceWalletFile());
        assertEquals(10, emptyConfig.getMaxTransactionsPerBlock());
    }
    
    @AfterEach
    public void tearDown() {
        // Clean up test files
        if (configFile != null && configFile.exists()) {
            configFile.delete();
        }
        
        File emptyConfigFile = new File("config", "empty-config.properties");
        if (emptyConfigFile.exists()) {
            emptyConfigFile.delete();
        }
    }
}