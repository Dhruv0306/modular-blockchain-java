package com.example.blockchain.core.utils;

import com.example.blockchain.core.chain.Blockchain;
import com.example.blockchain.core.model.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;

/**
 * Utility class for JSON serialization and deserialization operations
 * Uses Jackson ObjectMapper for JSON processing
 */
public class JsonUtils {
    // ObjectMapper instance configured for pretty printing and date/time handling
    static final ObjectMapper mapper = new ObjectMapper();

    static {
        // Register JavaTimeModule for proper datetime serialization
        mapper.registerModule(new JavaTimeModule());
        // Enable pretty printing of JSON output
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Writes an object to a JSON file
     * @param object The object to serialize
     * @param file The file to write to
     * @throws Exception if writing fails
     */
    public static <T> void writeToFile(T object, File file) throws Exception {
        mapper.writeValue(file, object);
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
