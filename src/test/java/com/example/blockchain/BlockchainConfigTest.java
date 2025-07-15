package com.example.blockchain;

import com.example.blockchain.blockchain.BlockchainConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class BlockchainConfigTest {

    @TempDir
    Path tempDir;
    private File configFile;
    private static final String TEST_CONFIG_FILENAME = "test-blockchain.properties";

    @BeforeEach
    void setUp() throws Exception {
        // Reset the singleton instance before each test
        resetSingleton();
        
        // Create a temporary config file for testing
        configFile = tempDir.resolve(TEST_CONFIG_FILENAME).toFile();
    }

    @AfterEach
    void tearDown() throws Exception {
        // Reset the singleton instance after each test
        resetSingleton();
    }

    /**
     * Helper method to reset the singleton instance using reflection
     */
    private void resetSingleton() throws Exception {
        Field instance = BlockchainConfig.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    /**
     * Helper method to create a properties file with custom settings
     */
    private void createPropertiesFile(int difficulty, String genesisHash, String logLevel) throws IOException {
        Properties props = new Properties();
        props.setProperty("difficulty", String.valueOf(difficulty));
        props.setProperty("genesis_hash", genesisHash);
        props.setProperty("log_level", logLevel);
        
        try (FileOutputStream outputStream = new FileOutputStream(configFile)) {
            props.store(outputStream, "Test blockchain configuration");
        }
    }

    @Test
    void testDefaultValues() {
        BlockchainConfig config = BlockchainConfig.getInstance();
        assertEquals(4, config.getDifficulty(), "Default difficulty should be 4");
        assertEquals("GENESIS_HASH", config.getGenesisHash(), "Default genesis hash should be GENESIS_HASH");
        assertEquals("INFO", config.getLogLevel(), "Default log level should be INFO");
    }

    @Test
    void testLoadFromFile() throws IOException {
        // Create a custom properties file
        createPropertiesFile(6, "CUSTOM_GENESIS", "DEBUG");
        
        // Get config instance with our custom file
        BlockchainConfig config = BlockchainConfig.getInstance(configFile.getAbsolutePath());
        
        // Verify values were loaded from the file
        assertEquals(6, config.getDifficulty(), "Difficulty should be loaded from file");
        assertEquals("CUSTOM_GENESIS", config.getGenesisHash(), "Genesis hash should be loaded from file");
        assertEquals("DEBUG", config.getLogLevel(), "Log level should be loaded from file");
    }

    @Test
    void testReloadConfig() throws IOException {
        // Create initial properties file
        createPropertiesFile(5, "INITIAL_HASH", "DEBUG");
        
        // Get config instance
        BlockchainConfig config = BlockchainConfig.getInstance(configFile.getAbsolutePath());
        assertEquals(5, config.getDifficulty(), "Initial difficulty should be 5");
        
        // Change the properties file
        createPropertiesFile(7, "UPDATED_HASH", "TRACE");
        
        // Reload the config
        config.reloadConfig();
        
        // Verify values were updated
        assertEquals(7, config.getDifficulty(), "Difficulty should be updated after reload");
        assertEquals("UPDATED_HASH", config.getGenesisHash(), "Genesis hash should be updated after reload");
        assertEquals("TRACE", config.getLogLevel(), "Log level should be updated after reload");
    }

    @Test
    void testChangeConfigFile() throws IOException {
        // Create initial properties file
        createPropertiesFile(5, "INITIAL_HASH", "DEBUG");
        
        // Get config instance
        BlockchainConfig config = BlockchainConfig.getInstance(configFile.getAbsolutePath());
        assertEquals(5, config.getDifficulty(), "Initial difficulty should be 5");
        
        // Create a second config file
        File secondConfigFile = tempDir.resolve("second-config.properties").toFile();
        Properties props = new Properties();
        props.setProperty("difficulty", "8");
        props.setProperty("genesis_hash", "SECOND_CONFIG");
        props.setProperty("log_level", "WARN");
        
        try (FileOutputStream outputStream = new FileOutputStream(secondConfigFile)) {
            props.store(outputStream, "Second test configuration");
        }
        
        // Change the config file
        config.setConfigFile(secondConfigFile.getAbsolutePath());
        
        // Verify values were updated from the new file
        assertEquals(8, config.getDifficulty(), "Difficulty should be updated after changing config file");
        assertEquals("SECOND_CONFIG", config.getGenesisHash(), "Genesis hash should be updated after changing config file");
        assertEquals("WARN", config.getLogLevel(), "Log level should be updated after changing config file");
    }

    @Test
    void testSingletonPattern() throws IOException {
        // Create a custom properties file
        createPropertiesFile(6, "CUSTOM_GENESIS", "DEBUG");
        
        // Get two instances
        BlockchainConfig instance1 = BlockchainConfig.getInstance(configFile.getAbsolutePath());
        BlockchainConfig instance2 = BlockchainConfig.getInstance();
        
        // Verify they are the same instance
        assertSame(instance1, instance2, "getInstance should return the same instance (singleton pattern)");
    }
} 