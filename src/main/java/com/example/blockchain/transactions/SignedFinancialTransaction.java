package com.example.blockchain.transactions;

import java.security.PublicKey;

import com.example.blockchain.blockchain.SignedTransaction;
import com.example.blockchain.crypto.CryptoUtils;

public class SignedFinancialTransaction implements SignedTransaction {
    private final String sender;
    private final String receiver;
    private final double amount;
    private final PublicKey senderPublicKey;
    private final String signature;

    public SignedFinancialTransaction(String sender, String receiver, double amount,
            PublicKey senderPublicKey, String signature) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.senderPublicKey = senderPublicKey;
        this.signature = signature;
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
        return sender + " -> " + receiver + " : $" + amount;
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

    @Override
    public String toString() {
        return getSummary();
    }
}
