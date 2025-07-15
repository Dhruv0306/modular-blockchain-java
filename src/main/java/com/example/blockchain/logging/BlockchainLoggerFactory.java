package com.example.blockchain.logging;

import org.slf4j.Logger;

/**
 * Factory class for obtaining loggers.
 * This class centralizes logger creation and allows for future
 * switching of logging backends without changing client code.
 */
public class BlockchainLoggerFactory {

    /**
     * Get a logger for the specified class.
     *
     * @param clazz The class requesting the logger
     * @return Logger instance
     */
    public static Logger getLogger(Class<?> clazz) {
        return org.slf4j.LoggerFactory.getLogger(clazz);
    }

    /**
     * Get a logger with the specified name.
     *
     * @param name Logger name
     * @return Logger instance
     */
    public static Logger getLogger(String name) {
        return org.slf4j.LoggerFactory.getLogger(name);
    }
}
