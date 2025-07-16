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

@RestController
@RequestMapping("/api")
public class BlockchainController {

    private final Blockchain<FinancialTransaction> blockchain = new Blockchain<FinancialTransaction>();
    private final ProofOfWork<FinancialTransaction> consensus = new ProofOfWork<>();

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

    @GetMapping("/chain")
    public List<Block<FinancialTransaction>> getBlockchain() {
        return blockchain.getChain();
    }

    @PostMapping("/transactions")
    public String addTransaction(@RequestBody FinancialTransaction tx) {
        boolean added = blockchain.addTransaction(tx);
        return added ? "Transaction added." : "Invalid transaction.";
    }

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

    @GetMapping("/pending")
    public List<FinancialTransaction> getPendingTransactions() {
        return blockchain.getPendingTransactions();
    }

    @GetMapping("/validate")
    public String validateChain() {
        return blockchain.isChainValid() ? "Chain is valid." : "Chain is invalid!";
    }

    @PreDestroy
    public void cleanup() {
        // Save the blockchain state to disk before shutting down
        PersistenceManager.saveBlockchain(blockchain);
    }
}
