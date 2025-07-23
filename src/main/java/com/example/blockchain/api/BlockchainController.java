package com.example.blockchain.api;

import com.example.blockchain.core.chain.Blockchain;
import com.example.blockchain.core.config.ChainConfig;
import com.example.blockchain.transactions.FinancialTransaction;
import com.example.blockchain.transactions.SignedFinancialTransaction;
import com.example.blockchain.wallet.core.Wallet;
import com.example.blockchain.wallet.core.WalletList;

import jakarta.annotation.PreDestroy;

import com.example.blockchain.consensus.ProofOfWork;
import com.example.blockchain.core.model.Block;
import com.example.blockchain.core.model.Transaction;
import com.example.blockchain.core.pool.Mempool;
import com.example.blockchain.core.utils.PersistenceManager;
import com.example.blockchain.crypto.CryptoUtils;
import com.example.blockchain.logging.BlockchainLoggerFactory;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
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

    // Maximum transections per block
    private int MAX_TRANSECTIONS_PER_BLOCK = 10;

    // Wallet management instance for handling user wallets
    @Autowired
    private WalletList walletList;

    // Mempool management instance for handling pending transactions
    @Autowired
    private Mempool mempool;

    // Consensus mechanism for block mining and validation using proof of work
    private final ProofOfWork<Transaction> consensus = new ProofOfWork<>();

    private static final Logger logger = BlockchainLoggerFactory.getLogger(BlockchainController.class);

    /**
     * Constructor that initializes the blockchain controller.
     * Attempts to load existing blockchain from persistent storage.
     * Creates a new blockchain with genesis block if no saved state exists.
     */
    public BlockchainController() {
        logger.info("Initializing BlockchainController...");

        // Attempt to load existing blockchain from persistent storage
        logger.debug("Loading blockchain from persistent storage");
        this.blockchain = PersistenceManager.loadBlockchain(Transaction.class)
                .orElseGet(() -> {
                    logger.info("No existing blockchain found, creating new instance");
                    return new Blockchain<>();
                });
        logger.info("Blockchain loaded with blocks {}", blockchain.getBlockCount());
        // Get max transactions per block from chain configuration
        logger.debug("Setting max transactions per block from chain config");
        this.MAX_TRANSECTIONS_PER_BLOCK = ChainConfig.getInstance().getMaxTransactionsPerBlock();
        logger.info("BlockchainController initialized with max {} transactions per block",
                MAX_TRANSECTIONS_PER_BLOCK);
    }

    /**
     * Retrieves the complete blockchain data structure.
     * 
     * @return List of all blocks in chronological order
     */
    @GetMapping("/chain")
    public List<Block<Transaction>> getBlockchain() {
        return blockchain.getChain();
    }

    /**
     * Adds a new transaction to the pending transaction pool.
     * Transaction will be included in the next mined block.
     * 
     * @param tx Transaction object containing transfer details
     * @return Success/failure message indicating transaction status
     */
    @PostMapping("/transactions")
    public String addTransaction(@RequestBody Transaction tx) {
        if (tx == null || tx.getSender() == null || tx.getReceiver() == null || tx.getAmount() <= 0
                || tx.getSenderID() == null || tx.getReceiverID() == null) {
            return "Invalid transaction data.";
        }

        if (tx.getType().equalsIgnoreCase("SignedFinancialTransaction")) {
            try {
                tx = changeToSignedFinancialTransaction(tx);
            } catch (Exception e) {
                // Log the exception or handle it appropriately
                return "Error processing transaction: " + e.getMessage();
            }
        } else {
            try {
                tx = new FinancialTransaction(tx.getSender(), tx.getReceiver(), tx.getAmount(), tx.getSenderID(),
                        tx.getReceiverID());
            } catch (NoSuchAlgorithmException e) {
                String error = "Failed to create transaction: " + e.getMessage();
                logger.error(error, e.getMessage());
                return error;
            }
        }
        boolean added = mempool.addTransaction(tx);
        return added ? "Transaction added to MemPool." : "Invalid transaction.";
    }

    /**
     * Converts a transaction to a signed financial transaction if applicable.
     * This is a placeholder method for future enhancements.
     * 
     * @param tx Transaction to convert
     * @return Converted transaction or original if no conversion needed
     */
    private Transaction changeToSignedFinancialTransaction(Transaction tx) {
        try {
            WalletList.WalletEntry senderEaWalletEntry = walletList.getWalletByUserID(tx.getSenderID());
            if (senderEaWalletEntry == null) {
                throw new IllegalArgumentException(
                        "Sender wallet not found for Sender: " + tx.getSender() + " and ID: " + tx.getSenderID());
            }
            WalletList.WalletEntry receiverEaWalletEntry = walletList.getWalletByUserID(tx.getReceiverID());
            if (receiverEaWalletEntry == null) {
                throw new IllegalArgumentException(
                        "Receiver wallet not found for Receiver: " + tx.getReceiver() + " and ID: "
                                + tx.getReceiverID());
            }

            Wallet senderWallet = senderEaWalletEntry.wallet;
            long timestamp = System.currentTimeMillis();
            String dataToSign = tx.getSenderID() + " -> " + tx.getReceiverID() + " : $" + tx.getAmount() + " (time: "
                    + timestamp + ")";
            String signature = CryptoUtils.signData(dataToSign, senderWallet.getPrivateKey());
            if (signature == null) {
                throw new RuntimeException("Failed to sign transaction data for sender: " + tx.getSender());
            }

            SignedFinancialTransaction signedTx = new SignedFinancialTransaction(
                    tx.getSender(), tx.getReceiver(), tx.getAmount(),
                    senderWallet.getPublicKey(), signature,
                    tx.getSenderID(), tx.getReceiverID(), timestamp);
            if (tx.getTransactionId() != null) {
                signedTx.setTransactionId(tx.getTransactionId());
            }

            // Validate the signed transaction
            if (!signedTx.verifySignature()) {
                throw new RuntimeException("Signature verification failed for transaction: " + signedTx);
            }

            return signedTx;
        } catch (Exception e) {
            throw new RuntimeException("Failed to process signed financial transaction: " + e.getMessage(), e);
        }
    }

    /**
     * Mines a new block by processing pending transactions.
     * Uses proof of work consensus to:
     * 1. Generate valid block with transactions
     * 2. Calculate proof of work
     * 3. Validate and add block to chain
     * 
     * @return Success message with block hash or failure message
     */
    @PostMapping("/mine")
    public String mineBlock() {
        Block<Transaction> newBlock = null;
        if (mempool.isEmpty()) {
            return "No transactions to mine.";
        }
        if (mempool.size() < this.MAX_TRANSECTIONS_PER_BLOCK) {
            newBlock = consensus.generateBlock(mempool.getAllTransactions(),
                    blockchain.getLastBlock());
        } else {
            newBlock = consensus.generateBlock(mempool.getTopN(MAX_TRANSECTIONS_PER_BLOCK),
                    blockchain.getLastBlock());
        }
        if (consensus.validateBlock(newBlock, blockchain.getLastBlock())) {
            blockchain.addBlock(newBlock);
            mempool.removeAllTransactions(newBlock.getTransactions());
            return "Block mined and added to chain: " + newBlock.getHash();
        } else {
            return "Block mining failed.";
        }
    }

    /**
     * Retrieves all pending transactions that haven't been mined yet.
     * These transactions are waiting to be included in the next block.
     * 
     * @return List of pending transactions in the pool
     */
    @GetMapping("/pending")
    public List<Transaction> getPendingTransactions() {
        return mempool.getAllTransactions();
    }

    /**
     * Validates the integrity of the entire blockchain.
     * Checks:
     * - Block hash validity
     * - Previous block hash links
     * - Transaction data integrity
     * 
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
