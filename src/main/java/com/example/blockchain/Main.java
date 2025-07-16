package com.example.blockchain;

import com.example.blockchain.core.config.ChainConfig;
import com.example.blockchain.examples.DemoRunner;
import com.example.blockchain.logging.BlockchainLoggerFactory;
import com.example.blockchain.logging.LoggingUtils;

import org.slf4j.Logger;

/**
 * Main entry point for the modular blockchain.
 * 
 * This class handles:
 * 1. Loading environment-specific configurations
 * 2. Setting up logging
 * 3. Running blockchain demonstrations via DemoRunner
 */
public class Main {
    private static final Logger logger = BlockchainLoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        // Load the appropriate config based on environment or command line argument
        String configFile = "config/blockchain.properties"; // default

        // If args provided, use first arg as config file
        if (args.length > 0) {
            configFile = args[0];
        }

        // Check for BLOCKCHAIN_ENV environment variable
        String env = System.getenv("BLOCKCHAIN_ENV");
        if (env != null && !env.isEmpty()) {
            configFile = "config/blockchain-" + env + ".properties";
        }

        // Initialize config with the appropriate file
        ChainConfig config = ChainConfig.getInstance(configFile);

        // Configure logging based on blockchain configuration
        LoggingUtils.configureLoggingFromConfig();

        // Output the current configuration
        logger.info("Using configuration:");
        logger.info("- Difficulty: " + config.getDifficulty());
        logger.info("- Genesis hash: " + config.getGenesisHash());
        logger.info("- Log level: " + config.getLogLevel());
        logger.info("");

        // Create and run the demo examples
        DemoRunner demoRunner = new DemoRunner();
        
        // Run the default blockchain example
        demoRunner.runDefaultBlockchainExample(config);
        
        logger.info("\n" + "=".repeat(100) + "\n" + 
                    "Running custom genesis blockchain example...\n" + 
                    "=".repeat(100) + "\n");
        
        // Run the custom genesis blockchain example
        demoRunner.runCustomGenesisBlockchainExample(config);
    }
}
