package com.example.blockchain;

import com.example.blockchain.blockchain.Block;
import com.example.blockchain.blockchain.Blockchain;
import com.example.blockchain.blockchain.BlockchainConfig;
import com.example.blockchain.blockchain.CustomGenesisBlockFactory;
import com.example.blockchain.consensus.Consensus;
import com.example.blockchain.consensus.ProofOfWork;
import com.example.blockchain.transactions.FinancialTransaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Main demonstration class for the modular blockchain.
 * 
 * This class demonstrates:
 * 1. Loading environment-specific configurations
 * 2. Creating a blockchain with default genesis block
 * 3. Creating a blockchain with custom genesis block containing initial transactions
 * 4. Adding and mining new blocks with transactions
 */
public class Main {
    public static void main(String[] args) {
        // Load the appropriate config based on environment or command line argument
        String configFile = "blockchain.properties"; // default
        
        // If args provided, use first arg as config file
        if (args.length > 0) {
            configFile = args[0];
        }
        
        // Check for BLOCKCHAIN_ENV environment variable
        String env = System.getenv("BLOCKCHAIN_ENV");
        if (env != null && !env.isEmpty()) {
            configFile = "blockchain-" + env + ".properties";
        }
        
        // Initialize config with the appropriate file
        BlockchainConfig config = BlockchainConfig.getInstance(configFile);
        
        // Output the current configuration
        System.out.println("Using configuration:");
        System.out.println("- Difficulty: " + config.getDifficulty());
        System.out.println("- Genesis hash: " + config.getGenesisHash());
        System.out.println();
        
        // EXAMPLE 1: Default blockchain with automatic genesis block
        System.out.println("Example 1: Default blockchain with automatic genesis block");
        Blockchain<FinancialTransaction> defaultBlockchain = new Blockchain<>();
        runBlockchainExample(defaultBlockchain, config);
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        // EXAMPLE 2: Blockchain with custom genesis block containing initial transactions
        System.out.println("Example 2: Blockchain with custom genesis block");
        
        // Create initial genesis transactions
        List<FinancialTransaction> genesisTransactions = new ArrayList<>();
        genesisTransactions.add(new FinancialTransaction("Genesis", "Alice", 1000));
        genesisTransactions.add(new FinancialTransaction("Genesis", "Bob", 1000));
        
        // Create custom genesis block factory
        CustomGenesisBlockFactory<FinancialTransaction> customFactory = 
            CustomGenesisBlockFactory.<FinancialTransaction>builder()
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
    private static void runBlockchainExample(
            Blockchain<FinancialTransaction> blockchain, 
            BlockchainConfig config) {
        
        Consensus<FinancialTransaction> consensus = new ProofOfWork<>();

        // Show the genesis block first
        System.out.println("\nGenesis block:");
        Block<FinancialTransaction> genesisBlock = blockchain.getChain().get(0);
        System.out.println("Block #" + genesisBlock.getIndex() + " | Hash: " + genesisBlock.getHash());
        for (FinancialTransaction tx : genesisBlock.getTransactions()) {
            System.out.println("  - " + tx);
        }
        
        // Add regular transactions
        blockchain.addTransaction(new FinancialTransaction("Alice", "Bob", 100));
        blockchain.addTransaction(new FinancialTransaction("Charlie", "Dave", 75));

        System.out.println("\nMining block... (difficulty=" + config.getDifficulty() + ")");
        long startTime = System.currentTimeMillis();
        
        Block<FinancialTransaction> newBlock = consensus.generateBlock(
                blockchain.getPendingTransactions(),
                blockchain.getLastBlock()
        );
        
        long endTime = System.currentTimeMillis();
        System.out.println("Block mined in " + (endTime - startTime) + "ms");

        if (consensus.validateBlock(newBlock, blockchain.getLastBlock())) {
            blockchain.addBlock(newBlock);
            System.out.println("✅ Block added to chain");
        }

        System.out.println("\nFinal blockchain state:");
        for (Block<FinancialTransaction> block : blockchain.getChain()) {
            System.out.println("Block #" + block.getIndex() + " | Hash: " + block.getHash());
            for (FinancialTransaction tx : block.getTransactions()) {
                System.out.println("  - " + tx);
            }
        }
    }
}
