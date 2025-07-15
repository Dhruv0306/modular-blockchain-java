package com.example.blockchain.examples;

import com.example.blockchain.blockchain.Block;
import com.example.blockchain.blockchain.Blockchain;
import com.example.blockchain.blockchain.BlockchainConfig;
import com.example.blockchain.blockchain.CustomGenesisBlockFactory;
import com.example.blockchain.consensus.Consensus;
import com.example.blockchain.consensus.ProofOfWork;
import com.example.blockchain.transactions.FinancialTransaction;
import com.example.blockchain.logging.BlockchainLoggerFactory;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Demonstration runner for the modular blockchain examples.
 * 
 * This class demonstrates:
 * 1. Creating a blockchain with default genesis block
 * 2. Creating a blockchain with custom genesis block containing initial transactions
 * 3. Adding and mining new blocks with transactions
 */
public class DemoRunner {
    private static final Logger logger = BlockchainLoggerFactory.getLogger(DemoRunner.class);

    /**
     * Runs the default blockchain example with automatic genesis block
     */
    public void runDefaultBlockchainExample(BlockchainConfig config) {
        logger.info("Example 1: Default blockchain with automatic genesis block");
        Blockchain<FinancialTransaction> defaultBlockchain = new Blockchain<>();
        runBlockchainExample(defaultBlockchain, config);
    }

    /**
     * Runs the blockchain example with a custom genesis block
     */
    public void runCustomGenesisBlockchainExample(BlockchainConfig config) {
        logger.info("Example 2: Blockchain with custom genesis block");

        // Create initial genesis transactions
        List<FinancialTransaction> genesisTransactions = new ArrayList<>();
        genesisTransactions.add(new FinancialTransaction("Genesis", "Alice", 1000));
        genesisTransactions.add(new FinancialTransaction("Genesis", "Bob", 1000));

        // Create custom genesis block factory
        CustomGenesisBlockFactory<FinancialTransaction> customFactory = CustomGenesisBlockFactory
                .<FinancialTransaction>builder()
                .withHash("CUSTOM_GENESIS_HASH_WITH_INITIAL_FUNDS")
                .withTransactions(genesisTransactions)
                .withMetadata("creator", "Satoshi")
                .withMetadata("version", "1.0")
                .build();

        // Create blockchain with custom genesis
        Blockchain<FinancialTransaction> customBlockchain = new Blockchain<>(customFactory);
        runBlockchainExample(customBlockchain, config);
    }

    /**
     * Helper method to run the blockchain example with transactions
     */
    private void runBlockchainExample(
            Blockchain<FinancialTransaction> blockchain,
            BlockchainConfig config) {

        Consensus<FinancialTransaction> consensus = new ProofOfWork<>();

        // Show the genesis block first
        logger.info("\nGenesis block:");
        Block<FinancialTransaction> genesisBlock = blockchain.getChain().get(0);
        logger.info("Block #" + genesisBlock.getIndex() + " | Hash: " + genesisBlock.getHash());
        for (FinancialTransaction tx : genesisBlock.getTransactions()) {
            logger.info("  - " + tx);
        }

        // Add regular transactions
        blockchain.addTransaction(new FinancialTransaction("Alice", "Bob", 100));
        blockchain.addTransaction(new FinancialTransaction("Charlie", "Dave", 75));

        logger.info("\nMining block... (difficulty=" + config.getDifficulty() + ")");
        long startTime = System.currentTimeMillis();

        Block<FinancialTransaction> newBlock = consensus.generateBlock(
                blockchain.getPendingTransactions(),
                blockchain.getLastBlock());

        long endTime = System.currentTimeMillis();
        logger.info("Block mined in " + (endTime - startTime) + "ms");

        if (consensus.validateBlock(newBlock, blockchain.getLastBlock())) {
            blockchain.addBlock(newBlock);
            logger.info("✅ Block added to chain");
        }

        logger.info("\nFinal blockchain state:");
        for (Block<FinancialTransaction> block : blockchain.getChain()) {
            logger.info("Block #" + block.getIndex() + " | Hash: " + block.getHash());
            for (FinancialTransaction tx : block.getTransactions()) {
                logger.info("  - " + tx);
            }
        }
    }
}