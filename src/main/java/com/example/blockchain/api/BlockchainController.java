package com.example.blockchain.api;

import com.example.blockchain.core.chain.Blockchain;
import com.example.blockchain.core.config.ChainConfig;
import com.example.blockchain.transactions.FinancialTransaction;

import jakarta.annotation.PreDestroy;

import com.example.blockchain.consensus.ProofOfWork;
import com.example.blockchain.core.model.Block;
import com.example.blockchain.core.model.Transaction;
import com.example.blockchain.core.utils.PersistenceManager;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API Controller that handles blockchain operations and endpoints.
 * Provides functionality to:
 * - View the complete blockchain
 * - Submit new transactions
 * - Mine blocks
 * - View pending transactions
 * - Validate chain integrity
 */
@RestController
@RequestMapping("/api")
public class BlockchainController {

    // Main blockchain instance for managing transaction data and chain state
    private final Blockchain<Transaction> blockchain;
    
    // Consensus mechanism for block mining and validation using proof of work
    private final ProofOfWork<Transaction> consensus = new ProofOfWork<>();

    /**
     * Constructor that initializes the blockchain controller.
     * Attempts to load existing blockchain from persistent storage.
     * Creates a new blockchain with genesis block if no saved state exists.
     */
    public BlockchainController() {
        // Initialize blockchain from saved state or create new
        this.blockchain = PersistenceManager.loadBlockchain(Transaction.class)
        .orElseGet(() -> new Blockchain<>()); 
    }

    /**
     * Retrieves the complete blockchain data structure.
     * @return List of all blocks in chronological order
     */
    @GetMapping("/chain")
    public List<Block<Transaction>> getBlockchain() {
        return blockchain.getChain();
    }

    /**
     * Adds a new transaction to the pending transaction pool.
     * Transaction will be included in the next mined block.
     * @param tx Transaction object containing transfer details
     * @return Success/failure message indicating transaction status
     */
    @PostMapping("/transactions")
    public String addTransaction(@RequestBody Transaction tx) {
        boolean added = blockchain.addTransaction(tx);
        return added ? "Transaction added." : "Invalid transaction.";
    }

    /**
     * Mines a new block by processing pending transactions.
     * Uses proof of work consensus to:
     * 1. Generate valid block with transactions
     * 2. Calculate proof of work
     * 3. Validate and add block to chain
     * @return Success message with block hash or failure message
     */
    @PostMapping("/mine")
    public String mineBlock() {
        Block<Transaction> newBlock = consensus.generateBlock(
                blockchain.getPendingTransactions(), blockchain.getLastBlock());
        if (consensus.validateBlock(newBlock, blockchain.getLastBlock())) {
            blockchain.addBlock(newBlock);
            return "Block mined and added to chain: " + newBlock.getHash();
        } else {
            return "Block mining failed.";
        }
    }

    /**
     * Retrieves all pending transactions that haven't been mined yet.
     * These transactions are waiting to be included in the next block.
     * @return List of pending transactions in the pool
     */
    @GetMapping("/pending")
    public List<Transaction> getPendingTransactions() {
        return blockchain.getPendingTransactions();
    }

    /**
     * Validates the integrity of the entire blockchain.
     * Checks:
     * - Block hash validity
     * - Previous block hash links
     * - Transaction data integrity
     * @return Status message indicating if chain is valid
     */
    @GetMapping("/validate")
    public String validateChain() {
        return blockchain.isChainValid() ? "Chain is valid." : "Chain is invalid!";
    }

    /**
     * Cleanup method called before application shutdown.
     * Persists the current blockchain state to disk for recovery.
     * Ensures no data is lost between application restarts.
     */
    @PreDestroy
    public void cleanup() {
        // Save current blockchain state to persistent storage
        PersistenceManager.saveBlockchain(blockchain);
    }
}
