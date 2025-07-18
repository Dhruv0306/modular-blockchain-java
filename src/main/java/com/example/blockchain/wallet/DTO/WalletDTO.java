package com.example.blockchain.wallet.DTO;

import java.util.Base64;

import com.example.blockchain.wallet.core.Wallet;
import com.example.blockchain.wallet.core.WalletList;

/**
 * Data Transfer Object for Wallet information
 * Contains user identification and public key data
 */
public class WalletDTO {
    // User's unique identifier
    private String userId;
    
    // User's display name
    private String userName;
    
    // Base64 encoded public key string
    private String publicKeyBase64;

    /**
     * Constructs WalletDTO from a Wallet object
     * @param wallet The source Wallet object
     */
    public WalletDTO(Wallet wallet) {
        this.userId = wallet.getUserId();
        this.userName = wallet.getUserName();
        this.publicKeyBase64 = Base64.getEncoder().encodeToString(wallet.getPublicKey().getEncoded());
    }

    /**
     * Constructs WalletDTO from a WalletEntry object
     * @param entry The source WalletEntry object
     */
    public WalletDTO(WalletList.WalletEntry entry) {
        this.userId = entry.userId;
        this.userName = entry.userName;
        this.publicKeyBase64 = Base64.getEncoder().encodeToString(entry.wallet.getPublicKey().getEncoded());
    }

    /**
     * @return The user's ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @return The user's name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @return Base64 encoded public key
     */
    public String getPublicKeyBase64() {
        return publicKeyBase64;
    }
}
