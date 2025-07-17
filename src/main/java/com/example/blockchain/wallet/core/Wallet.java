/**
 * Represents a cryptocurrency wallet that manages cryptographic keys and signing operations.
 * This class handles the generation and storage of RSA key pairs used for transaction signing.
 */
package com.example.blockchain.wallet.core;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

public class Wallet {
    // The key pair containing the public and private keys for this wallet
    private KeyPair keyPair;

    /**
     * Creates a new wallet by generating a fresh key pair
     * @throws NoSuchAlgorithmException if the RSA algorithm is not available
     */
    public Wallet() throws NoSuchAlgorithmException {
        this.keyPair = generateKeyPair();
    }

    /**
     * Generates a new RSA key pair for the wallet
     * @return KeyPair containing public and private keys
     * @throws NoSuchAlgorithmException if the RSA algorithm is not available
     */
    private KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048); // Using 2048 bit keys for strong security
        return generator.generateKeyPair();
    }

    /**
     * Gets the public key associated with this wallet
     * @return the wallet's public key
     */
    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }

    /**
     * Gets the private key associated with this wallet
     * @return the wallet's private key
     */
    public PrivateKey getPrivateKey() {
        return keyPair.getPrivate();
    }

    /**
     * Signs the provided data using the wallet's private key
     * @param data the string data to sign
     * @return byte array containing the digital signature
     * @throws Exception if there is an error during the signing process
     */
    public byte[] signData(String data) throws Exception {
        // Create SHA256withRSA signature instance
        Signature signer = Signature.getInstance("SHA256withRSA");
        // Initialize with private key
        signer.initSign(keyPair.getPrivate());
        // Add data
        signer.update(data.getBytes());
        // Generate and return signature
        return signer.sign();
    }
}
