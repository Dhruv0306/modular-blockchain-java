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

import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<Block<Transaction>>> getBlockchain() {
        return ResponseEntity.ok(blockchain.getChain());
    }

    /**
     * Retrieves a single block by its index.
     * 
     * @param index Block index to retrieve
     * @return Block data or 404 if not found
     */
    @GetMapping("/block/{index}")
    public ResponseEntity<Block<Transaction>> getBlock(@PathVariable("index") int index) {
        if (index < 0 || index >= blockchain.getBlockCount()) {
            return ResponseEntity.notFound().build();
        }
        logger.info("Retriving Block # {}", index);
        return ResponseEntity.ok(blockchain.getChain().get(index));
    }

    /**
     * Adds a new transaction to the pending transaction pool.
     * Transaction will be included in the next mined block.
     * 
     * @param tx Transaction object containing transfer details
     * @return Success/failure message indicating transaction status
     */
    @PostMapping("/transactions")
    public ResponseEntity<String> addTransaction(@RequestBody Transaction tx) {
        if (tx == null || tx.getSender() == null || tx.getReceiver() == null || tx.getAmount() <= 0
                || tx.getSenderID() == null || tx.getReceiverID() == null) {
            logger.error("Error: ", "\"Invalid transaction data.\"");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid transaction data.");
        }

        try {
            if (tx.getType().equalsIgnoreCase("SignedFinancialTransaction")) {
                tx = changeToSignedFinancialTransaction(tx);
            } else {
                tx = new FinancialTransaction(tx.getSender(), tx.getReceiver(), tx.getAmount(), tx.getSenderID(),
                        tx.getReceiverID());
            }

            boolean added = mempool.addTransaction(tx);
            if (added) {
                logger.info("Added: ", "\"Transaction added to MemPool.\"");
                return ResponseEntity.status(HttpStatus.CREATED).body("Transaction added to MemPool.");
            } else {
                logger.error("Error: ", "\"Invalid transaction.\"");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid transaction.");
            }
        } catch (IllegalArgumentException e) {
            logger.error("Invalid transaction: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid transaction: " + e.getMessage());
        } catch (RuntimeException e) {
            logger.error("Transaction processing error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("Transaction processing error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to process transaction: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process transaction: " + e.getMessage());
        }
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
            if (!tx.getSender().equalsIgnoreCase(senderEaWalletEntry.userName)){
                throw new IllegalArgumentException(
                        "Sender name does not match wallet name for Sender: " + tx.getSender() + " and ID: " + tx.getSenderID());
            }

            WalletList.WalletEntry receiverEaWalletEntry = walletList.getWalletByUserID(tx.getReceiverID());
            if (receiverEaWalletEntry == null) {
                throw new IllegalArgumentException(
                        "Receiver wallet not found for Receiver: " + tx.getReceiver() + " and ID: "
                                + tx.getReceiverID());
            }
            if (!tx.getReceiver().equalsIgnoreCase(receiverEaWalletEntry.userName)){
                throw new IllegalArgumentException(
                        "Receiver name does not match wallet name for Receiver: " + tx.getReceiver() + " and ID: "
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
    public ResponseEntity<String> mineBlock() {
        if (mempool.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No transactions to mine.");
        }

        try {
            Block<Transaction> newBlock;
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
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("Block mined and added to chain: " + newBlock.getHash());
            } else {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body("Block mining failed: validation error.");
            }
        } catch (Exception e) {
            logger.error("Block mining failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Block mining failed: " + e.getMessage());
        }
    }

    /**
     * Retrieves all pending transactions that haven't been mined yet.
     * These transactions are waiting to be included in the next block.
     * 
     * @return List of pending transactions in the pool
     */
    @GetMapping("/pending")
    public ResponseEntity<List<Transaction>> getPendingTransactions() {
        List<Transaction> transactions = mempool.getAllTransactions();
        return ResponseEntity.ok(transactions);
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
    public ResponseEntity<String> validateChain() {
        boolean isValid = blockchain.isChainValid();
        if (isValid) {
            return ResponseEntity.ok("Chain is valid.");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Chain is invalid!");
        }
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
