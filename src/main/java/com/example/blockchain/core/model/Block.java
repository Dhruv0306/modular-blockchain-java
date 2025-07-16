package com.example.blockchain.core.model;

import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Block<?> block = (Block<?>) o;

        return index == block.index &&
                timestamp == block.timestamp &&
                nonce == block.nonce &&
                Objects.equals(previousHash, block.previousHash) &&
                Objects.equals(hash, block.hash);
        // Note: We intentionally don't compare transactions directly
        // since their object identity might differ but content should be validated by
        // hash
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, previousHash, timestamp, nonce, hash);
        // Note: Similar to equals, we intentionally don't include transactions
    }
}
