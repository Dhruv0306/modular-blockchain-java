package com.example.blockchain.core.model;

import com.example.blockchain.consensus.ProofOfWork;
import com.example.blockchain.core.chain.Blockchain;
import com.example.blockchain.core.model.Block;
import com.example.blockchain.crypto.CryptoUtils;
import com.example.blockchain.transactions.SignedFinancialTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for validating block integrity and transaction tampering detection in the blockchain
 */
public class BlockValidationTest {

    // Core blockchain components
    private Blockchain<SignedFinancialTransaction> blockchain;
    private ProofOfWork<SignedFinancialTransaction> pow;
    
    // Test keypairs for transaction signing
    private KeyPair aliceKeyPair;
    private KeyPair bobKeyPair;

    /**
     * Sets up the test environment before each test
     * Initializes blockchain, proof of work, and keypairs for Alice and Bob
     */
    @BeforeEach
    void setUp() throws Exception {
        pow = new ProofOfWork<>();
        blockchain = new Blockchain<>();
        aliceKeyPair = CryptoUtils.generateKeyPair();
        bobKeyPair = CryptoUtils.generateKeyPair();
    }

    /**
     * Tests that the blockchain can detect tampered transactions in blocks
     * Steps:
     * 1. Create and add valid transaction
     * 2. Generate valid block with transaction
     * 3. Create tampered version of transaction
     * 4. Replace valid block with tampered block
     * 5. Verify chain is marked as invalid
     */
    @Test
    void testBlockWithTamperedSignedTransaction() throws Exception {
        // Create a valid signed transaction from Alice to Bob
        long timestamp = System.currentTimeMillis();
        String summary = "Alice -> Bob : $100.0 (time: " + timestamp + ")";
        String signature = CryptoUtils.signData(summary, aliceKeyPair.getPrivate());
        
        // Construct the valid transaction object with Alice's signature
        SignedFinancialTransaction validTx = new SignedFinancialTransaction(
                "Alice", "Bob", 100.0, aliceKeyPair.getPublic(), signature, null, timestamp);
        
        // Add valid transaction to pending transactions pool
        blockchain.addTransaction(validTx);
        
        // Mine a new block containing the valid transaction
        Block<SignedFinancialTransaction> validBlock = pow.generateBlock(
                blockchain.getPendingTransactions(), 
                blockchain.getLastBlock());
        
        // Add the valid block to the blockchain
        blockchain.addBlock(validBlock);
        
        // Create a tampered version of the transaction with Bob's signature instead
        String tamperedSignature = CryptoUtils.signData("Tampered data", bobKeyPair.getPrivate());
        SignedFinancialTransaction tamperedTx = new SignedFinancialTransaction(
                "Alice", "Bob", 100.0, aliceKeyPair.getPublic(), tamperedSignature, null, timestamp);
        
        // Package the tampered transaction into a new block
        List<SignedFinancialTransaction> tamperedTxs = new ArrayList<>();
        tamperedTxs.add(tamperedTx);
        
        // Create tampered block that maintains the original block's hash
        // This simulates an attempt to replace transaction while preserving block hash
        Block<SignedFinancialTransaction> tamperedBlock = new Block<>(
                validBlock.getIndex(),
                validBlock.getPreviousHash(),
                validBlock.getTimestamp(),
                tamperedTxs,
                validBlock.getNonce(),
                validBlock.getHash() // Deliberately keep original hash to attempt deception
        );
        
        // Attempt to swap the valid block with the tampered one
        blockchain.getChain().set(1, tamperedBlock);
        
        // Verify that the blockchain detects the invalid block
        // The hash verification should fail since block contents changed
        assertFalse(blockchain.isChainValid(), "Chain with tampered transaction should be invalid");
    }
}
