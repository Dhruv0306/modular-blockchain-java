package com.example.blockchain.blockchain;

public interface Transaction {
    boolean isValid();

    String getSender();

    String getReceiver();

    String getSummary();
}
