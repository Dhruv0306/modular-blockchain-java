package com.example.blockchain.examples;

import com.example.blockchain.consensus.Consensus;
import com.example.blockchain.consensus.ProofOfWork;
import com.example.blockchain.core.chain.Blockchain;
import com.example.blockchain.core.config.ChainConfig;
import com.example.blockchain.core.config.CustomGenesisBlockFactory;
import com.example.blockchain.core.model.Block;
import com.example.blockchain.transactions.FinancialTransaction;
import com.example.blockchain.logging.BlockchainLoggerFactory;

import org.slf4j.Logger;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Demonstration runner for the modular blockchain examples.
 * 
 * This class provides examples of different ways to initialize and use a
 * blockchain:
 * 1. Creating a blockchain with default genesis block
 * 2. Creating a blockchain with custom genesis block containing initial
 * transactions
 * 3. Adding and mining new blocks with transactions
 * 
 * The examples demonstrate core blockchain functionality including:
 * - Genesis block creation
 * - Transaction handling
 * - Block mining with proof of work
 * - Block validation and chain addition
 */
public class DemoRunner {
    // Logger instance for this class
    private static final Logger logger = BlockchainLoggerFactory.getLogger(DemoRunner.class);

    /**
     * Runs the default blockchain example with automatic genesis block.
     * Creates a new blockchain with default settings and no initial transactions.
     *
     * @param config The chain configuration parameters
     */
    public void runDefaultBlockchainExample(ChainConfig config) {
        logger.info("Example 1: Default blockchain with automatic genesis block");
        Blockchain<FinancialTransaction> defaultBlockchain = new Blockchain<>();
        runBlockchainExample(defaultBlockchain, config);
    }

    /**
     * Runs the blockchain example with a custom genesis block.
     * Demonstrates how to initialize a blockchain with predefined initial state.
     *
     * @param config The chain configuration parameters
     */
    public void runCustomGenesisBlockchainExample(ChainConfig config) {
        logger.info("Example 2: Blockchain with custom genesis block");

        // Create initial genesis transactions - allocate initial funds
        List<FinancialTransaction> genesisTransactions = new ArrayList<>();
        try {
            genesisTransactions.add(new FinancialTransaction("Genesis", "Alice", 1000, "U000", "U124"));
            genesisTransactions.add(new FinancialTransaction("Genesis", "Bob", 1000, "U000", "U123"));
        } catch (NoSuchAlgorithmException e) {
            String error = "Error Creating Transection. \nError: " + e.getMessage();
            logger.error(error, e);
            throw new RuntimeException(error, e);
        }

        // Create custom genesis block factory with initial configuration
        CustomGenesisBlockFactory<FinancialTransaction> customFactory = CustomGenesisBlockFactory
                .<FinancialTransaction>builder()
                .withHash("CUSTOM_GENESIS_HASH_WITH_INITIAL_FUNDS")
                .withTransactions(genesisTransactions)
                .withMetadata("creator", "Satoshi")
                .withMetadata("version", "1.0")
                .build();

        // Initialize blockchain with the custom genesis block
        Blockchain<FinancialTransaction> customBlockchain = new Blockchain<>(customFactory);
        runBlockchainExample(customBlockchain, config);
    }

    /**
     * Runs the blockchain example using an existing blockchain instance.
     * Useful for continuing operations on a persisted blockchain.
     *
     * @param blockchain The existing blockchain instance to use
     * @param config     The chain configuration parameters
     */
    public void runWithExistingBlockchain(Blockchain<FinancialTransaction> blockchain, ChainConfig config) {
        logger.info("Example 3: Running with existing blockchain loaded from persistence");
        runBlockchainExample(blockchain, config);
    }

    /**
     * Helper method to run the blockchain example with transactions.
     * Demonstrates the core workflow of:
     * 1. Displaying genesis block
     * 2. Adding new transactions
     * 3. Mining a new block
     * 4. Validating and adding the block to chain
     * 5. Displaying final chain state
     *
     * @param blockchain The blockchain instance to operate on
     * @param config     The chain configuration parameters
     */
    private void runBlockchainExample(
            Blockchain<FinancialTransaction> blockchain,
            ChainConfig config) {

        // Initialize consensus mechanism (Proof of Work)
        Consensus<FinancialTransaction> consensus = new ProofOfWork<>();
        
        // Call the overloaded method with the consensus
        runBlockchainExample(blockchain, config, consensus);
    }
    
    /**
     * Overloaded helper method that accepts a custom consensus algorithm.
     * This allows for testing with different consensus implementations.
     *
     * @param blockchain The blockchain instance to operate on
     * @param config     The chain configuration parameters
     * @param consensus  The consensus algorithm to use
     */
    private void runBlockchainExample(
            Blockchain<FinancialTransaction> blockchain,
            ChainConfig config,
            Consensus<FinancialTransaction> consensus) {

        // Display the genesis block information
        logger.info("\nGenesis block:");
        Block<FinancialTransaction> genesisBlock = blockchain.getChain().get(0);
        logger.info("Block #" + genesisBlock.getIndex() + " | Hash: " + genesisBlock.getHash());
        for (FinancialTransaction tx : genesisBlock.getTransactions()) {
            logger.info("  - " + tx);
        }

        // Add sample transactions to pending pool
        try {
            blockchain.addTransaction(new FinancialTransaction("Alice", "Bob", 100, "U124", "U123"));
            blockchain.addTransaction(new FinancialTransaction("Charlie", "Dave", 75, "U125", "U126"));
        } catch (NoSuchAlgorithmException e) {
            String error = "Error Creating Transection. \nError: " + e.getMessage();
            logger.error(error, e);
            throw new RuntimeException(error, e);
        }

        // Mine a new block with pending transactions
        logger.info("\nMining block... (difficulty=" + config.getDifficulty() + ")");
        long startTime = System.currentTimeMillis();

        Block<FinancialTransaction> newBlock = consensus.generateBlock(
                blockchain.getPendingTransactions(),
                blockchain.getLastBlock());

        long endTime = System.currentTimeMillis();
        logger.info("Block mined in " + (endTime - startTime) + "ms");

        // Validate and add the new block to chain
        if (consensus.validateBlock(newBlock, blockchain.getLastBlock())) {
            blockchain.addBlock(newBlock);
            logger.info("Block added to chain");
        }

        // Display the final state of the blockchain
        logger.info("\nFinal blockchain state:");
        for (Block<FinancialTransaction> block : blockchain.getChain()) {
            logger.info("Block #" + block.getIndex() + " | Hash: " + block.getHash());
            for (FinancialTransaction tx : block.getTransactions()) {
                logger.info("  - " + tx);
            }
        }
    }
}
