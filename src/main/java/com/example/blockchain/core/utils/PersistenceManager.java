package com.example.blockchain.core.utils;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

import com.example.blockchain.core.chain.Blockchain;
import com.example.blockchain.core.config.ChainConfig;
import com.example.blockchain.core.model.Transaction;
import com.example.blockchain.core.utils.JsonUtils;
import com.example.blockchain.wallet.core.WalletList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages persistence operations for the blockchain, including saving and
 * loading from JSON files. This class provides methods to serialize/deserialize
 * blockchain data to/from JSON format for persistent storage.
 * 
 * The PersistenceManager handles:
 * - Loading blockchain data from JSON files
 * - Saving blockchain state to JSON files
 * - Creating necessary directories and files
 * - Error handling and logging for persistence operations
 */
public class PersistenceManager {
    // Logger instance for this class to track persistence operations
    // Uses SLF4J logging framework for consistent logging across the application
    private static final Logger logger = LoggerFactory.getLogger(PersistenceManager.class);

    /**
     * Attempts to load a blockchain from a JSON file if it exists.
     * The blockchain data is deserialized from JSON format into a Blockchain
     * object.
     *
     * This method will:
     * 1. Construct the full file path from directory and filename
     * 2. Attempt to deserialize the JSON file into a Blockchain object
     * 3. Return the loaded blockchain wrapped in an Optional
     * 4. Log success/failure of the operation
     *
     * @param clazz     The Transaction class type used in the blockchain
     * @param directory The directory containing the blockchain file
     * @param filename  The name of the blockchain file to load from
     * @return Optional containing the loaded blockchain if successful, empty
     *         Optional if loading fails
     * @param <T> Type parameter extending Transaction to support different
     *            transaction types
     * @throws Exception if there are issues reading or parsing the file
     */
    public static <T extends Transaction> Optional<Blockchain<T>> loadIfConfigured(Class<T> clazz, String directory,
            String filename) {
        // Construct full file path by joining directory and filename
        // Uses Path.of() for platform-independent path handling
        String path = Path.of(directory, filename).toString();
        try {
            // Attempt to import blockchain from JSON file using deserialization
            // The importFromJson method handles the actual JSON parsing
            Blockchain<T> chain = Blockchain.importFromJson(new File(path), clazz);
            logger.info("Blockchain loaded from JSON file: {}", path);
            return Optional.of(chain);
        } catch (Exception e) {
            // Log warning if loading fails and return empty Optional
            // Includes both file path and error message for debugging
            logger.warn("Failed to load blockchain from '{}': {}", path, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Saves the current state of the blockchain to a JSON file.
     * Creates the necessary directories and file if they don't exist.
     * The blockchain is serialized to JSON format before saving.
     *
     * This method performs the following steps:
     * 1. Constructs the full file path
     * 2. Creates directories if they don't exist
     * 3. Creates the file if it doesn't exist
     * 4. Serializes and saves the blockchain to JSON
     * 5. Logs the operation result
     *
     * @param blockchain The blockchain instance to save to disk
     * @param directory  The directory to save the blockchain file in
     * @param filename   The name of the file to save the blockchain to
     * @param <T>        Type parameter extending Transaction to support different
     *                   transaction types
     * @throws Exception if there are issues creating directories or writing the
     *                   file
     */
    public static <T extends Transaction> void saveIfEnabled(Blockchain<T> blockchain, String directory,
            String filename) {
        // Construct full file path using platform-independent Path API
        String path = Path.of(directory, filename).toString();
        try {
            // Ensure the target file exists by creating necessary structure
            File file = new File(path);
            if (!file.exists()) {
                // Create parent directories if they don't exist
                // This ensures the full directory path is available
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                // Create the file itself after directories are ready
                file.createNewFile();
            }
            // Export blockchain to JSON file through serialization
            // The exportToJson method handles the actual JSON conversion
            blockchain.exportToJson(new File(path));
            logger.info("Blockchain state saved to: {}", path);
        } catch (Exception e) {
            // Log warning if saving fails, including both path and error details
            logger.warn("Failed to save blockchain to '{}': {}", path, e.getMessage());
        }
    }

    /**
     * Convenience method to load blockchain using default configuration.
     * Uses directory and filename settings from ChainConfig.
     *
     * This is a wrapper method that:
     * 1. Retrieves persistence settings from ChainConfig
     * 2. Delegates to loadIfConfigured() with those settings
     *
     * @param clazz The Transaction class type
     * @return Optional containing loaded blockchain or empty if loading fails
     * @param <T> Type parameter extending Transaction
     */
    public static <T extends Transaction> Optional<Blockchain<T>> loadBlockchain(Class<T> clazz) {
        // Get persistence settings from configuration singleton
        String dir = ChainConfig.getInstance().getPersistenceDirectory();
        String file = ChainConfig.getInstance().getPersistenceFile();
        return loadIfConfigured(clazz, dir, file);
    }

    /**
     * Convenience method to save blockchain using default configuration.
     * Uses directory and filename settings from ChainConfig.
     *
     * This is a wrapper method that:
     * 1. Retrieves persistence settings from ChainConfig
     * 2. Delegates to saveIfEnabled() with those settings
     *
     * @param blockchain The blockchain to save
     * @param <T>        Type parameter extending Transaction
     */
    public static <T extends Transaction> void saveBlockchain(Blockchain<T> blockchain) {
        // Get persistence settings from configuration singleton
        String dir = ChainConfig.getInstance().getPersistenceDirectory();
        String file = ChainConfig.getInstance().getPersistenceFile();
        saveIfEnabled(blockchain, dir, file);
    }

    /**
     * Saves a wallet list to a JSON file at the specified path.
     * Uses JsonUtils to handle the serialization and file writing process.
     *
     * @param walletList The wallet list to save
     * @param path       The file path where the wallet list should be saved
     * @throws RuntimeException if there are any errors during the save operation
     */
    public static void saveWalletList(WalletList walletList, String path) {
        try {
            // Attempt to write wallet list to JSON file
            // Uses JsonUtils to handle serialization and file writing
            JsonUtils.writeToFile(walletList, new File(path));
            logger.info("Wallet list saved to JSON file: {}", path);
        } catch (Exception e) {
            logger.error("Failed to save wallet list to '{}': error: {}", path, e.getMessage());
            throw new RuntimeException("Could not save wallet list", e);
        }
    }

    /**
     * Loads a wallet list from a JSON file at the specified path.
     * Uses JsonUtils to handle the file reading and deserialization process.
     *
     * @param path The file path from which to load the wallet list
     * @return The loaded WalletList object
     * @throws RuntimeException if there are any errors during the load operation
     */
    public static WalletList loadWalletList(String path) {
        try {
            // Attempt to read wallet list from JSON file
            // Uses JsonUtils to handle file reading and deserialization
            WalletList loadedWalletList = JsonUtils.readFromFile(new File(path), WalletList.class);
            logger.info("Wallet list loaded from JSON file: {}", path);
            // Return the loaded wallet list
            return loadedWalletList;
        } catch (Exception e) {
            logger.error("Failed to load wallet list from '{}': error: {}", path, e.getMessage());
            throw new RuntimeException("Could not load wallet list", e);
        }
    }
}
