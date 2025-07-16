package com.example.blockchain.core.utils;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

import com.example.blockchain.core.chain.Blockchain;
import com.example.blockchain.core.config.ChainConfig;
import com.example.blockchain.core.model.Transaction;
import com.example.blockchain.core.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages persistence operations for the blockchain, including saving and
 * loading from JSON files. This class provides methods to serialize/deserialize
 * blockchain data to/from JSON format for persistent storage.
 */
public class PersistenceManager {
    // Logger instance for this class to track persistence operations
    private static final Logger logger = LoggerFactory.getLogger(PersistenceManager.class);

    /**
     * Attempts to load a blockchain from a JSON file if it exists.
     * The blockchain data is deserialized from JSON format into a Blockchain
     * object.
     *
     * @param clazz     The Transaction class type used in the blockchain
     * @param directory The directory containing the blockchain file
     * @param filename  The name of the blockchain file to load from
     * @return Optional containing the loaded blockchain if successful, empty
     *         Optional if loading fails
     * @param <T> Type parameter extending Transaction to support different
     *            transaction types
     */
    public static <T extends Transaction> Optional<Blockchain<T>> loadIfConfigured(Class<T> clazz, String directory,
            String filename) {
        // Construct full file path by joining directory and filename
        String path = Path.of(directory, filename).toString();
        try {
            // Attempt to import blockchain from JSON file using deserialization
            Blockchain<T> chain = Blockchain.importFromJson(new File(path), clazz);
            logger.info("Blockchain loaded from JSON file: {}", path);
            return Optional.of(chain);
        } catch (Exception e) {
            // Log warning if loading fails and return empty Optional
            logger.warn("Failed to load blockchain from '{}': {}", path, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Saves the current state of the blockchain to a JSON file.
     * Creates the necessary directories and file if they don't exist.
     * The blockchain is serialized to JSON format before saving.
     *
     * @param blockchain The blockchain instance to save to disk
     * @param directory  The directory to save the blockchain file in
     * @param filename   The name of the file to save the blockchain to
     * @param <T>        Type parameter extending Transaction to support different
     *                   transaction types
     */
    public static <T extends Transaction> void saveIfEnabled(Blockchain<T> blockchain, String directory,
            String filename) {
        // Construct full file path
        String path = Path.of(directory, filename).toString();
        try {
            // Ensure the target file exists
            File file = new File(path);
            if (!file.exists()) {
                // Create parent directories if they don't exist
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                // Create the file itself
                file.createNewFile();
            }
            // Export blockchain to JSON file through serialization
            blockchain.exportToJson(new File(path));
            logger.info("Blockchain state saved to: {}", path);
        } catch (Exception e) {
            // Log warning if saving fails
            logger.warn("Failed to save blockchain to '{}': {}", path, e.getMessage());
        }
    }

    /**
     * Convenience method to load blockchain using default configuration.
     * Uses directory and filename settings from ChainConfig.
     *
     * @param clazz The Transaction class type
     * @return Optional containing loaded blockchain or empty if loading fails
     * @param <T> Type parameter extending Transaction
     */
    public static <T extends Transaction> Optional<Blockchain<T>> loadBlockchain(Class<T> clazz) {
        // Get persistence settings from configuration
        String dir = ChainConfig.getInstance().getPersistenceDirectory();
        String file = ChainConfig.getInstance().getPersistenceFile();
        return loadIfConfigured(clazz, dir, file);
    }

    /**
     * Convenience method to save blockchain using default configuration.
     * Uses directory and filename settings from ChainConfig.
     *
     * @param blockchain The blockchain to save
     * @param <T>        Type parameter extending Transaction
     */
    public static <T extends Transaction> void saveBlockchain(Blockchain<T> blockchain) {
        // Get persistence settings from configuration
        String dir = ChainConfig.getInstance().getPersistenceDirectory();
        String file = ChainConfig.getInstance().getPersistenceFile();
        saveIfEnabled(blockchain, dir, file);
    }
}
