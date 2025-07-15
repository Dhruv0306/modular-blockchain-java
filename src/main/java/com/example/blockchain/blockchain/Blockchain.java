package com.example.blockchain.blockchain;

import java.util.ArrayList;
import java.util.List;

public class Blockchain<T extends Transaction> {
    private final List<Block<T>> chain = new ArrayList<>();
    private final List<T> pendingTransactions = new ArrayList<>();

    public Blockchain() {
        chain.add(createGenesisBlock());
    }

    private Block<T> createGenesisBlock() {
        String genesisHash = BlockchainConfig.getInstance().getGenesisHash();
        return new Block<>(0, "0", System.currentTimeMillis(), new ArrayList<>(), 0, genesisHash);
    }

    public void addTransaction(T tx) {
        if (tx.isValid())
            pendingTransactions.add(tx);
    }

    public List<T> getPendingTransactions() {
        return pendingTransactions;
    }

    public Block<T> getLastBlock() {
        return chain.get(chain.size() - 1);
    }

    public void addBlock(Block<T> block) {
        chain.add(block);
        pendingTransactions.clear();
    }

    public List<Block<T>> getChain() {
        return chain;
    }
}
