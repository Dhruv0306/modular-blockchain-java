package com.example.blockchain.core.utils;

import com.example.blockchain.core.chain.Blockchain;
import com.example.blockchain.core.model.Transaction;
import com.example.blockchain.logging.BlockchainLoggerFactory;
import com.example.blockchain.logging.LoggingUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;

/**
 * Utility class for JSON serialization and deserialization operations
 * Uses Jackson ObjectMapper for JSON processing
 */
public class JsonUtils {
    // ObjectMapper instance configured for pretty printing and date/time handling
    static final ObjectMapper mapper = new ObjectMapper();
    static final org.slf4j.Logger logger = BlockchainLoggerFactory.getLogger(JsonUtils.class);

    static {
        // Register JavaTimeModule for proper datetime serialization
        mapper.registerModule(new JavaTimeModule());
        // Enable pretty printing of JSON output
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        LoggingUtils.configureLoggingFromConfig();
    }

    /**
     * Writes an object to a JSON file
     * @param object The object to serialize
     * @param file The file to write to
     * @throws IOException if writing fails
     */
    public static <T> void writeToFile(T object, File file) throws IOException{
        try {
            mapper.writeValue(file, object);
        } catch (IOException e) {
            logger.error("Failed to write object to JSON file: " + file.getAbsolutePath(), e.getMessage());
            String error = "Failed to write object to JSON file: " + file.getAbsolutePath();
            throw new IOException(error, e);
        }
    }

    /**
     * Reads JSON from a file and converts it to specified class type
     * @param file The file to read from
     * @param clazz The class type to convert to
     * @return The deserialized object
     * @throws Exception if reading fails
     */
    public static <T> T readFromFile(File file, Class<T> clazz) throws Exception {
        return mapper.readValue(file, clazz);
    }

    /**
     * Reads JSON from a file and converts it to specified JavaType
     * @param file The file to read from
     * @param type The JavaType to convert to
     * @return The deserialized object
     * @throws Exception if reading fails
     */
    public static <T> T readFromFile(File file, JavaType type) throws Exception {
        return mapper.readValue(file, type);
    }

    /**
     * Converts an object to JSON string
     * @param obj The object to convert
     * @return JSON string representation
     * @throws Exception if serialization fails
     */
    public static String toJson(Object obj) throws Exception {
        return mapper.writeValueAsString(obj);
    }

    /**
     * Converts JSON string to specified class type
     * @param json The JSON string to convert
     * @param clazz The class type to convert to
     * @return The deserialized object
     * @throws Exception if deserialization fails
     */
    public static <T> T fromJson(String json, Class<T> clazz) throws Exception {
        return mapper.readValue(json, clazz);
    }

    /**
     * Creates a JavaType for Blockchain parameterized with given transaction class
     * @param transactionClass The transaction class type
     * @return Parameterized JavaType for Blockchain
     */
    public static <T> JavaType getBlockchainType(Class<T> transactionClass) {
        return mapper.getTypeFactory().constructParametricType(Blockchain.class, transactionClass);
    }
}
