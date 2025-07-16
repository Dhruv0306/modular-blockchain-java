package com.example.blockchain.consensus;

import java.util.List;

import com.example.blockchain.core.model.Block;
import com.example.blockchain.core.model.Transaction;

/**
 * Interface defining consensus rules for validating and generating blocks in a blockchain
 * @param <T> Type parameter extending Transaction to allow for different transaction types
 */
public interface Consensus<T extends Transaction> {
    /**
     * Validates a new block according to consensus rules
     * @param newBlock The new block to validate
     * @param previousBlock The previous block in the chain to validate against
     * @return true if the block is valid according to consensus rules, false otherwise
     */
    boolean validateBlock(Block<T> newBlock, Block<T> previousBlock);

    /**
     * Generates a new block containing the given transactions
     * @param txs List of transactions to include in the new block
     * @param previousBlock The previous block to build upon
     * @return A new valid block containing the transactions
     */
    Block<T> generateBlock(List<T> txs, Block<T> previousBlock);
}
