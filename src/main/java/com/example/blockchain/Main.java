package com.example.blockchain;

import com.example.blockchain.blockchain.Block;
import com.example.blockchain.blockchain.Blockchain;
import com.example.blockchain.blockchain.BlockchainConfig;
import com.example.blockchain.consensus.Consensus;
import com.example.blockchain.consensus.ProofOfWork;
import com.example.blockchain.transactions.FinancialTransaction;

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
        
        // Initialize blockchain with configured settings
        Blockchain<FinancialTransaction> blockchain = new Blockchain<>();
        Consensus<FinancialTransaction> consensus = new ProofOfWork<>();

        blockchain.addTransaction(new FinancialTransaction("Alice", "Bob", 100));
        blockchain.addTransaction(new FinancialTransaction("Charlie", "Dave", 75));

        System.out.println("Mining block... (difficulty=" + config.getDifficulty() + ")");
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

        for (Block<FinancialTransaction> block : blockchain.getChain()) {
            System.out.println("Block #" + block.getIndex() + " | Hash: " + block.getHash());
            for (FinancialTransaction tx : block.getTransactions()) {
                System.out.println("  - " + tx);
            }
        }
    }
}
