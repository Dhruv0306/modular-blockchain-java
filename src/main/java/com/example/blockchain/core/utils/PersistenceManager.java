package com.example.blockchain.core.utils;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

import com.example.blockchain.core.chain.Blockchain;
import com.example.blockchain.core.model.Transaction;
import com.example.blockchain.core.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages persistence operations for the blockchain, including saving and loading from JSON files.
 */
public class PersistenceManager {
    // Logger instance for this class
    private static final Logger logger = LoggerFactory.getLogger(PersistenceManager.class);

    /**
     * Attempts to load a blockchain from a JSON file if it exists.
     *
     * @param clazz The Transaction class type
     * @param directory The directory containing the blockchain file
     * @param filename The name of the blockchain file
     * @return Optional containing the loaded blockchain if successful, empty Optional otherwise
     * @param <T> Type parameter extending Transaction
     */
    public static <T extends Transaction> Optional<Blockchain<T>> loadIfConfigured(Class<T> clazz, String directory, String filename) {
        String path = Path.of(directory, filename).toString();
        try {
            // Attempt to import blockchain from JSON file
            Blockchain<T> chain = Blockchain.importFromJson(new File(path), clazz);
            logger.info("Blockchain loaded from JSON file: {}", path);
            return Optional.of(chain);
        } catch (Exception e) {
            logger.warn("Failed to load blockchain from '{}': {}", path, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Saves the current state of the blockchain to a JSON file.
     * Creates the necessary directories and file if they don't exist.
     *
     * @param blockchain The blockchain to save
     * @param directory The directory to save the blockchain file in
     * @param filename The name of the file to save the blockchain to
     * @param <T> Type parameter extending Transaction
     */
    public static <T extends Transaction> void saveIfEnabled(Blockchain<T> blockchain, String directory, String filename) {
        String path = Path.of(directory, filename).toString();
        try {
            // Ensure the File exists
            File file = new File(path);
            if(!file.exists()){
                // Create parent directories if they don't exist
                if (!file.getParentFile().exists()){
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
            }
            // Export blockchain to JSON file
            blockchain.exportToJson(new File(path));
            logger.info("Blockchain state saved to: {}", path);
        } catch (Exception e) {
            logger.warn("Failed to save blockchain to '{}': {}", path, e.getMessage());
        }
    }
}
