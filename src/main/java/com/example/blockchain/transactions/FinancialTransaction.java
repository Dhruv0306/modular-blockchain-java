package com.example.blockchain.transactions;

import com.example.blockchain.blockchain.Transaction;

public class FinancialTransaction implements Transaction {
    private final String sender;
    private final String receiver;
    private final double amount;

    public FinancialTransaction(String sender, String receiver, double amount) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
    }

    public boolean isValid() {
        return sender != null && receiver != null && amount > 0;
    }

    public String getSender() { return sender; }
    public String getReceiver() { return receiver; }
    public String getSummary() {
        return sender + " -> " + receiver + " : $" + amount;
    }

    public String toString() { return getSummary(); }
}
