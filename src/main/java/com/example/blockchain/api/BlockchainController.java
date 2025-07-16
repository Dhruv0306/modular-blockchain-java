package com.example.blockchain.api;

import com.example.blockchain.core.chain.Blockchain;
import com.example.blockchain.core.config.ChainConfig;
import com.example.blockchain.transactions.FinancialTransaction;

import jakarta.annotation.PreDestroy;

import com.example.blockchain.consensus.ProofOfWork;
import com.example.blockchain.core.model.Block;
import com.example.blockchain.core.utils.PersistenceManager;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing blockchain operations.
 * Provides endpoints for viewing the chain, adding transactions,
 * mining blocks and validating the chain.
 */
@RestController
@RequestMapping("/api")
public class BlockchainController {

    // Main blockchain instance for storing financial transactions
    private final Blockchain<FinancialTransaction> blockchain = new Blockchain<FinancialTransaction>();
    
    // Consensus mechanism for block mining and validation
    private final ProofOfWork<FinancialTransaction> consensus = new ProofOfWork<>();

    /**
     * Constructor initializes blockchain and loads existing chain data if available.
     * Creates a new genesis block if no existing chain is found.
     */
    public BlockchainController() {
        // Initialize the blockchain with a default genesis block and proof of work
        // consensus
        // Load existing blockchain data if available
        PersistenceManager.loadBlockchain(FinancialTransaction.class)
                .ifPresent(loadedChain -> {
                    this.blockchain.getChain().addAll(loadedChain.getChain());
                    this.blockchain.getPendingTransactions().addAll(loadedChain.getPendingTransactions());
                });
    }

    /**
     * Returns the full blockchain.
     * @return List of all blocks in the chain
     */
    @GetMapping("/chain")
    public List<Block<FinancialTransaction>> getBlockchain() {
        return blockchain.getChain();
    }

    /**
     * Adds a new transaction to the pending transactions pool.
     * @param tx The financial transaction to add
     * @return Success/failure message
     */
    @PostMapping("/transactions")
    public String addTransaction(@RequestBody FinancialTransaction tx) {
        boolean added = blockchain.addTransaction(tx);
        return added ? "Transaction added." : "Invalid transaction.";
    }

    /**
     * Mines a new block with pending transactions.
     * Uses proof of work consensus to generate and validate the block.
     * @return Success/failure message with block hash
     */
    @PostMapping("/mine")
    public String mineBlock() {
        Block<FinancialTransaction> newBlock = consensus.generateBlock(
                blockchain.getPendingTransactions(), blockchain.getLastBlock());
        if (consensus.validateBlock(newBlock, blockchain.getLastBlock())) {
            blockchain.addBlock(newBlock);
            return "Block mined and added to chain: " + newBlock.getHash();
        } else {
            return "Block mining failed.";
        }
    }

    /**
     * Returns list of pending transactions not yet included in a block.
     * @return List of pending financial transactions
     */
    @GetMapping("/pending")
    public List<FinancialTransaction> getPendingTransactions() {
        return blockchain.getPendingTransactions();
    }

    /**
     * Validates the entire blockchain.
     * Checks block hashes and transaction validity.
     * @return Validation status message
     */
    @GetMapping("/validate")
    public String validateChain() {
        return blockchain.isChainValid() ? "Chain is valid." : "Chain is invalid!";
    }

    /**
     * Cleanup method called before shutdown.
     * Persists blockchain state to disk.
     */
    @PreDestroy
    public void cleanup() {
        // Save the blockchain state to disk before shutting down
        PersistenceManager.saveBlockchain(blockchain);
    }
}
