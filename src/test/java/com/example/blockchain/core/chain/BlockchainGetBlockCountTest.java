package com.example.blockchain.core.chain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.example.blockchain.core.config.DefaultGenesisBlockFactory;
import com.example.blockchain.core.model.Block;
import com.example.blockchain.transactions.FinancialTransaction;

import java.util.ArrayList;

/**
 * Tests for the getBlockCount method in the Blockchain class.
 */
public class BlockchainGetBlockCountTest {

    @Test
    public void testGetBlockCountWithOnlyGenesisBlock() {
        // Create a new blockchain with just the genesis block
        Blockchain<FinancialTransaction> blockchain = new Blockchain<>();
        
        // Verify that the block count is 1 (genesis block)
        assertEquals(1, blockchain.getBlockCount());
    }
    
    @Test
    public void testGetBlockCountWithMultipleBlocks() {
        // Create a new blockchain
        Blockchain<FinancialTransaction> blockchain = new Blockchain<>();
        
        // Add some blocks to the chain
        Block<FinancialTransaction> genesisBlock = blockchain.getLastBlock();
        
        // Create and add 3 more blocks
        for (int i = 1; i <= 3; i++) {
            Block<FinancialTransaction> newBlock = new Block<>(
                i,                          // index
                genesisBlock.getHash(),     // previousHash
                System.currentTimeMillis(), // timestamp
                new ArrayList<>(),          // empty transactions
                0,                          // nonce
                "hash" + i                  // hash
            );
            blockchain.addBlock(newBlock);
        }
        
        // Verify that the block count is 4 (genesis block + 3 added blocks)
        assertEquals(4, blockchain.getBlockCount());
    }
    
    @Test
    public void testGetBlockCountAfterAddingAndRemovingBlocks() {
        // Create a new blockchain with a custom genesis block factory
        Blockchain<FinancialTransaction> blockchain = 
            new Blockchain<>(new DefaultGenesisBlockFactory<>());
        
        // Verify initial count is 1
        assertEquals(1, blockchain.getBlockCount());
        
        // Add a block
        Block<FinancialTransaction> newBlock = new Block<>(
            1,                                  // index
            blockchain.getLastBlock().getHash(),// previousHash
            System.currentTimeMillis(),         // timestamp
            new ArrayList<>(),                  // empty transactions
            0,                                  // nonce
            "hash1"                             // hash
        );
        blockchain.addBlock(newBlock);
        
        // Verify count is now 2
        assertEquals(2, blockchain.getBlockCount());
        
        // Get direct access to the chain and remove a block
        // (This is for testing purposes only - not recommended in real code)
        blockchain.getChain().remove(1);
        
        // Verify count is back to 1
        assertEquals(1, blockchain.getBlockCount());
    }
}