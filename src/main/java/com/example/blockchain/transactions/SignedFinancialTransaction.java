package com.example.blockchain.transactions;

import java.security.PublicKey;
import java.util.Objects;
import java.util.UUID;

import com.example.blockchain.blockchain.SignedTransaction;
import com.example.blockchain.crypto.CryptoUtils;

public class SignedFinancialTransaction implements SignedTransaction {
    private final String sender;
    private final String receiver;
    private final double amount;
    private final PublicKey senderPublicKey;
    private final String signature;
    private final String transactionId;
    private final long timestamp;

    public SignedFinancialTransaction(String sender, String receiver, double amount,
            PublicKey senderPublicKey, String signature) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.senderPublicKey = senderPublicKey;
        this.signature = signature;
        this.timestamp = System.currentTimeMillis();
        this.transactionId = generateTransactionId();
    }
    
    /**
     * Constructor that allows specifying a transaction ID and timestamp (for testing or special cases)
     */
    public SignedFinancialTransaction(String sender, String receiver, double amount,
            PublicKey senderPublicKey, String signature, String transactionId) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.senderPublicKey = senderPublicKey;
        this.signature = signature;
        this.timestamp = System.currentTimeMillis();
        this.transactionId = transactionId != null ? transactionId : generateTransactionId();
    }
    
    /**
     * Constructor that allows specifying a transaction ID and timestamp (for testing or special cases)
     */
    public SignedFinancialTransaction(String sender, String receiver, double amount,
            PublicKey senderPublicKey, String signature, String transactionId, long timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.senderPublicKey = senderPublicKey;
        this.signature = signature;
        this.timestamp = timestamp;
        this.transactionId = transactionId != null ? transactionId : generateTransactionId();
    }
    
    private String generateTransactionId() {
        // Create a deterministic ID based on transaction data + signature + timestamp
        String baseData = sender + receiver + amount + signature + timestamp;
        return UUID.nameUUIDFromBytes(baseData.getBytes()).toString();
    }
    
    @Override
    public String getTransactionId() {
        return transactionId;
    }

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

    @Override
    public String getSummary() {
        return sender + " -> " + receiver + " : $" + amount + " (time: " + timestamp + ")";
    }

    @Override
    public String getSignature() {
        return signature;
    }

    @Override
    public PublicKey getSenderPublicKey() {
        return senderPublicKey;
    }

    @Override
    public boolean verifySignature() {
        try {
            String data = getSummary(); // must match the signed input
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
}
