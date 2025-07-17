package com.example.blockchain.wallet.utils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.blockchain.logging.LoggingUtils;
import com.example.blockchain.wallet.core.Wallet;

/**
 * Utility class for handling wallet-related operations
 */
public class WalletUtils {
    // Logger instance for this class
    private static final Logger logger = LoggerFactory.getLogger(WalletUtils.class);

    /**
     * Saves the public and private keys of a wallet to files in the specified directory
     *
     * @param wallet The wallet containing the keys to save
     * @param directory The directory path where the key files will be stored
     * @param name The base name for the key files
     * @throws Exception If there is an error creating directories or writing files
     */
    public static void saveWalletKeys(Wallet wallet, String directory, String name) throws Exception {
        // Configure logging using external configuration
        LoggingUtils.configureLoggingFromConfig();
        logger.info("Saving wallet keys for user: {}", name);

        // Create directory if it doesn't exist
        Files.createDirectories(Paths.get(directory));

        // Save public key to file with _pub.key extension
        Files.writeString(Paths.get(directory, name + "_pub.key"),
                Base64.getEncoder().encodeToString(wallet.getPublicKey().getEncoded()));

        // Save private key to file with _priv.key extension
        Files.writeString(Paths.get(directory, name + "_priv.key"),
                Base64.getEncoder().encodeToString(wallet.getPrivateKey().getEncoded()));
    }
}
