package com.example.blockchain.transactions;

import java.util.Objects;
import java.util.UUID;

import com.example.blockchain.core.model.Transaction;

public class FinancialTransaction implements Transaction {
    private final String sender;
    private final String receiver;
    private final double amount;
    private final String transactionId;

    public FinancialTransaction(String sender, String receiver, double amount) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.transactionId = generateTransactionId();
    }
    
    /**
     * Constructor that allows specifying a transaction ID (for testing or special cases)
     */
    public FinancialTransaction(String sender, String receiver, double amount, String transactionId) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.transactionId = transactionId != null ? transactionId : generateTransactionId();
    }
    
    private String generateTransactionId() {
        // Create a deterministic ID based on transaction data + random component
        String baseData = sender + receiver + amount + System.currentTimeMillis();
        return UUID.nameUUIDFromBytes(baseData.getBytes()).toString();
    }
    
    @Override
    public String getTransactionId() {
        return transactionId;
    }

    @Override
    public boolean isValid() {
        return sender != null && receiver != null && amount > 0;
    }

    @Override
    public String getSender() {
        return sender;
    }

    @Override
    public String getReceiver() {
        return receiver;
    }

    @Override
    public String getSummary() {
        return sender + " -> " + receiver + " : $" + amount;
    }

    public double getAmount() {
        return amount;
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

        FinancialTransaction that = (FinancialTransaction) o;

        return Double.compare(that.amount, amount) == 0 &&
                Objects.equals(sender, that.sender) &&
                Objects.equals(receiver, that.receiver) &&
                Objects.equals(transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, receiver, amount, transactionId);
    }
}
