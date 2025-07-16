package com.example.blockchain.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.example.blockchain.logging.LoggingUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

public class DynamicLoggingTest {

    @Test
    void testDynamicLogLevelChange() {
        // Get the blockchain package logger
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger blockchainLogger = context.getLogger("com.example.blockchain");
        
        // Set initial log level to INFO
        LoggingUtils.setBlockchainLogLevel("INFO");
        assertEquals(Level.INFO, blockchainLogger.getLevel());
        
        // Change log level to DEBUG at runtime
        LoggingUtils.setBlockchainLogLevel("DEBUG");
        assertEquals(Level.DEBUG, blockchainLogger.getLevel());
        
        // Change log level to TRACE at runtime
        LoggingUtils.setBlockchainLogLevel("TRACE");
        assertEquals(Level.TRACE, blockchainLogger.getLevel());
        
        // Change log level back to INFO
        LoggingUtils.setBlockchainLogLevel("INFO");
        assertEquals(Level.INFO, blockchainLogger.getLevel());
    }
    
    @Test
    void testDynamicLogLevelChangeForSpecificClass() {
        // Get a logger for a specific class
        String specificClass = "com.example.blockchain.transactions.SignedFinancialTransaction";
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger classLogger = context.getLogger(specificClass);
        
        // Set initial log level to INFO
        LoggingUtils.setLogLevel(specificClass, "INFO");
        assertEquals(Level.INFO, classLogger.getLevel());
        
        // Change log level to DEBUG at runtime
        LoggingUtils.setLogLevel(specificClass, "DEBUG");
        assertEquals(Level.DEBUG, classLogger.getLevel());
        
        // Verify that changing the class logger doesn't affect the package logger
        Logger blockchainLogger = context.getLogger("com.example.blockchain");
        LoggingUtils.setBlockchainLogLevel("WARN");
        
        assertEquals(Level.DEBUG, classLogger.getLevel());
        assertEquals(Level.WARN, blockchainLogger.getLevel());
    }
}