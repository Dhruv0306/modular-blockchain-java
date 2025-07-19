package com.example.blockchain.wallet.core;

import com.example.blockchain.logging.BlockchainLoggerFactory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.security.*;
import java.security.spec.*;
import java.util.Base64;

/**
 * A digital wallet implementation that manages cryptographic keys and digital
 * signatures.
 * Provides functionality for RSA key pair generation, secure key storage, and
 * digital signing operations.
 * Uses 2048-bit RSA keys and SHA-256 with RSA for signatures.
 */
public class Wallet {

    /** The RSA public/private key pair for cryptographic operations */
    @JsonIgnore
    private KeyPair keyPair;

    /** Unique identifier for the wallet owner/user */
    private String userId;

    /** Display name of the wallet owner */
    private String userName;

    /** Base64-encoded private key for persistent storage */
    private String encodedPrivateKey;

    /** Base64-encoded public key for persistent storage */
    private String encodedPublicKey;

    private static org.slf4j.Logger logger = BlockchainLoggerFactory.getLogger(Wallet.class);

    /**
     * Default constructor required for JSON deserialization.
     * Creates an empty wallet without initializing keys.
     */
    public Wallet() {
    }

    /**
     * Creates a new wallet instance with a freshly generated RSA key pair.
     * 
     * @param userId   Unique identifier for the wallet owner
     * @param userName Display name of the wallet owner
     * @throws NoSuchAlgorithmException if the RSA algorithm implementation is not
     *                                  available
     */
    public Wallet(String userId, String userName) throws NoSuchAlgorithmException {
        this.userId = userId;
        this.userName = userName;
        this.keyPair = generateKeyPair();
        this.encodedPrivateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        this.encodedPublicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
    }

    /**
     * Generates a new 2048-bit RSA key pair for cryptographic operations.
     * 
     * @return A new KeyPair containing public and private RSA keys
     * @throws NoSuchAlgorithmException if RSA algorithm is not available in the JCA
     */
    private KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048); // Use 2048-bit key size for adequate security
        return generator.generateKeyPair();
    }

    /**
     * Retrieves the wallet's RSA private key, initializing from encoded storage if
     * necessary.
     * 
     * @return The wallet's RSA private key for signing operations
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    @JsonIgnore
    public PrivateKey getPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        try {
            ensureKeysInitialized();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Error initializing keys: " + e.getMessage());
            String error = "Error initializing keys: " + e.getMessage();
            throw new NoSuchAlgorithmException(error, e);
        } catch (InvalidKeySpecException e) {
            logger.error("Error initializing keys: " + e.getMessage());
            String error = "Error initializing keys: " + e.getMessage();
            throw new InvalidKeySpecException(error, e);
        }
        return keyPair.getPrivate();
    }

    /**
     * Retrieves the wallet's RSA public key, initializing from encoded storage if
     * necessary.
     * 
     * @return The wallet's RSA public key for verification
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeySpecException 
     */
    @JsonIgnore
    public PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        try {
            ensureKeysInitialized();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Error initializing keys: " + e.getMessage());
            String error = "Error initializing keys: " + e.getMessage();
            throw new NoSuchAlgorithmException(error, e);
        } catch (InvalidKeySpecException e) {
            logger.error("Error initializing keys: " + e.getMessage());
            String error = "Error initializing keys: " + e.getMessage();
            throw new InvalidKeySpecException(error, e);
        }
        return keyPair.getPublic();
    }

    /**
     * Initializes the key pair from encoded strings if not already loaded.
     * Handles PEM format by stripping headers and whitespace before decoding.
     * 
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    private void ensureKeysInitialized() throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (keyPair == null && encodedPrivateKey != null && encodedPublicKey != null) {
            try {
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");

                // Clean PEM format headers and whitespace
                String cleanPrivateKey = encodedPrivateKey
                        .replace("-----BEGIN PRIVATE KEY-----", "")
                        .replace("-----END PRIVATE KEY-----", "")
                        .replaceAll("\\s", "");

                String cleanPublicKey = encodedPublicKey
                        .replace("-----BEGIN PUBLIC KEY-----", "")
                        .replace("-----END PUBLIC KEY-----", "")
                        .replaceAll("\\s", "");

                byte[] privateBytes = Base64.getDecoder().decode(cleanPrivateKey);
                byte[] publicBytes = Base64.getDecoder().decode(cleanPublicKey);

                PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(privateBytes);
                X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(publicBytes);

                PrivateKey privateKey = keyFactory.generatePrivate(privateSpec);
                PublicKey publicKey = keyFactory.generatePublic(publicSpec);

                this.keyPair = new KeyPair(publicKey, privateKey);
            } catch (NullPointerException e) {
                logger.error("Error initializing keys: " + e.getMessage());
                String error = "Error initializing keys: " + e.getMessage();
                throw new NullPointerException(error);
            } catch (NoSuchAlgorithmException e) {
                logger.error("Error initializing keys: " + e.getMessage());
                String error = "Error initializing keys: " + e.getMessage();
                throw new NoSuchAlgorithmException(error, e);
            } catch (InvalidKeySpecException e) {
                logger.error("Error initializing keys: " + e.getMessage());
                String error = "Error initializing keys: " + e.getMessage();
                throw new InvalidKeySpecException(error, e);
            }
        }
    }

    /**
     * Signs the provided data using SHA-256 with RSA and the wallet's private key.
     * 
     * @param data The string data to be signed
     * @return The RSA signature bytes
     * @throws Exception if the signing operation fails
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
