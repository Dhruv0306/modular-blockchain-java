package com.example.blockchain.logging;

import com.example.blockchain.core.config.ChainConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Test class specifically focused on exception handling in LoggingUtils.
 * Tests the catch block in configureLoggingFromConfig() method.
 */
public class LoggingUtilsExceptionTest {

    /**
     * Reset the ChainConfig singleton before each test to ensure clean state.
     */
    @BeforeEach
    void setUp() throws Exception {
        resetBlockchainConfigSingleton();
    }

    /**
     * Helper method to reset the BlockchainConfig singleton instance using reflection.
     */
    private void resetBlockchainConfigSingleton() throws Exception {
        Field instance = ChainConfig.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    /**
     * Test that when an exception occurs in configureLoggingFromConfig(),
     * it is properly caught and logged as a warning without propagating.
     */
    @Test
    void testConfigureLoggingFromConfigExceptionHandling() {
        // Create a mock logger to verify warning is logged
        Logger mockLogger = mock(Logger.class);

        // Use MockedStatic to mock the static BlockchainLoggerFactory.getLogger method
        try (MockedStatic<BlockchainLoggerFactory> mockedFactory = Mockito.mockStatic(BlockchainLoggerFactory.class)) {
            // Configure the mock to return our mock logger
            mockedFactory.when(() -> BlockchainLoggerFactory.getLogger(LoggingUtils.class)).thenReturn(mockLogger);

            // Create a mock ChainConfig that throws an exception when getLogLevel is called
            ChainConfig mockConfig = mock(ChainConfig.class);
            when(mockConfig.getLogLevel()).thenThrow(new RuntimeException("Test exception"));

            // Use MockedStatic to mock the static ChainConfig.getInstance method
            try (MockedStatic<ChainConfig> mockedConfig = Mockito.mockStatic(ChainConfig.class)) {
                // Configure the mock to return our mock config
                mockedConfig.when(ChainConfig::getInstance).thenReturn(mockConfig);

                // Call the method under test - should not throw exception
                LoggingUtils.configureLoggingFromConfig();

                // Verify that warning was logged with the exception message
                verify(mockLogger).warn(contains("Failed to configure logging from blockchain config"), eq("Test exception"));
            }
        }
    }
}