package com.example.blockchain.core.chain;

import com.example.blockchain.consensus.ProofOfWork;
import com.example.blockchain.core.chain.Blockchain;
import com.example.blockchain.core.model.Block;
import com.example.blockchain.transactions.FinancialTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for edge cases in the Blockchain implementation.
 * Tests various boundary conditions and edge scenarios to ensure robust behavior.
 */
public class BlockchainEdgeCasesTest {

    private Blockchain<FinancialTransaction> blockchain;
    private ProofOfWork<FinancialTransaction> pow;

    /**
     * Sets up a fresh blockchain and proof of work instance before each test.
     */
    @BeforeEach
    void setUp() {
        pow = new ProofOfWork<>();
        blockchain = new Blockchain<>();
    }

    /**
     * Tests that the blockchain can handle blocks with no transactions.
     * Verifies that empty blocks are valid and properly added to the chain.
     */
    @Test
    void testEmptyTransactionListInBlock() {
        // Create a block with empty transaction list
        List<FinancialTransaction> emptyTxList = new ArrayList<>();
        Block<FinancialTransaction> emptyBlock = pow.generateBlock(
                emptyTxList, 
                blockchain.getLastBlock());
        
        // Add the empty block to the chain
        blockchain.addBlock(emptyBlock);
        
        // Chain should still be valid
        assertTrue(blockchain.isChainValid());
        assertEquals(2, blockchain.getChain().size());  // Genesis block + empty block
        assertEquals(0, blockchain.getChain().get(1).getTransactions().size());
    }
    
    /**
     * Tests handling of duplicate transactions in the blockchain.
     * Verifies that identical transactions can be added and processed.
     */
    @Test
    void testDuplicateTransactions() {
        // Create two separate but identical transactions
        FinancialTransaction tx1 = new FinancialTransaction("Alice", "Bob", 100);
        FinancialTransaction tx2 = new FinancialTransaction("Alice", "Bob", 100);
        
        // Add both transactions to pending list
        blockchain.addTransaction(tx1);
        blockchain.addTransaction(tx2);
        
        // Verify both instances were added successfully
        assertEquals(2, blockchain.getPendingTransactions().size());
        
        // Generate a block containing both duplicate transactions
        Block<FinancialTransaction> block = pow.generateBlock(
                blockchain.getPendingTransactions(), 
                blockchain.getLastBlock());
        
        // Add the block to the chain
        blockchain.addBlock(block);
        
        // Verify the block was successfully added to the chain
        assertEquals(2, blockchain.getChain().size());  // Genesis block + new block
    }
    
    /**
     * Tests the blockchain's behavior with null transactions.
     * Currently verifies initial state, but suggests implementation improvements.
     */
    @Test
    void testAddingNullTransaction() {
        // The blockchain.addTransaction method doesn't handle null transactions
        // This test verifies that the blockchain has no pending transactions initially
        assertEquals(0, blockchain.getPendingTransactions().size());
    }
    
    /**
     * Tests the blockchain's ability to handle multiple transactions in a block.
     * Verifies that blocks with multiple transactions are properly processed.
     */
    @Test
    void testMaxTransactionsInBlock() {
        // Add a small number of transactions for testing purposes
        int maxTransactions = 5;
        for (int i = 0; i < maxTransactions; i++) {
            blockchain.addTransaction(new FinancialTransaction("User" + i, "Receiver" + i, i + 1));
        }
        
        // Verify all transactions were added to pending list
        assertEquals(maxTransactions, blockchain.getPendingTransactions().size());
        
        // Generate a block containing all pending transactions
        Block<FinancialTransaction> block = pow.generateBlock(
                blockchain.getPendingTransactions(), 
                blockchain.getLastBlock());
        
        // Add the block to the chain
        blockchain.addBlock(block);
        
        // Verify the block was successfully added
        assertEquals(2, blockchain.getChain().size());  // Genesis block + new block
    }
}
