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

public class ConfigErrorsTest {

    @TempDir
    Path tempDir;
    private File configFile;

    @BeforeEach
    void setUp() throws Exception {
        // Reset the singleton instance before each test
        resetSingleton();
        configFile = tempDir.resolve("test-config.properties").toFile();
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

    @Test
    void testMissingPropertiesFile() {
        // Try to load config from a non-existent file
        String nonExistentPath = "non-existent-file-" + System.currentTimeMillis() + ".properties";
        BlockchainConfig config = BlockchainConfig.getInstance(nonExistentPath);
        
        // Should fall back to default values
        assertEquals(4, config.getDifficulty());
        assertEquals("GENESIS_HASH", config.getGenesisHash());
        assertEquals("INFO", config.getLogLevel());
    }
    
    @Test
    void testInvalidPropertiesFormat() throws IOException {
        // Create a file with invalid properties format
        try (FileOutputStream out = new FileOutputStream(configFile)) {
            out.write("This is not a valid properties file format".getBytes());
        }
        
        // Try to load config from the invalid file
        BlockchainConfig config = BlockchainConfig.getInstance(configFile.getAbsolutePath());
        
        // Should fall back to default values
        assertEquals(4, config.getDifficulty());
        assertEquals("GENESIS_HASH", config.getGenesisHash());
        assertEquals("INFO", config.getLogLevel());
    }
    
    @Test
    void testInvalidDifficultyValue() throws IOException {
        // Create a properties file with invalid difficulty value
        Properties props = new Properties();
        props.setProperty("difficulty", "not_a_number");
        props.setProperty("genesis_hash", "TEST_HASH");
        props.setProperty("log_level", "DEBUG");
        
        try (FileOutputStream out = new FileOutputStream(configFile)) {
            props.store(out, "Test config with invalid difficulty");
        }
        
        try {
            // Try to load config from the file - this will throw NumberFormatException
            BlockchainConfig config = BlockchainConfig.getInstance(configFile.getAbsolutePath());
            fail("Should have thrown NumberFormatException");
        } catch (NumberFormatException e) {
            // Expected exception - the current implementation doesn't handle invalid number formats
            assertTrue(e.getMessage().contains("not_a_number"), "Exception should mention the invalid input");
            
            // Note: We could suggest improving BlockchainConfig to handle invalid number formats
            // by using a try-catch block and falling back to default values
        }
    }
    
    @Test
    void testEmptyPropertiesFile() throws IOException {
        // Create an empty properties file
        Properties props = new Properties();
        try (FileOutputStream out = new FileOutputStream(configFile)) {
            props.store(out, "Empty properties file");
        }
        
        // Try to load config from the empty file
        BlockchainConfig config = BlockchainConfig.getInstance(configFile.getAbsolutePath());
        
        // Should use all default values
        assertEquals(4, config.getDifficulty());
        assertEquals("GENESIS_HASH", config.getGenesisHash());
        assertEquals("INFO", config.getLogLevel());
    }
    
    @Test
    void testPartiallyDefinedProperties() throws IOException {
        // Create a properties file with only some properties defined
        Properties props = new Properties();
        props.setProperty("difficulty", "8"); // Only define difficulty
        
        try (FileOutputStream out = new FileOutputStream(configFile)) {
            props.store(out, "Partially defined properties");
        }
        
        // Try to load config from the file
        BlockchainConfig config = BlockchainConfig.getInstance(configFile.getAbsolutePath());
        
        // Should use defined value for difficulty and defaults for others
        assertEquals(8, config.getDifficulty()); // From file
        assertEquals("GENESIS_HASH", config.getGenesisHash()); // Default
        assertEquals("INFO", config.getLogLevel()); // Default
    }
}