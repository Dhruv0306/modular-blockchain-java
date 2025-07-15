package com.example.blockchain.blockchain;

import java.util.ArrayList;
import java.util.List;

public class Blockchain<T extends Transaction> {
    private final List<Block<T>> chain = new ArrayList<>();
    private final List<T> pendingTransactions = new ArrayList<>();

    /**
     * Creates a new blockchain with a default genesis block.
     */
    public Blockchain() {
        this(new DefaultGenesisBlockFactory<>());
    }

    /**
     * Creates a new blockchain with a custom genesis block factory.
     *
     * @param genesisBlockFactory The factory to create the genesis block
     */
    public Blockchain(GenesisBlockFactory<T> genesisBlockFactory) {
        chain.add(genesisBlockFactory.createGenesisBlock());
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
