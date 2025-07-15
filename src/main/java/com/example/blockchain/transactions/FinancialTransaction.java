package com.example.blockchain.transactions;

import com.example.blockchain.blockchain.Transaction;
import java.util.Objects;

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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        FinancialTransaction that = (FinancialTransaction) o;
        
        return Double.compare(that.amount, amount) == 0 &&
               Objects.equals(sender, that.sender) &&
               Objects.equals(receiver, that.receiver);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(sender, receiver, amount);
    }
}
