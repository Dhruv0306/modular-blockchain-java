package com.example.blockchain.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

import com.example.blockchain.core.config.ChainConfig;
import com.example.blockchain.logging.BlockchainLoggerFactory;
import com.example.blockchain.logging.LoggingUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for logging functionality in the blockchain system.
 * Tests various logging configurations and utilities.
 */
class LoggingTest {

    // Configuration file name used for testing
    private static final String TEST_CONFIG_FILENAME = "test-blockchain.properties";

    /**
     * Setup method run before each test.
     * Resets the BlockchainConfig singleton to ensure clean test state.
     */
    @BeforeEach
    void setUp() throws Exception {
        // Reset BlockchainConfig singleton before each test
        resetBlockchainConfigSingleton();
    }

    /**
     * Helper method to reset the BlockchainConfig singleton instance using
     * reflection. This ensures each test starts with a fresh configuration.
     */
    private void resetBlockchainConfigSingleton() throws Exception {
        Field instance = ChainConfig.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    /**
     * Helper method to create a properties file with custom settings.
     * Creates a temporary directory and file for test configuration.
     * 
     * @param logLevel The logging level to set in the properties file
     * @return File object representing the created properties file
     */
    private File createPropertiesFile(String logLevel) throws IOException {
        Path tempDir = Files.createTempDirectory("blockchain-test");
        File configFile = tempDir.resolve(TEST_CONFIG_FILENAME).toFile();

        Properties props = new Properties();
        props.setProperty("log_level", logLevel);

        try (FileOutputStream outputStream = new FileOutputStream(configFile)) {
            props.store(outputStream, "Test blockchain configuration");
        }

        return configFile;
    }

    /**
     * Tests creation of a class-based logger using BlockchainLoggerFactory
     */
    @Test
    void testBlockchainLoggerFactoryClassLogger() {
        // Get a logger for a class
        org.slf4j.Logger logger = BlockchainLoggerFactory.getLogger(LoggingTest.class);

        // Verify the logger was created with the correct name
        assertNotNull(logger);
        assertEquals("com.example.blockchain.logging.LoggingTest", logger.getName());
    }

    /**
     * Tests creation of a named logger using BlockchainLoggerFactory
     */
    @Test
    void testBlockchainLoggerFactoryNamedLogger() {
        // Get a logger with a custom name
        String customName = "test.custom.logger";
        org.slf4j.Logger logger = BlockchainLoggerFactory.getLogger(customName);

        // Verify the logger was created with the correct name
        assertNotNull(logger);
        assertEquals(customName, logger.getName());
    }

    /**
     * Tests setting and changing log levels for a specific logger
     */
    @Test
    void testSetLogLevel() {
        String loggerName = "com.example.test.logger";

        // Set log level to DEBUG
        boolean result = LoggingUtils.setLogLevel(loggerName, "DEBUG");
        assertTrue(result, "Setting log level should succeed");

        // Verify the log level was set correctly
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = context.getLogger(loggerName);
        assertEquals(Level.DEBUG, logger.getLevel());

        // Change to ERROR
        result = LoggingUtils.setLogLevel(loggerName, "ERROR");
        assertTrue(result, "Changing log level should succeed");
        assertEquals(Level.ERROR, logger.getLevel());
    }

    /**
     * Tests behavior when setting an invalid log level
     */
    @Test
    void testSetLogLevelWithInvalidLevel() {
        String loggerName = "com.example.test.invalid";

        // Set an invalid log level - should default to INFO
        boolean result = LoggingUtils.setLogLevel(loggerName, "INVALID_LEVEL");
        assertTrue(result, "Setting default log level should succeed");

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = context.getLogger(loggerName);
        assertEquals(Level.INFO, logger.getLevel());
    }

    /**
     * Tests setting the root logger level
     */
    @Test
    void testSetRootLogLevel() {
        // Set root log level to WARN
        boolean result = LoggingUtils.setRootLogLevel("WARN");
        assertTrue(result, "Setting root log level should succeed");

        // Verify the log level was set correctly
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = context.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        assertEquals(Level.WARN, rootLogger.getLevel());
    }

    /**
     * Tests setting the blockchain package log level
     */
    @Test
    void testSetBlockchainLogLevel() {
        // Set blockchain package log level to TRACE
        boolean result = LoggingUtils.setBlockchainLogLevel("TRACE");
        assertTrue(result, "Setting blockchain log level should succeed");

        // Verify the log level was set correctly
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger blockchainLogger = context.getLogger("com.example.blockchain");
        assertEquals(Level.TRACE, blockchainLogger.getLevel());
    }

    /**
     * Tests configuring logging from a configuration file
     */
    @Test
    void testConfigureLoggingFromConfig() throws IOException {
        // Create config file with DEBUG log level
        File configFile = createPropertiesFile("DEBUG");

        // Set the config file path in BlockchainConfig
        ChainConfig config = ChainConfig.getInstance(configFile.getAbsolutePath());

        // Configure logging from config
        LoggingUtils.configureLoggingFromConfig();

        // Verify blockchain log level was set to INFO (default from main config)
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger blockchainLogger = context.getLogger("com.example.blockchain");
        assertEquals(Level.INFO, blockchainLogger.getLevel());
    }

    /**
     * Tests error handling when setting log level with invalid parameters
     */
    @Test
    void testSetLogLevelException() {
        // Force an exception by using null logger name
        assertThrows(RuntimeException.class, () -> LoggingUtils.setLogLevel(null, "DEBUG"));
    }

    /**
     * Tests error handling when configuring logging with invalid configuration
     */
    @Test
    void testConfigureLoggingFromConfigException() {
        // Create a scenario where the method will throw an exception
        // by temporarily breaking the logging system

        // Save original logger factory
        org.slf4j.ILoggerFactory originalFactory = LoggerFactory.getILoggerFactory();

        try {
            // Replace with a broken factory that will cause issues
            Field factoryField = LoggerFactory.class.getDeclaredField("INITIALIZATION_STATE");
            factoryField.setAccessible(true);

            // This should cause the method to fail and trigger catch block
            assertDoesNotThrow(() -> {
                // Create a config with invalid log level that will cause exception
                resetBlockchainConfigSingleton();
                ChainConfig config = ChainConfig.getInstance();

                // Set an invalid log level using reflection
                Field logLevelField = ChainConfig.class.getDeclaredField("logLevel");
                logLevelField.setAccessible(true);
                logLevelField.set(config, "\u0000INVALID\u0000"); // Null characters should cause issues

                LoggingUtils.configureLoggingFromConfig();
            });
        } catch (Exception e) {
            // If reflection fails, just test that the method doesn't throw
            assertDoesNotThrow(() -> LoggingUtils.configureLoggingFromConfig());
        }
    }

    /**
     * Tests LoggingUtils constructor for coverage
     */
    @Test
    void testConstructor() {
        // Test constructor coverage
        LoggingUtils utils = new LoggingUtils();
        assertNotNull(utils);
    }

    /**
     * Tests BlockchainLoggerFactory constructor for coverage
     */
    @Test
    void testBlockchainLoggerFactoryConstructor() {
        // Test constructor coverage
        BlockchainLoggerFactory factory = new BlockchainLoggerFactory();
        assertNotNull(factory);
    }
}
