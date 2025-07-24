package com.example.blockchain.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;

/**
 * Utilities for working with logging at runtime.
 * This class provides methods to configure and manage logging levels
 * for different parts of the blockchain application.
 */
public class LoggingUtils {

    /**
     * Sets the log level for a specific package or class.
     * This method allows dynamic configuration of logging levels at runtime.
     *
     * @param loggerName  The logger name (typically a package name or class name)
     * @param levelString The log level (TRACE, DEBUG, INFO, WARN, ERROR)
     * @return true if successful, false otherwise
     * @throws Exception if there is an error accessing the logger factory or setting the level
     */
    public static boolean setLogLevel(String loggerName, String levelString) {
        try {
            // Get the logger context from the factory
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            // Get or create logger for the specified name
            Logger logger = loggerContext.getLogger(loggerName);

            // Convert string level to Level enum, defaulting to INFO if invalid
            Level level = Level.toLevel(levelString, Level.INFO);
            logger.setLevel(level);

            return true;
        } catch (Exception e) {
            // Log the error and return false to indicate failure
            org.slf4j.Logger rootLogger = BlockchainLoggerFactory.getLogger(LoggingUtils.class);
            rootLogger.error("Failed to set log level: " + e.getMessage(), e.getMessage());
            String error = "Failed to set log level: " + e.getMessage();
            throw new RuntimeException(error, e);
        }
    }

    /**
     * Sets the log level for the root logger.
     * The root logger is the parent of all other loggers in the hierarchy.
     *
     * @param levelString The log level (TRACE, DEBUG, INFO, WARN, ERROR)
     * @return true if successful, false otherwise
     */
    public static boolean setRootLogLevel(String levelString) {
        return setLogLevel(org.slf4j.Logger.ROOT_LOGGER_NAME, levelString);
    }

    /**
     * Sets the log level for the blockchain package.
     * This affects all loggers under the com.example.blockchain package.
     *
     * @param levelString The log level (TRACE, DEBUG, INFO, WARN, ERROR)
     * @return true if successful, false otherwise
     */
    public static boolean setBlockchainLogLevel(String levelString) {
        return setLogLevel("com.example.blockchain", levelString);
    }

    /**
     * Configures logging based on blockchain configuration.
     * This should be called at application startup to ensure
     * log levels are set according to configuration.
     * 
     * The method:
     * 1. Retrieves the chain configuration
     * 2. Gets the configured log level
     * 3. Applies the log level to the blockchain package
     * 4. Logs confirmation of the change
     * 
     * If configuration fails, the error is logged but application startup continues.
     */
    public static void configureLoggingFromConfig() {
        try {
            // Get the singleton chain configuration instance
            com.example.blockchain.core.config.ChainConfig config = com.example.blockchain.core.config.ChainConfig
                    .getInstance();

            // Get and apply the configured log level
            String logLevel = config.getLogLevel();
            setBlockchainLogLevel(logLevel);

            // Log confirmation of the change
            org.slf4j.Logger logger = BlockchainLoggerFactory.getLogger(LoggingUtils.class);
            logger.info("Blockchain log level set to: {}", logLevel);
        } catch (Exception e) {
            // If this fails, we don't want to prevent application startup
            org.slf4j.Logger logger = BlockchainLoggerFactory.getLogger(LoggingUtils.class);
            logger.warn("Failed to configure logging from blockchain config: {}", e.getMessage());
        }
    }
}
