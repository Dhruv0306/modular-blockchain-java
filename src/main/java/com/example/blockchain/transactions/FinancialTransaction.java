package com.example.blockchain.transactions;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

import com.example.blockchain.core.model.Transaction;
import com.example.blockchain.logging.BlockchainLoggerFactory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents a financial transaction between two parties in the blockchain.
 * This class implements the Transaction interface and handles the core
 * transaction data
 * including sender, receiver, amount and transaction ID.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FinancialTransaction implements Transaction {
    // The address/identifier of the transaction sender
    private String sender;
    // The address/identifier of the transaction receiver
    private String receiver;
    // The amount being transferred in the transaction
    private double amount;
    // Unique identifier for this transaction
    private String transactionId;
    // Sender's Unique ID, used for polymorphic serialization
    private String senderID;
    // Receiver's Unique ID, used for polymorphic serialization
    private String receiverID;
    // Type of transaction, used for polymorphic serialization
    @JsonIgnore
    private final String type = "FinancialTransaction";

    private static final org.slf4j.Logger logger = BlockchainLoggerFactory.getLogger(FinancialTransaction.class);

    /**
     * Default constructor required for JSON deserialization.
     * Creates an empty transaction that must be populated via setters.
     */
    public FinancialTransaction() {
    }

    /**
     * Creates a new financial transaction with the specified sender, receiver and
     * amount.
     * Automatically generates a transaction ID.
     *
     * @param sender   The address of the sending party
     * @param receiver The address of the receiving party
     * @param amount   The amount to transfer
     * @param senderID The unique identifier of the sender
     * @param receiverID The unique identifier of the receiver
     * @throws NoSuchAlgorithmException 
     */
    public FinancialTransaction(String sender, String receiver, double amount, String senderID, String receiverID) throws NoSuchAlgorithmException {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.senderID = senderID;
        this.receiverID = receiverID;
        try {
            this.transactionId = generateTransactionId();
        } catch (NoSuchAlgorithmException e) {
            String error = "Error generating transaction ID. \nError: " + e.getMessage();
            logger.error(error, e);
            throw new NoSuchAlgorithmException(error, e);
        }
    }

    /**
     * Creates a new financial transaction with a specified transaction ID.
     * Primarily used for testing or special cases where ID needs to be controlled.
     *
     * @param sender        The address of the sending party
     * @param receiver      The address of the receiving party
     * @param amount        The amount to transfer
     * @param senderID      The unique identifier of the sender
     * @param receiverID    The unique identifier of the receiverq
     * @param transactionId The specific transaction ID to use, or null to generate
     *                      one
     * @throws NoSuchAlgorithmException 
     */
    public FinancialTransaction(String sender, String receiver, double amount, String senderID, String receiverID, String transactionId) throws NoSuchAlgorithmException {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.senderID = senderID;
        this.receiverID = receiverID;
        try {
            this.transactionId = transactionId != null ? transactionId : generateTransactionId();
        } catch (NoSuchAlgorithmException e) {
            String error = "Error generating transaction ID. \nError: " + e.getMessage();
            logger.error(error, e);
            throw new NoSuchAlgorithmException(error, e);
        }
    }

    /**
     * Creates a new financial transaction with the specified sender, receiver and
     * amount.
     * Automatically generates a transaction ID.
     *
     * @param sender   The address of the sending party
     * @param receiver The address of the receiving party
     * @param amount   The amount to transfer
     * @throws NoSuchAlgorithmException 
     */
    public FinancialTransaction(String sender, String receiver, double amount) throws NoSuchAlgorithmException{
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.senderID = "U00-1";
        this.receiverID = "U00-2";
        try {
            this.transactionId = generateTransactionId();
        } catch (NoSuchAlgorithmException e) {
            String error = "Error generating transaction ID. \nError: " + e.getMessage();
            logger.error(error, e);
            throw new NoSuchAlgorithmException(error, e);
        }
    }

    /**
     * Creates a new financial transaction with a specified transaction ID.
     * Primarily used for testing or special cases where ID needs to be controlled.
     *
     * @param sender        The address of the sending party
     * @param receiver      The address of the receiving party
     * @param amount        The amount to transfer
     * @param transactionId The specific transaction ID to use, or null to generate
     *                      one
     * @throws NoSuchAlgorithmException 
     */
    public FinancialTransaction(String sender, String receiver, double amount, String transactionId) throws NoSuchAlgorithmException {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.senderID = "U00-1";
        this.receiverID = "U00-2";
        try {
            this.transactionId = transactionId != null ? transactionId : generateTransactionId();
        } catch (NoSuchAlgorithmException e) {
            String error = "Error generating transaction ID. \nError: " + e.getMessage();
            logger.error(error, e);
            throw new NoSuchAlgorithmException(error, e);
        }
    }

    /**
     * Generates a unique transaction ID based on the transaction details and
     * timestamp.
     * Uses UUID.nameUUIDFromBytes to create a deterministic but unique identifier.
     *
     * @return A string representation of the generated UUID
     * @throws NoSuchAlgorithmException 
     */
    private String generateTransactionId() throws NoSuchAlgorithmException {
        // Create a deterministic ID based on transaction data + random component
        try {
            String baseData = sender + receiver + amount + System.currentTimeMillis();
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(baseData.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            String error = "Error generating transaction ID. \nError: " + e.getMessage();
            logger.error(error, e);
            throw new NoSuchAlgorithmException(error, e);
        }
    }

    /**
     * @return The unique identifier for this transaction
     */
    @Override
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Validates the transaction by checking if sender, receiver are set and amount
     * is positive.
     * 
     * @return true if the transaction is valid, false otherwise
     */
    @Override
    public boolean isValid() {
        return sender != null && receiver != null && amount > 0;
    }

    /**
     * @return The address of the sending party
     */
    @Override
    public String getSender() {
        return sender;
    }

    /**
     * @return The address of the receiving party
     */
    @Override
    public String getReceiver() {
        return receiver;
    }

    /**
     * Creates a human-readable summary of the transaction.
     * 
     * @return A string in the format "sender -> receiver : $amount"
     */
    @Override
    public String getSummary() {
        return sender + " -> " + receiver + " : $" + amount;
    }

    /**
     * @return The amount being transferred in this transaction
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Creates a detailed string representation of the transaction.
     * 
     * @return A string containing the transaction summary and ID
     */
    @Override
    public String toString() {
        return getSummary() + " [ID: " + getTransactionId() + "]";
    }

    /**
     * Compares this transaction with another object for equality.
     * Two transactions are considered equal if they have the same sender, receiver,
     * amount and transaction ID.
     *
     * @param o The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        FinancialTransaction that = (FinancialTransaction) o;

        return Double.compare(that.amount, amount) == 0 &&
                Objects.equals(sender, that.sender) &&
                Objects.equals(receiver, that.receiver) &&
                Objects.equals(transactionId, that.transactionId);
    }

    /**
     * Generates a hash code for this transaction based on its properties.
     * 
     * @return A hash code value for this transaction
     */
    @Override
    public int hashCode() {
        return Objects.hash(sender, receiver, amount, transactionId);
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getSenderID() {
        // Returns the sender's unique ID for polymorphic serialization
        return senderID;
    }

    @Override
    public String getReceiverID() {
        // Returns the receiver's unique ID for polymorphic serialization
        return receiverID;
    }

    @Override
    public Object getHash() {
        return transactionId;
    }
}
