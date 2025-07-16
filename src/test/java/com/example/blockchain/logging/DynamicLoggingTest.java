package com.example.blockchain.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.example.blockchain.logging.LoggingUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for verifying dynamic log level changes in the blockchain application.
 * Tests both package-level and class-specific logging configurations.
 */
public class DynamicLoggingTest {

    /**
     * Tests the ability to dynamically change log levels for the entire blockchain package.
     * Verifies that log levels can be changed at runtime between INFO, DEBUG, and TRACE levels.
     */
    @Test
    void testDynamicLogLevelChange() {
        // Get the blockchain package logger
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger blockchainLogger = context.getLogger("com.example.blockchain");
        
        // Set initial log level to INFO and verify
        LoggingUtils.setBlockchainLogLevel("INFO");
        assertEquals(Level.INFO, blockchainLogger.getLevel());
        
        // Test changing to DEBUG level at runtime
        LoggingUtils.setBlockchainLogLevel("DEBUG");
        assertEquals(Level.DEBUG, blockchainLogger.getLevel());
        
        // Test changing to TRACE level at runtime
        LoggingUtils.setBlockchainLogLevel("TRACE");
        assertEquals(Level.TRACE, blockchainLogger.getLevel());
        
        // Test reverting back to INFO level
        LoggingUtils.setBlockchainLogLevel("INFO");
        assertEquals(Level.INFO, blockchainLogger.getLevel());
    }
    
    /**
     * Tests the ability to set log levels for specific classes independently.
     * Verifies that changing class-specific log levels doesn't affect package-level logging.
     */
    @Test
    void testDynamicLogLevelChangeForSpecificClass() {
        // Initialize logger for a specific transaction class
        String specificClass = "com.example.blockchain.transactions.SignedFinancialTransaction";
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger classLogger = context.getLogger(specificClass);
        
        // Test setting initial INFO level for specific class
        LoggingUtils.setLogLevel(specificClass, "INFO");
        assertEquals(Level.INFO, classLogger.getLevel());
        
        // Test changing to DEBUG level for specific class
        LoggingUtils.setLogLevel(specificClass, "DEBUG");
        assertEquals(Level.DEBUG, classLogger.getLevel());
        
        // Verify class-specific and package-level logging independence
        Logger blockchainLogger = context.getLogger("com.example.blockchain");
        LoggingUtils.setBlockchainLogLevel("WARN");
        
        // Confirm that class logger maintains its level while package logger changes
        assertEquals(Level.DEBUG, classLogger.getLevel());
        assertEquals(Level.WARN, blockchainLogger.getLevel());
    }
}
