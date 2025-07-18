/**
 * A thread-safe container class for managing multiple user wallets.
 * Uses ConcurrentHashMap for safe concurrent access.
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
    // Map of user IDs to their wallet entries
    private Map<String, WalletEntry> wallets = new ConcurrentHashMap<>();

    /**
     * Adds a new wallet entry to the list
     * 
     * @param userId   Unique identifier for the user
     * @param userName Display name of the user
     * @param wallet   The user's wallet object
     */
    public void addWallet(String userId, String userName, Wallet wallet) {
        wallets.put(userId, new WalletEntry(userId, userName, wallet));
    }

    /**
     * Returns all wallet entries in the list
     * 
     * @return Collection of all WalletEntry objects
     */
    @JsonIgnore
    public Collection<WalletEntry> getAllWalletEntries() {
        return new java.util.ArrayList<>(wallets.values());
    }

    /**
     * Inner class representing a single wallet entry with user details
     */
    public static class WalletEntry {
        // User's unique identifier
        public String userId;
        // User's display name
        public String userName;
        // User's wallet instance
        public Wallet wallet;

        /**
         * Default constructor for JSON deserialization
         */
        public WalletEntry() {
            // Default constructor needed for Jackson deserialization
        }

        /**
         * Creates a new wallet entry
         * 
         * @param userId   Unique identifier for the user
         * @param userName Display name of the user
         * @param wallet   The user's wallet object
         */
        public WalletEntry(String userId, String userName, Wallet wallet) {
            this.userId = userId;
            this.userName = userName;
            this.wallet = wallet;
        }
    }

    /**
     * Retrieves a wallet entry by user ID
     * 
     * @param userId Unique identifier for the user
     * @return WalletEntry object if found, null otherwise
     */
    public int size() {
        // Returns the number of wallets in the list
        return wallets.size();
    }

    /**
     * Checks if a wallet entry exists for the given user ID
     * 
     * @param userId Unique identifier for the user
     * @return true if wallet exists, false otherwise
     */
    public boolean containsUser(String userId) {
        // Checks if a wallet entry exists for the given user ID
        return wallets.containsKey(userId);
    }

    /**
     * Retrieves the wallet entry for a specific user
     * 
     * @param userId Unique identifier for the user
     * @return WalletEntry object containing user and wallet information
     */
    @JsonIgnore
    public WalletEntry getWalletByUserID(String userId) {
        // Retrieves the wallet entry for the given user ID
        return wallets.get(userId);
    }

    /**
     * Returns the internal map of wallets for direct access
     * 
     * @return Map of user IDs to WalletEntry objects
     */
    public Map<String, WalletEntry> getAllWalletsAsMap() {
        // Returns the internal map of wallets
        return wallets;
    }

    /**
     * Checks if the wallet list is empty
     * 
     * @return true if no wallets are present, false otherwise
     */
    @JsonIgnore
    public boolean isEmpty() {
        // Checks if the wallet list is empty
        return wallets.isEmpty();
    }

    /**
     * Exports the current wallet list to JSON format
     * 
     * @return String containing the JSON representation of all wallets
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
     * Removes a wallet entry from the list for the specified user ID
     * 
     * @param userId Unique identifier for the user whose wallet should be removed
     */
    @JsonIgnore
    public void removeWallet(String userId) {
        // Remove the wallet entry associated with the given user ID from the map
        wallets.remove(userId);
    }
}
