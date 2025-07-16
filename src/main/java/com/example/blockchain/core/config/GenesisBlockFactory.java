package com.example.blockchain.core.config;

import com.example.blockchain.core.model.Block;
import com.example.blockchain.core.model.Transaction;

/**
 * Interface for creating genesis blocks.
 * This allows different strategies for initializing the blockchain
 * with custom genesis blocks.
 * 
 * The genesis block is the first block in a blockchain and has special properties:
 * - It has no previous block hash
 * - It establishes the initial state of the blockchain
 * - Its contents are typically hardcoded or configured at blockchain creation
 *
 * Implementations of this interface should define how the genesis block is created
 * based on the specific blockchain requirements.
 *
 * @param <T> The transaction type stored in the blockchain. Must extend Transaction class.
 * @see Block
 * @see Transaction
 */
public interface GenesisBlockFactory<T extends Transaction> {

    /**
     * Creates a genesis block for a new blockchain.
     * 
     * The genesis block should be created with:
     * - A null or zero previous hash
     * - A timestamp of when it was created
     * - Any initial transactions required for blockchain setup
     * - A valid block hash based on its contents
     *
     * @return The genesis block instance that will be the first block in the chain
     * @throws IllegalStateException if the genesis block cannot be created
     */
    Block<T> createGenesisBlock();
}
