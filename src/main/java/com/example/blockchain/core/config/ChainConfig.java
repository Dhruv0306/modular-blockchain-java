package com.example.blockchain.core.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import com.example.blockchain.logging.BlockchainLoggerFactory;

/**
 * Configuration class for the blockchain system.
 * Implements singleton pattern to ensure only one configuration instance exists.
 * Loads configuration from properties file and environment variables.
 */
public class ChainConfig {
    // Logger instance for this class
    private static final Logger logger = BlockchainLoggerFactory.getLogger(ChainConfig.class);

    // Default values - used if no config file is found
    private static final int DEFAULT_DIFFICULTY = 4;  // Default mining difficulty
    private static final String DEFAULT_GENESIS_HASH = "GENESIS_HASH";  // Default hash for genesis block

    // Config file path
    private static final String DEFAULT_CONFIG_FILE = "config/blockchain.properties";

    // Singleton instance
    private static ChainConfig instance;

    // Configuration properties
    private int difficulty;           // Mining difficulty level
    private String genesisHash;       // Hash of the genesis block
    private String configFile;        // Path to configuration file
    private String logLevel;          // Logging level
    private boolean persistenceEnabled;    // Flag for data persistence
    private String persistenceDirectory;   // Directory for persistent storage
    private String persistenceFile;        // Filename for persistent storage
    private String persistenceWallatFile; // Filename for wallet persistence

    /**
     * Private constructor using default config file.
     * Part of singleton pattern implementation.
     */
    private ChainConfig() {
        this(DEFAULT_CONFIG_FILE);
    }

    /**
     * Private constructor with specific config file.
     * @param configFile Path to the configuration file
     */
    private ChainConfig(String configFile) {
        this.configFile = configFile;
        loadConfig();
    }

    /**
     * Gets singleton instance using default config file.
     * @return ChainConfig singleton instance
     */
    public static synchronized ChainConfig getInstance() {
        if (instance == null) {
            instance = new ChainConfig();
        }
        return instance;
    }

    /**
     * Gets singleton instance with specific config file.
     * Creates new instance if config file differs from current.
     * @param configFile Path to the configuration file
     * @return ChainConfig singleton instance
     */
    public static synchronized ChainConfig getInstance(String configFile) {
        if (instance == null) {
            instance = new ChainConfig(configFile);
        } else if (!instance.configFile.equals(configFile)) {
            instance = new ChainConfig(configFile);
        }
        return instance;
    }

    /**
     * Loads configuration from file and environment variables.
     * Priority: Environment variables > Config file > Default values
     */
    private void loadConfig() {
        Properties properties = new Properties();
        boolean configLoaded = false;

        // Try to load from config file
        try (InputStream input = new FileInputStream(configFile.startsWith("config/") ? configFile : "config/" + configFile)) {
            properties.load(input);
            configLoaded = true;
            logger.info("Loaded configuration from: {}", configFile);
        } catch (IOException ex) {
            logger.warn("Config file not found: {}. Using default or environment values.", configFile);
        }

        // Load difficulty - check env var first, then config file, then default
        String difficultyStr = System.getenv("BLOCKCHAIN_DIFFICULTY");
        if (difficultyStr != null && !difficultyStr.isEmpty()) {
            difficulty = Integer.parseInt(difficultyStr);
            logger.info("Using environment difficulty: {}", difficulty);
        } else if (configLoaded) {
            difficulty = Integer.parseInt(properties.getProperty("difficulty", String.valueOf(DEFAULT_DIFFICULTY)));
        } else {
            difficulty = DEFAULT_DIFFICULTY;
        }

        // Load genesis hash - check env var first, then config file, then default
        String genesisHashEnv = System.getenv("BLOCKCHAIN_GENESIS_HASH");
        if (genesisHashEnv != null && !genesisHashEnv.isEmpty()) {
            genesisHash = genesisHashEnv;
            logger.info("Using environment genesis hash: {}", genesisHash);
        } else if (configLoaded) {
            genesisHash = properties.getProperty("genesis_hash", DEFAULT_GENESIS_HASH);
        } else {
            genesisHash = DEFAULT_GENESIS_HASH;
        }

        // Load log level from config file or use default
        logLevel = properties.getProperty("log_level", "INFO");

        // Load persistence settings from config file or use defaults
        persistenceEnabled = Boolean.parseBoolean(properties.getProperty("blockchain.persistence.enabled", "false"));
        persistenceDirectory = properties.getProperty("blockchain.persistence.directory", "data");
        persistenceFile = properties.getProperty("blockchain.persistence.file", "chain-data.json");
        persistenceWallatFile = properties.getProperty("blockchain.persistence.wallet_file", "wallet-data.json");
        if (persistenceEnabled) {
            logger.info("Persistence enabled: saving to {}/{}", persistenceDirectory, persistenceFile);
        } else {
            logger.info("Persistence is disabled.");
        }
    }

    /**
     * Gets the current mining difficulty level.
     * @return Current difficulty value
     */
    public int getDifficulty() {
        return difficulty;
    }

    /**
     * Gets the genesis block hash.
     * @return Genesis block hash
     */
    public String getGenesisHash() {
        return genesisHash;
    }

    /**
     * Gets the current log level.
     * @return Current log level
     */
    public String getLogLevel() {
        return logLevel;
    }

    /**
     * Reloads configuration from file and environment.
     * Useful for testing or runtime configuration changes.
     */
    public void reloadConfig() {
        loadConfig();
    }

    /**
     * Changes the configuration file path and reloads config.
     * @param configFile New configuration file path
     */
    public void setConfigFile(String configFile) {
        this.configFile = configFile;
        reloadConfig();
    }

    /**
     * Checks if data persistence is enabled.
     * @return true if persistence is enabled, false otherwise
     */
    public boolean isPersistenceEnabled() {
        return persistenceEnabled;
    }

    /**
     * Gets the directory path for persistent storage.
     * @return Persistence directory path
     */
    public String getPersistenceDirectory() {
        return persistenceDirectory;
    }

    /**
     * Gets the filename for persistent storage.
     * @return Persistence filename
     */
    public String getPersistenceFile() {
        return persistenceFile;
    }

    /**
     * Gets the filename for wallet persistence.
     * @return Wallet persistence filename
     */
    public String getPersistenceWalletFile() {
        return persistenceWallatFile;
    }
}
