package com.example.blockchain.transactions;

import java.util.Objects;
import java.util.UUID;

import com.example.blockchain.core.model.Transaction;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents a financial transaction between two parties in the blockchain.
 * This class implements the Transaction interface and handles the core transaction data
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

    /**
     * Default constructor required for JSON deserialization.
     * Creates an empty transaction that must be populated via setters.
     */
    public FinancialTransaction() {}

    /**
     * Creates a new financial transaction with the specified sender, receiver and amount.
     * Automatically generates a transaction ID.
     *
     * @param sender The address of the sending party
     * @param receiver The address of the receiving party
     * @param amount The amount to transfer
     */
    public FinancialTransaction(String sender, String receiver, double amount) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.transactionId = generateTransactionId();
    }
    
    /**
     * Creates a new financial transaction with a specified transaction ID.
     * Primarily used for testing or special cases where ID needs to be controlled.
     *
     * @param sender The address of the sending party
     * @param receiver The address of the receiving party
     * @param amount The amount to transfer
     * @param transactionId The specific transaction ID to use, or null to generate one
     */
    public FinancialTransaction(String sender, String receiver, double amount, String transactionId) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.transactionId = transactionId != null ? transactionId : generateTransactionId();
    }
    
    /**
     * Generates a unique transaction ID based on the transaction details and timestamp.
     * Uses UUID.nameUUIDFromBytes to create a deterministic but unique identifier.
     *
     * @return A string representation of the generated UUID
     */
    private String generateTransactionId() {
        // Create a deterministic ID based on transaction data + random component
        String baseData = sender + receiver + amount + System.currentTimeMillis();
        return UUID.nameUUIDFromBytes(baseData.getBytes()).toString();
    }
    
    /**
     * @return The unique identifier for this transaction
     */
    @Override
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Validates the transaction by checking if sender, receiver are set and amount is positive.
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
}
