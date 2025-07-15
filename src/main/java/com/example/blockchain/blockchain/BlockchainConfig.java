package com.example.blockchain.blockchain;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BlockchainConfig {
    // Default values - used if no config file is found
    private static final int DEFAULT_DIFFICULTY = 4;
    private static final String DEFAULT_GENESIS_HASH = "GENESIS_HASH";
    
    // Config file path
    private static final String DEFAULT_CONFIG_FILE = "blockchain.properties";
    
    // Singleton instance
    private static BlockchainConfig instance;
    
    // Configuration properties
    private int difficulty;
    private String genesisHash;
    private String configFile;
    
    // Private constructor for singleton pattern
    private BlockchainConfig() {
        this(DEFAULT_CONFIG_FILE);
    }
    
    // Private constructor with specific config file
    private BlockchainConfig(String configFile) {
        this.configFile = configFile;
        loadConfig();
    }
    
    // Get singleton instance
    public static synchronized BlockchainConfig getInstance() {
        if (instance == null) {
            instance = new BlockchainConfig();
        }
        return instance;
    }
    
    // Get or create instance with specific config file
    public static synchronized BlockchainConfig getInstance(String configFile) {
        if (instance == null) {
            instance = new BlockchainConfig(configFile);
        } else if (!instance.configFile.equals(configFile)) {
            instance = new BlockchainConfig(configFile);
        }
        return instance;
    }
    
    // Load configuration from file or environment
    private void loadConfig() {
        Properties properties = new Properties();
        boolean configLoaded = false;
        
        // Try to load from config file
        try (InputStream input = new FileInputStream(configFile)) {
            properties.load(input);
            configLoaded = true;
            System.out.println("Loaded configuration from: " + configFile);
        } catch (IOException ex) {
            System.out.println("Config file not found: " + configFile + ". Using default or environment values.");
        }
        
        // Load difficulty
        String difficultyStr = System.getenv("BLOCKCHAIN_DIFFICULTY");
        if (difficultyStr != null && !difficultyStr.isEmpty()) {
            difficulty = Integer.parseInt(difficultyStr);
            System.out.println("Using environment difficulty: " + difficulty);
        } else if (configLoaded) {
            difficulty = Integer.parseInt(properties.getProperty("difficulty", String.valueOf(DEFAULT_DIFFICULTY)));
        } else {
            difficulty = DEFAULT_DIFFICULTY;
        }
        
        // Load genesis hash
        String genesisHashEnv = System.getenv("BLOCKCHAIN_GENESIS_HASH");
        if (genesisHashEnv != null && !genesisHashEnv.isEmpty()) {
            genesisHash = genesisHashEnv;
            System.out.println("Using environment genesis hash: " + genesisHash);
        } else if (configLoaded) {
            genesisHash = properties.getProperty("genesis_hash", DEFAULT_GENESIS_HASH);
        } else {
            genesisHash = DEFAULT_GENESIS_HASH;
        }
    }
    
    // Getters for config values
    public int getDifficulty() {
        return difficulty;
    }
    
    public String getGenesisHash() {
        return genesisHash;
    }
    
    // Method to reload config (useful for testing or runtime changes)
    public void reloadConfig() {
        loadConfig();
    }
    
    // Method to switch configuration file at runtime
    public void setConfigFile(String configFile) {
        this.configFile = configFile;
        reloadConfig();
    }
} 