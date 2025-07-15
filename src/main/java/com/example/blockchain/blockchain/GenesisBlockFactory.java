package com.example.blockchain.blockchain;

/**
 * Interface for creating genesis blocks.
 * This allows different strategies for initializing the blockchain
 * with custom genesis blocks.
 *
 * @param <T> The transaction type stored in the blockchain
 */
public interface GenesisBlockFactory<T extends Transaction> {
    
    /**
     * Creates a genesis block for a new blockchain.
     *
     * @return The genesis block
     */
    Block<T> createGenesisBlock();
} 