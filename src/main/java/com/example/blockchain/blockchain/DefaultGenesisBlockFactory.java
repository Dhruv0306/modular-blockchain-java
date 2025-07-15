package com.example.blockchain.blockchain;

import java.util.ArrayList;

/**
 * Default implementation of GenesisBlockFactory that creates a basic genesis
 * block
 * with no transactions and a configurable hash.
 *
 * @param <T> The transaction type stored in the blockchain
 */
public class DefaultGenesisBlockFactory<T extends Transaction> implements GenesisBlockFactory<T> {

    @Override
    public Block<T> createGenesisBlock() {
        String genesisHash = BlockchainConfig.getInstance().getGenesisHash();
        return new Block<>(0, "0", System.currentTimeMillis(), new ArrayList<>(), 0, genesisHash);
    }
}