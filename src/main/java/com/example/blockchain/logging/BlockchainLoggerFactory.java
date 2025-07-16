package com.example.blockchain.logging;

import org.slf4j.Logger;

/**
 * Factory class for obtaining loggers.
 * This class centralizes logger creation and allows for future
 * switching of logging backends without changing client code.
 *
 * The factory provides two ways to obtain loggers:
 * 1. By class - recommended for class-level logging
 * 2. By name - useful for cross-cutting or module-level logging
 *
 * Usage example:
 * Logger log = BlockchainLoggerFactory.getLogger(MyClass.class);
 * Logger moduleLog = BlockchainLoggerFactory.getLogger("module.name");
 */
public class BlockchainLoggerFactory {

    /**
     * Get a logger for the specified class.
     * This is the recommended way to obtain loggers for class-level logging.
     * The logger name will be the fully qualified class name.
     *
     * @param clazz The class requesting the logger
     * @return Logger instance configured for the specified class
     * @throws IllegalArgumentException if clazz is null
     */
    public static Logger getLogger(Class<?> clazz) {
        // Delegate to SLF4J's LoggerFactory to create the actual logger instance
        return org.slf4j.LoggerFactory.getLogger(clazz);
    }

    /**
     * Get a logger with the specified name.
     * Useful for module-level or cross-cutting logging concerns where a class reference
     * is not appropriate or available.
     *
     * @param name Logger name, typically using dot notation (e.g. "com.example.module")
     * @return Logger instance configured with the specified name
     * @throws IllegalArgumentException if name is null or empty
     */
    public static Logger getLogger(String name) {
        // Delegate to SLF4J's LoggerFactory to create the actual logger instance
        return org.slf4j.LoggerFactory.getLogger(name);
    }
}
