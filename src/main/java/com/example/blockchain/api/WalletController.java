package com.example.blockchain.api;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.blockchain.core.config.ChainConfig;
import com.example.blockchain.core.utils.PersistenceManager;
import com.example.blockchain.logging.BlockchainLoggerFactory;
import com.example.blockchain.logging.LoggingUtils;
import com.example.blockchain.wallet.DTO.WalletDTO;
import com.example.blockchain.wallet.core.Wallet;
import com.example.blockchain.wallet.core.WalletList;
import com.example.blockchain.wallet.utils.WalletUtils;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * REST Controller for managing blockchain wallets.
 * Provides endpoints for creating and listing wallets, with persistence
 * capabilities.
 */
@RestController
@RequestMapping("/api/wallets")
public class WalletController {
    private static final Logger logger = BlockchainLoggerFactory.getLogger(WalletController.class);

    // Maintains the list of all wallets in the system
    @Autowired
    private WalletList walletList;

    /**
     * Constructor initializes the wallet list from persistent storage.
     * If no existing wallet list is found, creates a new empty one.
     */
    public WalletController() {
        LoggingUtils.configureLoggingFromConfig();
    }

    @PostConstruct
    public void loadWalletsFromDiskIfNeeded() {
        if (walletList.isEmpty()) {
            String walletListPath = Path.of(
                    ChainConfig.getInstance().getPersistenceDirectory(),
                    ChainConfig.getInstance().getPersistenceWalletFile()).toString();

            PersistenceManager.loadWalletList(walletListPath).ifPresent(loaded -> {
                walletList.getAllWalletsAsMap().putAll(loaded.getAllWalletsAsMap());
            });
        }
    }

    /**
     * Creates a new wallet for a user.
     * 
     * @param userId   Unique identifier for the user
     * @param userName Name of the user
     * @return Confirmation message of wallet creation
     * @throws Exception if wallet creation fails
     */
    @PostMapping("/generate")
    public String createWallet(@RequestParam("userId") String userId, @RequestParam("userName") String userName)
            throws Exception {
        logger.info("Creating new wallet for user: {}", userName);
        try {
            Wallet wallet = new Wallet(userId, userName);
            walletList.addWallet(userId, userName, wallet);
            WalletUtils.saveWalletKeys(wallet, "wallet-data", userId);
            logger.info("Successfully created wallet for user: {}", userName);
            return "Wallet created for User:{Name: " + userName + ", ID: " + userId + "}";
        } catch (Exception e) {
            logger.error("Failed to create wallet for user: {}", userName, e);
            throw e;
        }
    }

    /**
     * Retrieves all wallets in the system.
     * 
     * @return Collection of wallet entries containing user and wallet information
     */
    @GetMapping
    public List<WalletDTO> list() {
        logger.debug("Retrieving list of all wallets");
        return walletList.getAllWalletEntries().stream()
                .map(WalletDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Saves the current state of wallets before the application shuts down.
     * Called automatically by the Spring container during bean destruction.
     */
    @PreDestroy
    public void saveWallets() {
        logger.info("Saving wallet list before shutdown");
        String path = Path.of(ChainConfig.getInstance().getPersistenceDirectory(),
                ChainConfig.getInstance().getPersistenceWalletFile()).toString();
        PersistenceManager.saveWalletList(walletList, path);
        logger.debug("Wallet list saved to path: {}", path);
    }
}
