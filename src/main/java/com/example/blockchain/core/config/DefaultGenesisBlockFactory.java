package com.example.blockchain.core.config;

import java.util.ArrayList;

import com.example.blockchain.core.model.Block;
import com.example.blockchain.core.model.Transaction;

/**
 * Default implementation of GenesisBlockFactory that creates a basic genesis block
 * with no transactions and a configurable hash.
 *
 * This factory creates the first block (genesis block) of the blockchain with:
 * - Block index 0
 * - Previous hash "0" 
 * - Current timestamp
 * - Empty transaction list
 * - Nonce value 0
 * - Configurable genesis hash from ChainConfig
 *
 * @param <T> The transaction type stored in the blockchain
 */
public class DefaultGenesisBlockFactory<T extends Transaction> implements GenesisBlockFactory<T> {

    /**
     * Creates and returns the genesis block for the blockchain
     *
     * @return Block<T> The genesis block with default values and empty transaction list
     */
    @Override
    public Block<T> createGenesisBlock() {
        // Get configured genesis hash from chain config
        String genesisHash = ChainConfig.getInstance().getGenesisHash();
        
        // Create new genesis block with default values
        return new Block<>(
            0,              // Block index
            "0",           // Previous hash
            System.currentTimeMillis(), // Current timestamp
            new ArrayList<>(), // Empty transaction list
            0,              // Nonce
            genesisHash    // Genesis hash from config
        );
    }
}
