package com.example.blockchain;

import com.example.blockchain.core.chain.Blockchain;
import com.example.blockchain.core.config.ChainConfig;
import com.example.blockchain.core.config.DefaultGenesisBlockFactory;
import com.example.blockchain.core.utils.PersistenceManager;
import com.example.blockchain.examples.DemoRunner;
import com.example.blockchain.logging.BlockchainLoggerFactory;
import com.example.blockchain.logging.LoggingUtils;
import com.example.blockchain.transactions.FinancialTransaction;

import org.slf4j.Logger;

/**
 * Main entry point for the modular blockchain application.
 * 
 * This class handles the initialization and setup of the blockchain system including:
 * 1. Loading environment-specific configurations from properties files
 * 2. Setting up logging infrastructure and configuration
 * 3. Managing blockchain persistence and state
 * 4. Running blockchain demonstrations via DemoRunner
 *
 * The application can be configured through:
 * - Command line arguments (first arg is config file path)
 * - BLOCKCHAIN_ENV environment variable
 * - Default configuration file (config/blockchain.properties)
 */
public class Main {
    // Logger instance for this class
    private static final Logger logger = BlockchainLoggerFactory.getLogger(Main.class);

    /**
     * Main entry point for the blockchain application.
     * Handles initialization, configuration loading, and blockchain operations.
     *
     * @param args Command line arguments - first argument can be path to config file
     */
    public static void main(String[] args) {
        // Default configuration file path
        String configFile = "config/blockchain.properties"; // default

        // Override config file path if provided as command line argument
        if (args.length > 0) {
            configFile = args[0];
        }

        // Check for environment-specific configuration
        String env = System.getenv("BLOCKCHAIN_ENV");
        if (env != null && !env.isEmpty()) {
            configFile = "config/blockchain-" + env + ".properties";
        }

        // Load and initialize configuration
        ChainConfig config = ChainConfig.getInstance(configFile);

        // Setup logging based on configuration
        LoggingUtils.configureLoggingFromConfig();

        // Output current configuration settings
        logger.info("Using configuration:");
        logger.info("- Difficulty: " + config.getDifficulty());
        logger.info("- Genesis hash: " + config.getGenesisHash());
        logger.info("- Log level: " + config.getLogLevel());
        logger.info("");

        // Display startup banner
        logger.info("\n" + "=".repeat(100) + "\n" +
                "Starting Modular Blockchain with configuration: {}\n" +
                "=".repeat(100), configFile);
        Blockchain<FinancialTransaction> blockchain = null;

        // Handle blockchain persistence if enabled
        if (config.isPersistenceEnabled()) {
            logger.info("\n" + "=".repeat(50) + "\n" +
                    "Loading blockchain from persistence...\n" +
                    "=".repeat(50) + "\n");
            String persistenceDir = config.getPersistenceDirectory();
            String persistenceFile = config.getPersistenceFile();

            // Attempt to load existing blockchain or create new one if none exists
            blockchain = PersistenceManager
                    .loadIfConfigured(FinancialTransaction.class, persistenceDir, persistenceFile)
                    .orElseGet(() -> new Blockchain<>(new DefaultGenesisBlockFactory<>()));
            logger.info("Blockchain loaded with {} blocks.", blockchain.getChain().size());

            // Execute demonstration scenarios
            logger.info("Running demo with existing blockchain...");
            DemoRunner demoRunner = new DemoRunner();
            demoRunner.runWithExistingBlockchain(blockchain, config);
            logger.info("Demo completed.\n");

            // Persist final blockchain state
            logger.info("Saving blockchain state to persistence...");
            PersistenceManager.saveIfEnabled(blockchain, persistenceDir, persistenceFile);
            logger.info("Blockchain state saved to: {}/{}", persistenceDir, persistenceFile);
        } else {
            // Warn if persistence is disabled
            logger.warn("Blockchain persistence is disabled. No data will be saved.");
        }
    }
}
