package com.example.blockchain.transactions;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

import org.slf4j.Logger;

import com.example.blockchain.Main;
import com.example.blockchain.core.model.SignedTransaction;
import com.example.blockchain.crypto.CryptoUtils;
import com.example.blockchain.logging.BlockchainLoggerFactory;
import com.example.blockchain.logging.LoggingUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents a signed financial transaction in the blockchain.
 * This class implements the SignedTransaction interface and provides
 * functionality
 * for creating, validating and managing financial transactions between parties.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SignedFinancialTransaction implements SignedTransaction {
    private static final Logger logger = BlockchainLoggerFactory.getLogger(SignedFinancialTransaction.class);
    // Sender's identifier/address
    private String sender;
    // Receiver's identifier/address
    private String receiver;
    // Transaction amount
    private double amount;
    // Public key of the sender for signature verification
    @JsonIgnore
    private PublicKey senderPublicKey;
    // Encoded public key of the sender for serialization
    private String encodedSenderPublicKey;
    // Digital signature of the transaction
    private String signature;
    // Unique identifier for the transaction
    private String transactionId;
    // Timestamp when transaction was created
    private long timestamp;
    // Sender's Unique ID, used for polymorphic serialization
    private String senderID;
    // Receiver's Unique ID, used for polymorphic serialization
    private String receiverID;
    // Type of transaction, used for polymorphic serialization
    @JsonIgnore
    private final String type = "SignedFinancialTransaction";

    /**
     * Default constructor required for JSON deserialization
     */
    public SignedFinancialTransaction() {
    }

    /**
     * Creates a new signed financial transaction
     * 
     * @param sender          The transaction sender's identifier
     * @param receiver        The transaction receiver's identifier
     * @param amount          The amount to transfer
     * @param senderPublicKey The sender's public key for verification
     * @param signature       The transaction's digital signature
     * @throws NoSuchAlgorithmException 
     */
    public SignedFinancialTransaction(String sender, String receiver, double amount,
            PublicKey senderPublicKey, String signature, String senderID, String receiverID) throws NoSuchAlgorithmException {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.senderPublicKey = senderPublicKey;
        this.signature = signature;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.timestamp = System.currentTimeMillis();
        try {
            this.transactionId = generateTransactionId();
        } catch (NoSuchAlgorithmException e) {
            String error = "Failed to generate transaction ID. \n Error: " + e.getMessage();
            logger.error(error, e.getMessage());
            throw new NoSuchAlgorithmException(error, e);
        }
        this.encodedSenderPublicKey = Base64.getEncoder().encodeToString(senderPublicKey.getEncoded());
        LoggingUtils.configureLoggingFromConfig();
    }

    /**
     * Creates a new signed financial transaction with a specific transaction ID
     * 
     * @param sender          The transaction sender's identifier
     * @param receiver        The transaction receiver's identifier
     * @param amount          The amount to transfer
     * @param senderPublicKey The sender's public key for verification
     * @param signature       The transaction's digital signature
     * @param transactionId   Custom transaction ID (for testing)
     * @throws NoSuchAlgorithmException 
     */
    public SignedFinancialTransaction(String sender, String receiver, double amount,
            PublicKey senderPublicKey, String signature, String senderID, String receiverID, String transactionId) throws NoSuchAlgorithmException {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.senderPublicKey = senderPublicKey;
        this.signature = signature;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.timestamp = System.currentTimeMillis();
        try {
            this.transactionId = transactionId != null ? transactionId : generateTransactionId();
        } catch (NoSuchAlgorithmException e) {
            String error = "Failed to generate transaction ID. \n Error: " + e.getMessage();
            logger.error(error, e.getMessage());
            throw new NoSuchAlgorithmException(error, e);
        }
        this.encodedSenderPublicKey = Base64.getEncoder().encodeToString(senderPublicKey.getEncoded());
        LoggingUtils.configureLoggingFromConfig();
    }

    /**
     * Creates a new signed financial transaction with a specific transaction ID and
     * timestamp
     * 
     * @param sender          The transaction sender's identifier
     * @param receiver        The transaction receiver's identifier
     * @param amount          The amount to transfer
     * @param senderPublicKey The sender's public key for verification
     * @param signature       The transaction's digital signature
     * @param transactionId   Custom transaction ID (for testing)
     * @param timestamp       Custom timestamp (for testing)
     * @throws NoSuchAlgorithmException 
     */
    public SignedFinancialTransaction(String sender, String receiver, double amount,
            PublicKey senderPublicKey, String signature, String senderID, String receiverID, String transactionId,
            long timestamp) throws NoSuchAlgorithmException {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.senderPublicKey = senderPublicKey;
        this.signature = signature;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.timestamp = timestamp;
        try {
            this.transactionId = transactionId != null ? transactionId : generateTransactionId();
        } catch (NoSuchAlgorithmException e) {
            String error = "Failed to generate transaction ID. \n Error: " + e.getMessage();
            logger.error(error, e.getMessage());
            throw new NoSuchAlgorithmException(error, e);
        }
        this.encodedSenderPublicKey = Base64.getEncoder().encodeToString(senderPublicKey.getEncoded());
        LoggingUtils.configureLoggingFromConfig();
    }

    /**
     * Creates a new signed financial transaction with a specific transaction ID and
     * timestamp
     * 
     * @param sender          The transaction sender's identifier
     * @param receiver        The transaction receiver's identifier
     * @param amount          The amount to transfer
     * @param senderPublicKey The sender's public key for verification
     * @param signature       The transaction's digital signature
     * @param transactionId   Custom transaction ID (for testing)
     * @param timestamp       Custom timestamp (for testing)
     * @throws NoSuchAlgorithmException 
     */
    public SignedFinancialTransaction(String sender, String receiver, double amount,
            PublicKey senderPublicKey, String signature, String senderID, String receiverID, long timestamp) throws NoSuchAlgorithmException {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.senderPublicKey = senderPublicKey;
        this.signature = signature;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.timestamp = timestamp;
        try {
            this.transactionId = generateTransactionId();
        } catch (NoSuchAlgorithmException e) {
            String error = "Failed to generate transaction ID. \n Error: " + e.getMessage();
            logger.error(error, e.getMessage());
            throw new NoSuchAlgorithmException(error, e);
        }
        this.encodedSenderPublicKey = Base64.getEncoder().encodeToString(senderPublicKey.getEncoded());
        LoggingUtils.configureLoggingFromConfig();
    }

    /**
     * Generates a deterministic transaction ID based on the transaction data
     * 
     * @return UUID string generated from transaction details
     * @throws NoSuchAlgorithmException 
     */
    private String generateTransactionId() throws NoSuchAlgorithmException {
        // Create a deterministic ID based on transaction data + signature + timestamp
        try {
            String baseData = sender + receiver + amount + signature + timestamp;
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(baseData.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            logger.error("Failed to generate transaction ID", e.getMessage());
            throw new NoSuchAlgorithmException("Failed to generate transaction ID", e);
        }
    }

    @Override
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Validates the transaction by checking sender, receiver, amount and signature
     * 
     * @return true if transaction is valid, false otherwise
     * @throws NoSuchAlgorithmException 
     */
    @Override
    public boolean isValid() throws NoSuchAlgorithmException {
        try {
            return sender != null && receiver != null && amount > 0 && verifySignature();
        } catch (NoSuchAlgorithmException e) {
            String error = "Failed to validate transaction. \n Error: " + e.getMessage();
            logger.error(error, e.getMessage());
            throw new NoSuchAlgorithmException(error, e);
        }
    }

    @Override
    public String getSender() {
        return sender;
    }

    @Override
    public String getReceiver() {
        return receiver;
    }

    /**
     * Creates a human-readable summary of the transaction
     * 
     * @return String containing sender, receiver, amount and timestamp
     */
    @Override
    public String getSummary() {
        return senderID + " -> " + receiverID + " : $" + amount + " (time: " + timestamp + ")";
    }

    @Override
    public String getSignature() {
        return signature;
    }

    @Override
    public PublicKey getSenderPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (senderPublicKey == null && encodedSenderPublicKey != null) {
            try {
                byte[] keyBytes = Base64.getDecoder().decode(encodedSenderPublicKey);
                X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                senderPublicKey = keyFactory.generatePublic(spec);
            }  catch (NoSuchAlgorithmException e) {
                String error = "Failed to get sender public key. \n Error: " + e.getMessage();
                logger.error(error, e.getMessage());
                throw new NoSuchAlgorithmException(error, e);
            } catch (InvalidKeySpecException e) {
                String error = "Failed to get sender public key. \n Error: " + e.getMessage();
                logger.error(error, e.getMessage());
                throw new InvalidKeySpecException(error, e);
            }
        }
        return senderPublicKey;
    }

    /**
     * Verifies the digital signature of the transaction
     * 
     * @return true if signature is valid, false otherwise
     * @throws NoSuchAlgorithmException 
     */
    @Override
    public boolean verifySignature() throws NoSuchAlgorithmException {
        try {
            String data = getSummary(); // must match the signed input
            // logger.info("Verifying signature for transaction: " + data);
            return CryptoUtils.verifySignature(data, signature, senderPublicKey);
        }  catch (NoSuchAlgorithmException e) {
            String error = "Failed to verify signature. \n Error: " + e.getMessage();
            logger.error(error, e.getMessage());
            throw new NoSuchAlgorithmException(error, e);
        } catch (Exception e) {
            String error = "Failed to verify signature. \n Error: " + e.getMessage();
            logger.error(error, e.getMessage());
            throw new NoSuchAlgorithmException(error, e);
        }
    }

    public double getAmount() {
        return amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return getSummary() + " [ID: " + getTransactionId() + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        SignedFinancialTransaction that = (SignedFinancialTransaction) o;

        return Double.compare(that.amount, amount) == 0 &&
                timestamp == that.timestamp &&
                Objects.equals(sender, that.sender) &&
                Objects.equals(receiver, that.receiver) &&
                Objects.equals(signature, that.signature) &&
                Objects.equals(transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, receiver, amount, signature, transactionId, timestamp);
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getSenderID() {
        return senderID;
    }

    @Override
    public String getReceiverID() {
        return receiverID;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getEncodedSenderPublicKey() {
        return encodedSenderPublicKey;
    }

    @Override
    public Object getHash() {
       return transactionId;
    }
}
