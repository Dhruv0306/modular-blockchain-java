/**
 * A thread-safe container class for managing multiple user wallets.
 * Uses ConcurrentHashMap to ensure safe concurrent access and modifications.
 * This class serves as the central registry for all user wallets in the system.
 */
package com.example.blockchain.wallet.core;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.example.blockchain.core.utils.JsonUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Component
public class WalletList {
    // Thread-safe map storing user wallet entries indexed by user ID
    private Map<String, WalletEntry> wallets = new ConcurrentHashMap<>();

    /**
     * Adds a new wallet entry to the list.
     * If an entry already exists for the given userId, it will be overwritten.
     * 
     * @param userId   Unique identifier for the user
     * @param userName Display name of the user
     * @param wallet   The user's wallet object containing balance and transaction history
     */
    public void addWallet(String userId, String userName, Wallet wallet) {
        wallets.put(userId, new WalletEntry(userId, userName, wallet));
    }

    /**
     * Returns all wallet entries in the list as an immutable collection.
     * Creates a new ArrayList to prevent external modifications to internal state.
     * 
     * @return Collection of all WalletEntry objects currently registered
     */
    @JsonIgnore
    public Collection<WalletEntry> getAllWalletEntries() {
        return new java.util.ArrayList<>(wallets.values());
    }

    /**
     * Inner class representing a single wallet entry with associated user details.
     * Used to bundle user identification with their wallet information.
     */
    public static class WalletEntry {
        // Unique identifier for the user, used as the key in the wallets map
        public String userId;
        // Human-readable display name for the user
        public String userName;
        // User's wallet instance containing their balance and transactions
        public Wallet wallet;

        /**
         * Default constructor required for JSON deserialization.
         * Should not be used directly in application code.
         */
        public WalletEntry() {
            // Default constructor needed for Jackson deserialization
        }

        /**
         * Creates a new wallet entry with the specified user details and wallet.
         * 
         * @param userId   Unique identifier for the user
         * @param userName Display name of the user
         * @param wallet   The user's wallet object containing their financial data
         */
        public WalletEntry(String userId, String userName, Wallet wallet) {
            this.userId = userId;
            this.userName = userName;
            this.wallet = wallet;
        }
    }

    /**
     * Returns the total number of wallet entries currently registered.
     * 
     * @return Integer count of registered wallets
     */
    public int size() {
        return wallets.size();
    }

    /**
     * Checks if a wallet entry exists for the given user ID.
     * Useful for validation before performing operations on a wallet.
     * 
     * @param userId Unique identifier for the user
     * @return true if wallet exists for the user, false otherwise
     */
    public boolean containsUser(String userId) {
        return wallets.containsKey(userId);
    }

    /**
     * Retrieves the wallet entry for a specific user.
     * Returns null if no wallet exists for the given user ID.
     * 
     * @param userId Unique identifier for the user
     * @return WalletEntry object containing user and wallet information, or null
     */
    @JsonIgnore
    public WalletEntry getWalletByUserID(String userId) {
        return wallets.get(userId);
    }

    /**
     * Returns the internal map of wallets for direct access.
     * Note: Returns reference to internal map - use with caution.
     * 
     * @return Map of user IDs to their corresponding WalletEntry objects
     */
    public Map<String, WalletEntry> getAllWalletsAsMap() {
        return wallets;
    }

    /**
     * Checks if the wallet list contains any entries.
     * 
     * @return true if no wallets are registered, false if at least one exists
     */
    @JsonIgnore
    public boolean isEmpty() {
        return wallets.isEmpty();
    }

    /**
     * Exports the current wallet list to a JSON file.
     * Uses JsonUtils to handle serialization.
     * 
     * @param file The destination file to write the JSON data
     * @return Success message with the file path
     * @throws RuntimeException if export fails
     */
    public String exportToJson(File file) {
        try {
            JsonUtils.writeToFile(this, file);
        } catch (Exception e) {
            throw new RuntimeException("Failed to export wallet list to JSON file: " + file.getAbsolutePath(), e);
        }
        return "Wallet list exported to " + file.getAbsolutePath();
    }

    /**
     * Removes a wallet entry from the list.
     * If no wallet exists for the given user ID, this operation has no effect.
     * 
     * @param userId Unique identifier for the user whose wallet should be removed
     */
    @JsonIgnore
    public void removeWallet(String userId) {
        wallets.remove(userId);
    }
}
