package com.example.blockchain.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;

/**
 * Utilities for working with logging at runtime.
 */
public class LoggingUtils {

    /**
     * Sets the log level for a specific package or class.
     *
     * @param loggerName  The logger name (typically a package name or class name)
     * @param levelString The log level (TRACE, DEBUG, INFO, WARN, ERROR)
     * @return true if successful, false otherwise
     */
    public static boolean setLogLevel(String loggerName, String levelString) {
        try {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            Logger logger = loggerContext.getLogger(loggerName);

            Level level = Level.toLevel(levelString, Level.INFO);
            logger.setLevel(level);

            return true;
        } catch (Exception e) {
            org.slf4j.Logger rootLogger = BlockchainLoggerFactory.getLogger(LoggingUtils.class);
            rootLogger.error("Failed to set log level: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Sets the log level for the root logger.
     *
     * @param levelString The log level (TRACE, DEBUG, INFO, WARN, ERROR)
     * @return true if successful, false otherwise
     */
    public static boolean setRootLogLevel(String levelString) {
        return setLogLevel(org.slf4j.Logger.ROOT_LOGGER_NAME, levelString);
    }

    /**
     * Sets the log level for the blockchain package.
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
     */
    public static void configureLoggingFromConfig() {
        try {
            com.example.blockchain.core.config.ChainConfig config = com.example.blockchain.core.config.ChainConfig
                    .getInstance();

            String logLevel = config.getLogLevel();
            setBlockchainLogLevel(logLevel);

            org.slf4j.Logger logger = BlockchainLoggerFactory.getLogger(LoggingUtils.class);
            logger.info("Blockchain log level set to: {}", logLevel);
        } catch (Exception e) {
            // If this fails, we don't want to prevent application startup
            org.slf4j.Logger logger = BlockchainLoggerFactory.getLogger(LoggingUtils.class);
            logger.warn("Failed to configure logging from blockchain config: {}", e.getMessage());
        }
    }
}