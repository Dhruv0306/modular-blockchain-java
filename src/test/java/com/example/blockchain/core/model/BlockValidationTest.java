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

public class BlockValidationTest {

    private Blockchain<SignedFinancialTransaction> blockchain;
    private ProofOfWork<SignedFinancialTransaction> pow;
    private KeyPair aliceKeyPair;
    private KeyPair bobKeyPair;

    @BeforeEach
    void setUp() throws Exception {
        pow = new ProofOfWork<>();
        blockchain = new Blockchain<>();
        aliceKeyPair = CryptoUtils.generateKeyPair();
        bobKeyPair = CryptoUtils.generateKeyPair();
    }

    @Test
    void testBlockWithTamperedSignedTransaction() throws Exception {
        // Create a valid signed transaction
        long timestamp = System.currentTimeMillis();
        String summary = "Alice -> Bob : $100.0 (time: " + timestamp + ")";
        String signature = CryptoUtils.signData(summary, aliceKeyPair.getPrivate());
        
        SignedFinancialTransaction validTx = new SignedFinancialTransaction(
                "Alice", "Bob", 100.0, aliceKeyPair.getPublic(), signature, null, timestamp);
        
        // Add transaction to blockchain
        blockchain.addTransaction(validTx);
        
        // Generate a valid block with the transaction
        Block<SignedFinancialTransaction> validBlock = pow.generateBlock(
                blockchain.getPendingTransactions(), 
                blockchain.getLastBlock());
        
        // Add the block to the chain
        blockchain.addBlock(validBlock);
        
        // Now create a tampered transaction with the same data but different signature
        String tamperedSignature = CryptoUtils.signData("Tampered data", bobKeyPair.getPrivate());
        SignedFinancialTransaction tamperedTx = new SignedFinancialTransaction(
                "Alice", "Bob", 100.0, aliceKeyPair.getPublic(), tamperedSignature, null, timestamp);
        
        // Create a new block with the tampered transaction but keep the original hash
        List<SignedFinancialTransaction> tamperedTxs = new ArrayList<>();
        tamperedTxs.add(tamperedTx);
        
        Block<SignedFinancialTransaction> tamperedBlock = new Block<>(
                validBlock.getIndex(),
                validBlock.getPreviousHash(),
                validBlock.getTimestamp(),
                tamperedTxs,
                validBlock.getNonce(),
                validBlock.getHash() // Keep the original hash
        );
        
        // Replace the valid block with the tampered block
        blockchain.getChain().set(1, tamperedBlock);
        
        // Chain should now be invalid because the hash doesn't match the content
        assertFalse(blockchain.isChainValid(), "Chain with tampered transaction should be invalid");
    }
}