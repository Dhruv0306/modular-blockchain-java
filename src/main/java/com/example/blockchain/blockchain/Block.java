package com.example.blockchain.blockchain;

import java.util.List;

public class Block<T extends Transaction> {
    private int index;
    private String previousHash;
    private long timestamp;
    private List<T> transactions;
    private int nonce;
    private String hash;

    public Block(int index, String previousHash, long timestamp, List<T> transactions, int nonce, String hash) {
        this.index = index;
        this.previousHash = previousHash;
        this.timestamp = timestamp;
        this.transactions = transactions;
        this.nonce = nonce;
        this.hash = hash;
    }

    public int getIndex() {
        return index;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public List<T> getTransactions() {
        return transactions;
    }

    public int getNonce() {
        return nonce;
    }

    public String getHash() {
        return hash;
    }
}
