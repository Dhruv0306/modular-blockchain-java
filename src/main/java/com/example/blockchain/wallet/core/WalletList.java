package com.example.blockchain.wallet.core;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WalletList {
    private Map<String, WalletEntry> wallets = new ConcurrentHashMap<>();

    public void addWallet(String userId, String userName, Wallet wallet) {
        wallets.put(userId, new WalletEntry(userId, userName, wallet));
    }

    public Collection<WalletEntry> getAllWallets() {
        return wallets.values();
    }

    public static class WalletEntry {
        public String userId;
        public String userName;
        public Wallet wallet;

        public WalletEntry(String userId, String userName, Wallet wallet) {
            this.userId = userId;
            this.userName = userName;
            this.wallet = wallet;
        }
    }
}
