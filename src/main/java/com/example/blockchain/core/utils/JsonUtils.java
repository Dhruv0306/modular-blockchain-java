package com.example.blockchain.core.utils;

import com.example.blockchain.core.chain.Blockchain;
import com.example.blockchain.core.model.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;

public class JsonUtils {
    static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public static <T> void writeToFile(T object, File file) throws Exception {
        mapper.writeValue(file, object);
    }

    public static <T> T readFromFile(File file, Class<T> clazz) throws Exception {
        return mapper.readValue(file, clazz);
    }

    public static <T> T readFromFile(File file, JavaType type) throws Exception {
        return mapper.readValue(file, type);
    }

    public static String toJson(Object obj) throws Exception {
        return mapper.writeValueAsString(obj);
    }

    public static <T> T fromJson(String json, Class<T> clazz) throws Exception {
        return mapper.readValue(json, clazz);
    }

    public static <T> JavaType getBlockchainType(Class<T> transactionClass) {
        return mapper.getTypeFactory().constructParametricType(Blockchain.class, transactionClass);
    }
}