package com.example.blockchain.wallet.DTO;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import com.example.blockchain.logging.BlockchainLoggerFactory;
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

    private static final org.slf4j.Logger logger = BlockchainLoggerFactory.getLogger(WalletDTO.class);

    /**
     * Constructs WalletDTO from a Wallet object
     * 
     * @param wallet The source Wallet object
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public WalletDTO(Wallet wallet) throws NoSuchAlgorithmException, InvalidKeySpecException {
        this.userId = wallet.getUserId();
        this.userName = wallet.getUserName();
        try {
            this.publicKeyBase64 = Base64.getEncoder().encodeToString(wallet.getPublicKey().getEncoded());
        } catch (NoSuchAlgorithmException e) {
            logger.error("Failed to get public key from wallet.\nError: " + e.getMessage() + "\n");
            String error = "Failed to get public key from wallet.\nError: " + e.getMessage() + "\n";
            throw new NoSuchAlgorithmException(error, e);
        } catch (InvalidKeySpecException e) {
            logger.error("Failed to get public key from wallet.\nError: " + e.getMessage() + "\n");
            String error = "Failed to get public key from wallet.\nError: " + e.getMessage() + "\n";
            throw new InvalidKeySpecException(error, e);
        }
    }

    /**
     * Constructs WalletDTO from a WalletEntry object
     * 
     * @param entry The source WalletEntry object
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public WalletDTO(WalletList.WalletEntry entry) throws NoSuchAlgorithmException, InvalidKeySpecException {
        this.userId = entry.userId;
        this.userName = entry.userName;
        try {
            this.publicKeyBase64 = Base64.getEncoder().encodeToString(entry.wallet.getPublicKey().getEncoded());
        } catch (NoSuchAlgorithmException e) {
            logger.error("Failed to get public key from wallet.\nError: " + e.getMessage() + "\n");
            String error = "Failed to get public key from wallet.\nError: " + e.getMessage() + "\n";
            throw new NoSuchAlgorithmException(error, e);
        } catch (InvalidKeySpecException e) {
            logger.error("Failed to get public key from wallet.\nError: " + e.getMessage() + "\n");
            String error = "Failed to get public key from wallet.\nError: " + e.getMessage() + "\n";
            throw new InvalidKeySpecException(error, e);
        }
    }
    
    public WalletDTO() {
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
