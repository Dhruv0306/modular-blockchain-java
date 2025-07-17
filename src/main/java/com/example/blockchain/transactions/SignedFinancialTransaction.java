package com.example.blockchain.transactions;

import java.security.KeyFactory;
import java.security.PublicKey;
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
     */
    public SignedFinancialTransaction(String sender, String receiver, double amount,
            PublicKey senderPublicKey, String signature, String senderID, String receiverID) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.senderPublicKey = senderPublicKey;
        this.signature = signature;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.timestamp = System.currentTimeMillis();
        this.transactionId = generateTransactionId();
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
     */
    public SignedFinancialTransaction(String sender, String receiver, double amount,
            PublicKey senderPublicKey, String signature, String senderID, String receiverID, String transactionId) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.senderPublicKey = senderPublicKey;
        this.signature = signature;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.timestamp = System.currentTimeMillis();
        this.transactionId = transactionId != null ? transactionId : generateTransactionId();
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
     */
    public SignedFinancialTransaction(String sender, String receiver, double amount,
            PublicKey senderPublicKey, String signature, String senderID, String receiverID, String transactionId,
            long timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.senderPublicKey = senderPublicKey;
        this.signature = signature;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.timestamp = timestamp;
        this.transactionId = transactionId != null ? transactionId : generateTransactionId();
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
     */
    public SignedFinancialTransaction(String sender, String receiver, double amount,
            PublicKey senderPublicKey, String signature, String senderID, String receiverID, long timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.senderPublicKey = senderPublicKey;
        this.signature = signature;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.timestamp = timestamp;
        this.transactionId = generateTransactionId();
        this.encodedSenderPublicKey = Base64.getEncoder().encodeToString(senderPublicKey.getEncoded());
        LoggingUtils.configureLoggingFromConfig();
    }

    /**
     * Generates a deterministic transaction ID based on the transaction data
     * 
     * @return UUID string generated from transaction details
     */
    private String generateTransactionId() {
        // Create a deterministic ID based on transaction data + signature + timestamp
        String baseData = sender + receiver + amount + signature + timestamp;
        return UUID.nameUUIDFromBytes(baseData.getBytes()).toString();
    }

    @Override
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Validates the transaction by checking sender, receiver, amount and signature
     * 
     * @return true if transaction is valid, false otherwise
     */
    @Override
    public boolean isValid() {
        return sender != null && receiver != null && amount > 0 && verifySignature();
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
    public PublicKey getSenderPublicKey() {
        if (senderPublicKey == null && encodedSenderPublicKey != null) {
            try {
                byte[] keyBytes = Base64.getDecoder().decode(encodedSenderPublicKey);
                X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                senderPublicKey = keyFactory.generatePublic(spec);
            } catch (Exception e) {
                throw new RuntimeException("Failed to reconstruct public key", e);
            }
        }
        return senderPublicKey;
    }

    /**
     * Verifies the digital signature of the transaction
     * 
     * @return true if signature is valid, false otherwise
     */
    @Override
    public boolean verifySignature() {
        try {
            String data = getSummary(); // must match the signed input
            logger.info("Verifying signature for transaction: " + data);
            return CryptoUtils.verifySignature(data, signature, senderPublicKey);
        } catch (Exception e) {
            return false;
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
    
}
