package com.example.blockchain.wallet.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.security.*;
import java.security.spec.*;
import java.util.Base64;

/**
 * Represents a digital wallet that manages cryptographic keys and signing operations.
 * This class handles RSA key pair generation, storage and digital signatures.
 */
public class Wallet {

    /** The RSA key pair used for cryptographic operations */
    @JsonIgnore
    private KeyPair keyPair;

    /** Unique identifier for the wallet owner */
    private String userId;
    
    /** Name of the wallet owner */
    private String userName;

    /** Base64 encoded private key string for serialization */
    private String encodedPrivateKey;
    
    /** Base64 encoded public key string for serialization */
    private String encodedPublicKey;

    /**
     * Default no-arg constructor required for JSON deserialization
     */
    public Wallet() {
    }

    /**
     * Creates a new wallet with generated RSA key pair
     * @param userId Unique identifier for the wallet owner
     * @param userName Name of the wallet owner
     * @throws NoSuchAlgorithmException if RSA algorithm is not available
     */
    public Wallet(String userId, String userName) throws NoSuchAlgorithmException {
        this.userId = userId;
        this.userName = userName;
        this.keyPair = generateKeyPair();
        this.encodedPrivateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        this.encodedPublicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
    }

    /**
     * Generates a new RSA key pair
     * @return KeyPair containing public and private RSA keys
     * @throws NoSuchAlgorithmException if RSA algorithm is not available
     */
    private KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048); // Use 2048 bit key size for security
        return generator.generateKeyPair();
    }

    /**
     * Gets the wallet's private key, initializing from encoded strings if needed
     * @return The RSA private key
     */
    @JsonIgnore
    public PrivateKey getPrivateKey() {
        ensureKeysInitialized();
        return keyPair.getPrivate();
    }

    /**
     * Gets the wallet's public key, initializing from encoded strings if needed
     * @return The RSA public key
     */
    @JsonIgnore
    public PublicKey getPublicKey() {
        ensureKeysInitialized();
        return keyPair.getPublic();
    }

    /**
     * Initializes the key pair from encoded strings if not already done
     * Called lazily when keys are first accessed
     */
    private void ensureKeysInitialized() {
        if (keyPair == null && encodedPrivateKey != null && encodedPublicKey != null) {
            try {
                // Create key factory and specs for RSA keys
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");

                byte[] privateBytes = Base64.getDecoder().decode(encodedPrivateKey);
                byte[] publicBytes = Base64.getDecoder().decode(encodedPublicKey);

                PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(privateBytes);
                X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(publicBytes);

                // Generate keys from encoded formats
                PrivateKey privateKey = keyFactory.generatePrivate(privateSpec);
                PublicKey publicKey = keyFactory.generatePublic(publicSpec);

                this.keyPair = new KeyPair(publicKey, privateKey);
            } catch (Exception e) {
                throw new RuntimeException("Failed to restore RSA key pair from encoded strings", e);
            }
        }
    }

    /**
     * Signs data using the wallet's private key
     * @param data The string data to sign
     * @return Signature bytes
     * @throws Exception if signing fails
     */
    public byte[] signData(String data) throws Exception {
        Signature signer = Signature.getInstance("SHA256withRSA");
        signer.initSign(getPrivateKey());
        signer.update(data.getBytes());
        return signer.sign();
    }

    // --- Getters and Setters ---

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    @JsonProperty("encodedPrivateKey")
    public String getEncodedPrivateKey() {
        return encodedPrivateKey;
    }

    @JsonProperty("encodedPrivateKey")
    public void setEncodedPrivateKey(String encodedPrivateKey) {
        this.encodedPrivateKey = encodedPrivateKey;
    }

    @JsonProperty("encodedPublicKey")
    public String getEncodedPublicKey() {
        return encodedPublicKey;
    }

    @JsonProperty("encodedPublicKey")
    public void setEncodedPublicKey(String encodedPublicKey) {
        this.encodedPublicKey = encodedPublicKey;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
