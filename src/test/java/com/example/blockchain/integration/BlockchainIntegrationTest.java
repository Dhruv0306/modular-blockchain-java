package com.example.blockchain.integration;

import com.example.blockchain.consensus.ProofOfWork;
import com.example.blockchain.core.chain.Blockchain;
import com.example.blockchain.core.config.CustomGenesisBlockFactory;
import com.example.blockchain.core.model.Block;
import com.example.blockchain.transactions.FinancialTransaction;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test simulating end-to-end blockchain usage as demonstrated in
 * Main.java
 */
class BlockchainIntegrationTest {

    /**
     * Test a valid blockchain workflow with the default genesis block
     */
    @Test
    void testDefaultBlockchainWorkflow() {
        // Create blockchain with default genesis
        ProofOfWork<FinancialTransaction> consensus = new ProofOfWork<>();
        Blockchain<FinancialTransaction> blockchain = new Blockchain<>();

        // Verify genesis block exists
        assertEquals(1, blockchain.getChain().size());
        assertEquals(0, blockchain.getChain().get(0).getIndex());

        // Chain with just genesis block should be valid
        assertTrue(blockchain.isChainValid(), "Genesis block chain should be valid");

        // Add transactions
        blockchain.addTransaction(new FinancialTransaction("Alice", "Bob", 100));
        blockchain.addTransaction(new FinancialTransaction("Charlie", "Dave", 75));

        // Create a new block
        Block<FinancialTransaction> newBlock = consensus.generateBlock(
                blockchain.getPendingTransactions(),
                blockchain.getLastBlock());

        // Verify the new block is valid according to the consensus rules
        assertTrue(consensus.validateBlock(newBlock, blockchain.getLastBlock()));

        // Add the block to the chain
        blockchain.addBlock(newBlock);

        // Verify final state
        assertEquals(2, blockchain.getChain().size());
        assertEquals(1, newBlock.getIndex());
        assertTrue(blockchain.getPendingTransactions().isEmpty());
    }

    /**
     * Test with a custom genesis block
     */
    @Test
    void testCustomGenesisBlockchainWorkflow() {
        // Create custom genesis with initial transactions
        CustomGenesisBlockFactory<FinancialTransaction> factory = CustomGenesisBlockFactory
                .<FinancialTransaction>builder()
                .withHash("CUSTOM_GENESIS")
                .addTransaction(new FinancialTransaction("Genesis", "Alice", 1000))
                .addTransaction(new FinancialTransaction("Genesis", "Bob", 1000))
                .build();

        ProofOfWork<FinancialTransaction> consensus = new ProofOfWork<>();
        Blockchain<FinancialTransaction> blockchain = new Blockchain<>(factory, consensus);

        // Verify custom genesis block
        Block<FinancialTransaction> genesis = blockchain.getChain().get(0);
        assertEquals(2, genesis.getTransactions().size());
        assertEquals("0", genesis.getPreviousHash());

        // Chain with just custom genesis block should be valid
        assertTrue(blockchain.isChainValid(), "Custom genesis blockchain should be valid");

        // Add and mine new transactions
        blockchain.addTransaction(new FinancialTransaction("Alice", "Bob", 100));
        Block<FinancialTransaction> newBlock = consensus.generateBlock(
                blockchain.getPendingTransactions(),
                blockchain.getLastBlock());

        assertTrue(consensus.validateBlock(newBlock, blockchain.getLastBlock()));
        blockchain.addBlock(newBlock);

        assertEquals(2, blockchain.getChain().size());
        assertEquals(1, newBlock.getIndex());
    }

    /**
     * Test detection of transaction tampering after block creation
     */
    @Test
    void testDetectTransactionTampering() {
        // Create blockchain with direct consensus access
        ProofOfWork<FinancialTransaction> consensus = new ProofOfWork<>();
        Blockchain<FinancialTransaction> blockchain = new Blockchain<>();

        // Create a transaction and a valid block
        FinancialTransaction validTx = new FinancialTransaction("Alice", "Bob", 100);

        // Get the genesis block
        Block<FinancialTransaction> genesisBlock = blockchain.getLastBlock();

        // Create a list with the valid transaction
        List<FinancialTransaction> validTxs = new ArrayList<>();
        validTxs.add(validTx);

        // Generate a valid block with our transaction
        Block<FinancialTransaction> validBlock = consensus.generateBlock(validTxs, genesisBlock);

        // Verify it's valid
        assertTrue(consensus.validateBlock(validBlock, genesisBlock));

        // Now create a tampered transaction with a different amount
        FinancialTransaction tamperedTx = new FinancialTransaction("Alice", "Bob", 1000); // 1000 instead of 100

        // Create a new block with the tampered transaction but keep all other block
        // properties the same
        List<FinancialTransaction> tamperedTxs = new ArrayList<>();
        tamperedTxs.add(tamperedTx);

        Block<FinancialTransaction> tamperedBlock = new Block<>(
                validBlock.getIndex(),
                validBlock.getPreviousHash(),
                validBlock.getTimestamp(),
                tamperedTxs,
                validBlock.getNonce(),
                validBlock.getHash() // Using the valid block's hash, which is incorrect for the tampered content
        );

        // This should be detected as invalid
        assertFalse(consensus.validateBlock(tamperedBlock, genesisBlock),
                "Block with tampered transactions should be detected");
    }

    /**
     * Test detection of non-sequential block indices
     */
    @Test
    void testDetectNonSequentialBlockIndex() {
        Blockchain<FinancialTransaction> blockchain = new Blockchain<>();

        // Chain with just genesis block should be valid
        assertTrue(blockchain.isChainValid(), "Genesis blockchain should be valid");

        // Create an invalid block with a non-sequential index (skip from 0 to 2)
        List<FinancialTransaction> txs = new ArrayList<>();
        txs.add(new FinancialTransaction("Eve", "Frank", 300));

        // Calculate a valid hash but with wrong index
        String validHash = "0000" + System.currentTimeMillis(); // Just ensure it starts with enough zeros

        // Create a block with index 2 (should be 1 after genesis)
        Block<FinancialTransaction> nonSequentialBlock = new Block<>(
                2, // Should be 1 after genesis block
                blockchain.getLastBlock().getHash(),
                System.currentTimeMillis(),
                txs,
                0,
                validHash);

        // Add the invalid block
        blockchain.addBlock(nonSequentialBlock);

        // Chain validation should fail due to non-sequential index
        assertFalse(blockchain.isChainValid(), "Chain should be invalid with non-sequential indices");
    }

    /**
     * Test detection of invalid previous hash references
     */
    @Test
    void testDetectInvalidPreviousHashReference() {
        // Create blockchain
        ProofOfWork<FinancialTransaction> consensus = new ProofOfWork<>();
        Blockchain<FinancialTransaction> blockchain = new Blockchain<>();

        // Add a valid block first to establish the chain
        blockchain.addTransaction(new FinancialTransaction("Alice", "Bob", 100));
        Block<FinancialTransaction> validBlock = consensus.generateBlock(
                blockchain.getPendingTransactions(),
                blockchain.getLastBlock());
        blockchain.addBlock(validBlock);

        // Create a block with an invalid previous hash reference
        List<FinancialTransaction> txs = new ArrayList<>();
        txs.add(new FinancialTransaction("Eve", "Frank", 200));

        // Calculate a valid hash but with wrong previous hash
        String validHash = "0000" + System.currentTimeMillis(); // Just ensure it starts with enough zeros

        // Create a block with an invalid previous hash
        Block<FinancialTransaction> invalidPrevHashBlock = new Block<>(
                2, // Correct sequential index
                "FAKE_PREVIOUS_HASH", // Invalid previous hash
                System.currentTimeMillis(),
                txs,
                0,
                validHash);

        // Add the invalid block
        blockchain.addBlock(invalidPrevHashBlock);

        // Chain validation should fail due to incorrect previous hash
        assertFalse(blockchain.isChainValid(), "Chain should be invalid with incorrect previous hash");
    }
}