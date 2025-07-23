package com.example.blockchain.integration;

import com.example.blockchain.consensus.ProofOfWork;
import com.example.blockchain.core.chain.Blockchain;
import com.example.blockchain.core.config.CustomGenesisBlockFactory;
import com.example.blockchain.core.model.Block;
import com.example.blockchain.logging.BlockchainLoggerFactory;
import com.example.blockchain.transactions.FinancialTransaction;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test class that simulates end-to-end blockchain usage scenarios.
 * Tests various blockchain operations including block creation, transaction
 * handling,
 * and validation of the blockchain state.
 */
class BlockchainIntegrationTest {
        private static final org.slf4j.Logger logger = BlockchainLoggerFactory
                        .getLogger(BlockchainIntegrationTest.class);

        /**
         * Tests a basic blockchain workflow using the default genesis block.
         * Verifies:
         * - Genesis block creation
         * - Transaction addition
         * - Block generation and validation
         * - Chain state after block addition
         */
        @Test
        void testDefaultBlockchainWorkflow() {
                // Initialize blockchain with proof of work consensus
                ProofOfWork<FinancialTransaction> consensus = new ProofOfWork<>();
                Blockchain<FinancialTransaction> blockchain = new Blockchain<>();

                // Verify genesis block exists and has correct initial state
                assertEquals(1, blockchain.getChain().size());
                assertEquals(0, blockchain.getChain().get(0).getIndex());

                // Validate initial chain state
                assertTrue(blockchain.isChainValid(), "Genesis block chain should be valid");

                // Add sample transactions to pending pool
                try {
                        blockchain.addTransaction(new FinancialTransaction("Alice", "Bob", 100));
                        blockchain.addTransaction(new FinancialTransaction("Charlie", "Dave", 75));
                } catch (NoSuchAlgorithmException e) {
                        String error = "Failed To create Transection. \nError: " + e.getMessage();
                        logger.error(error, e.getMessage());
                        throw new RuntimeException(error, e);
                }

                // Generate new block with pending transactions
                Block<FinancialTransaction> newBlock = consensus.generateBlock(
                                blockchain.getPendingTransactions(),
                                blockchain.getLastBlock());

                // Validate the newly generated block
                assertTrue(consensus.validateBlock(newBlock, blockchain.getLastBlock()));

                // Add validated block to chain
                blockchain.addBlock(newBlock);

                // Verify final blockchain state
                assertEquals(2, blockchain.getChain().size());
                assertEquals(1, newBlock.getIndex());
                assertTrue(blockchain.getPendingTransactions().isEmpty());
        }

        /**
         * Tests blockchain workflow with a custom genesis block configuration.
         * Verifies:
         * - Custom genesis block creation with initial transactions
         * - Chain validation with custom genesis
         * - New block addition after custom genesis
         */
        @Test
        void testCustomGenesisBlockchainWorkflow() {
                // Initialize custom genesis block with predefined transactions
                CustomGenesisBlockFactory<FinancialTransaction> factory;
                try {
                        factory = CustomGenesisBlockFactory
                                        .<FinancialTransaction>builder()
                                        .withHash("CUSTOM_GENESIS")
                                        .addTransaction(new FinancialTransaction("Genesis", "Alice", 1000))
                                        .addTransaction(new FinancialTransaction("Genesis", "Bob", 1000))
                                        .build();
                } catch (NoSuchAlgorithmException e) {
                        String error = "Failed To create Transection. \nError: " + e.getMessage();
                        logger.error(error, e.getMessage());
                        throw new RuntimeException(error, e);
                }

                // Create blockchain with custom genesis configuration
                ProofOfWork<FinancialTransaction> consensus = new ProofOfWork<>();
                Blockchain<FinancialTransaction> blockchain = new Blockchain<>(factory, consensus);

                // Verify custom genesis block properties
                Block<FinancialTransaction> genesis = blockchain.getChain().get(0);
                assertEquals(2, genesis.getTransactions().size());
                assertEquals("0", genesis.getPreviousHash());

                // Validate chain with custom genesis
                assertTrue(blockchain.isChainValid(), "Custom genesis blockchain should be valid");

                // Test block addition after custom genesis
                try {
                        blockchain.addTransaction(new FinancialTransaction("Alice", "Bob", 100));
                } catch (NoSuchAlgorithmException e) {
                        String error = "Failed To create Transection. \nError: " + e.getMessage();
                        logger.error(error, e.getMessage());
                        throw new RuntimeException(error, e);
                }
                Block<FinancialTransaction> newBlock = consensus.generateBlock(
                                blockchain.getPendingTransactions(),
                                blockchain.getLastBlock());

                assertTrue(consensus.validateBlock(newBlock, blockchain.getLastBlock()));
                blockchain.addBlock(newBlock);

                // Verify final state
                assertEquals(2, blockchain.getChain().size());
                assertEquals(1, newBlock.getIndex());
        }

        /**
         * Tests the blockchain's ability to detect transaction tampering after block
         * creation.
         * Creates a valid block, then attempts to tamper with its transactions while
         * keeping
         * other block properties unchanged.
         */
        @Test
        void testDetectTransactionTampering() {
                // Initialize blockchain for tampering detection test
                ProofOfWork<FinancialTransaction> consensus = new ProofOfWork<>();
                Blockchain<FinancialTransaction> blockchain = new Blockchain<>();

                // Create and validate initial legitimate transaction
                FinancialTransaction validTx;
                try {
                        validTx = new FinancialTransaction("Alice", "Bob", 100);
                } catch (NoSuchAlgorithmException e) {
                        String error = "Failed To create Transection. \nError: " + e.getMessage();
                        logger.error(error, e.getMessage());
                        throw new RuntimeException(error, e);
                }
                Block<FinancialTransaction> genesisBlock = blockchain.getLastBlock();

                // Create and validate legitimate block
                List<FinancialTransaction> validTxs = new ArrayList<>();
                validTxs.add(validTx);
                Block<FinancialTransaction> validBlock = consensus.generateBlock(validTxs, genesisBlock);
                assertTrue(consensus.validateBlock(validBlock, genesisBlock));

                // Create tampered transaction with modified amount
                FinancialTransaction tamperedTx;
                try {
                        tamperedTx = new FinancialTransaction("Alice", "Bob", 1000);
                } catch (NoSuchAlgorithmException e) {
                        String error = "Failed To create Transection. \nError: " + e.getMessage();
                        logger.error(error, e.getMessage());
                        throw new RuntimeException(error, e);
                }
                List<FinancialTransaction> tamperedTxs = new ArrayList<>();
                tamperedTxs.add(tamperedTx);

                // Create block with tampered transaction but original block properties
                Block<FinancialTransaction> tamperedBlock = new Block<>(
                                validBlock.getIndex(),
                                validBlock.getPreviousHash(),
                                validBlock.getTimestamp(),
                                tamperedTxs,
                                validBlock.getNonce(),
                                validBlock.getHash());

                // Verify tampering detection
                assertFalse(consensus.validateBlock(tamperedBlock, genesisBlock),
                                "Block with tampered transactions should be detected");
        }

        /**
         * Tests detection of blocks with non-sequential indices.
         * Attempts to add a block with an index that skips a number in the sequence.
         */
        @Test
        void testDetectNonSequentialBlockIndex() {
                Blockchain<FinancialTransaction> blockchain = new Blockchain<>();

                // Verify initial chain validity
                assertTrue(blockchain.isChainValid(), "Genesis blockchain should be valid");

                // Create block with non-sequential index
                List<FinancialTransaction> txs = new ArrayList<>();
                try {
                        txs.add(new FinancialTransaction("Eve", "Frank", 300));
                } catch (NoSuchAlgorithmException e) {
                        String error = "Failed To create Transection. \nError: " + e.getMessage();
                        logger.error(error, e.getMessage());
                        throw new RuntimeException(error, e);
                }

                // Generate hash for invalid block
                String validHash = "0000" + System.currentTimeMillis();

                // Create block with invalid index sequence
                Block<FinancialTransaction> nonSequentialBlock = new Block<>(
                                2, // Invalid index - skips 1
                                blockchain.getLastBlock().getHash(),
                                System.currentTimeMillis(),
                                txs,
                                0,
                                validHash);

                // Add invalid block and verify detection
                blockchain.addBlock(nonSequentialBlock);
                assertFalse(blockchain.isChainValid(), "Chain should be invalid with non-sequential indices");
        }

        /**
         * Tests detection of blocks with invalid previous hash references.
         * Creates a valid block chain then attempts to add a block with an incorrect
         * previous hash reference.
         */
        @Test
        void testDetectInvalidPreviousHashReference() {
                // Initialize blockchain for hash reference test
                ProofOfWork<FinancialTransaction> consensus = new ProofOfWork<>();
                Blockchain<FinancialTransaction> blockchain = new Blockchain<>();

                // Add initial valid block to chain
                try {
                        blockchain.addTransaction(new FinancialTransaction("Alice", "Bob", 100));
                } catch (NoSuchAlgorithmException e) {
                        String error = "Failed To create Transection. \nError: " + e.getMessage();
                        logger.error(error, e.getMessage());
                        throw new RuntimeException(error, e);
                }
                Block<FinancialTransaction> validBlock = consensus.generateBlock(
                                blockchain.getPendingTransactions(),
                                blockchain.getLastBlock());
                blockchain.addBlock(validBlock);

                // Create block with invalid previous hash
                List<FinancialTransaction> txs = new ArrayList<>();
                try {
                        txs.add(new FinancialTransaction("Eve", "Frank", 200));
                } catch (NoSuchAlgorithmException e) {
                        String error = "Failed To create Transection. \nError: " + e.getMessage();
                        logger.error(error, e.getMessage());
                        throw new RuntimeException(error, e);
                }
                String validHash = "0000" + System.currentTimeMillis();

                // Construct block with incorrect previous hash reference
                Block<FinancialTransaction> invalidPrevHashBlock = new Block<>(
                                2,
                                "FAKE_PREVIOUS_HASH", // Invalid previous hash reference
                                System.currentTimeMillis(),
                                txs,
                                0,
                                validHash);

                // Add invalid block and verify detection
                blockchain.addBlock(invalidPrevHashBlock);
                assertFalse(blockchain.isChainValid(), "Chain should be invalid with incorrect previous hash");
        }
}
