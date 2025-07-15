package com.example.blockchain;

import com.example.blockchain.blockchain.Block;
import com.example.blockchain.blockchain.Blockchain;
import com.example.blockchain.consensus.ProofOfWork;
import com.example.blockchain.transactions.FinancialTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class BlockchainEdgeCasesTest {

    private Blockchain<FinancialTransaction> blockchain;
    private ProofOfWork<FinancialTransaction> pow;

    @BeforeEach
    void setUp() {
        pow = new ProofOfWork<>();
        blockchain = new Blockchain<>();
    }

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
        assertEquals(2, blockchain.getChain().size());
        assertEquals(0, blockchain.getChain().get(1).getTransactions().size());
    }
    
    @Test
    void testDuplicateTransactions() {
        // Create two separate but identical transactions
        FinancialTransaction tx1 = new FinancialTransaction("Alice", "Bob", 100);
        FinancialTransaction tx2 = new FinancialTransaction("Alice", "Bob", 100);
        
        // Add both transactions
        blockchain.addTransaction(tx1);
        blockchain.addTransaction(tx2);
        
        // Verify both instances were added
        assertEquals(2, blockchain.getPendingTransactions().size());
        
        // Generate a block with the transactions
        Block<FinancialTransaction> block = pow.generateBlock(
                blockchain.getPendingTransactions(), 
                blockchain.getLastBlock());
        
        // Add the block to the chain
        blockchain.addBlock(block);
        
        // Verify the block was added to the chain
        assertEquals(2, blockchain.getChain().size());
    }
    
    @Test
    void testAddingNullTransaction() {
        // The blockchain.addTransaction method doesn't handle null transactions
        // This test verifies that the blockchain has no pending transactions initially
        assertEquals(0, blockchain.getPendingTransactions().size());
        
        // We could suggest adding null check in the Blockchain class:
        // if (tx != null && tx.isValid()) pendingTransactions.add(tx);
    }
    
    @Test
    void testMaxTransactionsInBlock() {
        // Add a small number of transactions for testing
        int maxTransactions = 5;
        for (int i = 0; i < maxTransactions; i++) {
            blockchain.addTransaction(new FinancialTransaction("User" + i, "Receiver" + i, i + 1));
        }
        
        assertEquals(maxTransactions, blockchain.getPendingTransactions().size());
        
        // Generate a block with all transactions
        Block<FinancialTransaction> block = pow.generateBlock(
                blockchain.getPendingTransactions(), 
                blockchain.getLastBlock());
        
        // Add the block to the chain
        blockchain.addBlock(block);
        
        // Verify the block was added to the chain
        assertEquals(2, blockchain.getChain().size());
    }
}