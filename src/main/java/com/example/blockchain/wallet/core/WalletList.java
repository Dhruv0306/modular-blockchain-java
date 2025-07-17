/**
 * A thread-safe container class for managing multiple user wallets.
 * Uses ConcurrentHashMap for safe concurrent access.
 */
package com.example.blockchain.wallet.core;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WalletList {
    // Map of user IDs to their wallet entries
    private Map<String, WalletEntry> wallets = new ConcurrentHashMap<>();

    /**
     * Adds a new wallet entry to the list
     * @param userId Unique identifier for the user
     * @param userName Display name of the user
     * @param wallet The user's wallet object
     */
    public void addWallet(String userId, String userName, Wallet wallet) {
        wallets.put(userId, new WalletEntry(userId, userName, wallet));
    }

    /**
     * Returns all wallet entries in the list
     * @return Collection of all WalletEntry objects
     */
    public Collection<WalletEntry> getAllWallets() {
        return wallets.values();
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
         * Creates a new wallet entry
         * @param userId Unique identifier for the user
         * @param userName Display name of the user
         * @param wallet The user's wallet object
         */
        public WalletEntry(String userId, String userName, Wallet wallet) {
            this.userId = userId;
            this.userName = userName;
            this.wallet = wallet;
        }
    }
}
