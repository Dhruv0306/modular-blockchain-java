package com.example.blockchain.transactions;

import static org.junit.jupiter.api.Assertions.*;

import java.security.KeyPair;

import org.junit.jupiter.api.Test;

import com.example.blockchain.crypto.CryptoUtils;
import com.example.blockchain.transactions.SignedFinancialTransaction;

/**
 * Test class for SignedFinancialTransaction
 * Verifies the validation, creation and functionality of signed financial transactions
 */
class SignedFinancialTransactionTest {

    /**
     * Tests that a properly constructed transaction with valid signature passes validation
     */
    @Test
    void testValidSignedTransaction() throws Exception {
        KeyPair pair = CryptoUtils.generateKeyPair();
        // Create transaction with fixed timestamp for testing
        long timestamp = 1234567890L;
        String summary = "Alice -> Bob : $100.0 (time: " + timestamp + ")";
        String signature = CryptoUtils.signData(summary, pair.getPrivate());

        SignedFinancialTransaction tx = new SignedFinancialTransaction(
                "Alice", "Bob", 100.0, pair.getPublic(), signature, null, timestamp);

        assertTrue(tx.isValid());
    }

    /**
     * Tests that a transaction with invalid signature fails validation
     */
    @Test
    void testInvalidSignatureFailsValidation() throws Exception {
        KeyPair pair = CryptoUtils.generateKeyPair();
        // Create signature with incorrect data
        String signature = CryptoUtils.signData("invalid data", pair.getPrivate());
        long timestamp = 1234567890L;

        SignedFinancialTransaction tx = new SignedFinancialTransaction(
                "Alice", "Bob", 100.0, pair.getPublic(), signature, null, timestamp);

        assertFalse(tx.isValid());
    }

    /**
     * Tests that a transaction with zero amount fails validation
     */
    @Test
    void testInvalidAmountFailsValidation() throws Exception {
        KeyPair pair = CryptoUtils.generateKeyPair();
        long timestamp = 1234567890L;
        String summary = "Alice -> Bob : $0.0 (time: " + timestamp + ")";
        String signature = CryptoUtils.signData(summary, pair.getPrivate());

        SignedFinancialTransaction tx = new SignedFinancialTransaction(
                "Alice", "Bob", 0.0, pair.getPublic(), signature, null, timestamp);

        assertFalse(tx.isValid());
    }

    /**
     * Tests that a transaction with null receiver fails validation
     */
    @Test
    void testNullReceiverFailsValidation() throws Exception {
        KeyPair pair = CryptoUtils.generateKeyPair();
        long timestamp = 1234567890L;
        String summary = "Alice -> null : $100.0 (time: " + timestamp + ")";
        String signature = CryptoUtils.signData(summary, pair.getPrivate());

        SignedFinancialTransaction tx = new SignedFinancialTransaction(
                "Alice", null, 100.0, pair.getPublic(), signature, null, timestamp);

        assertFalse(tx.isValid());
    }

    /**
     * Tests that toString() includes both the transaction summary and ID
     */
    @Test
    void testToStringIncludesTransactionId() throws Exception {
        KeyPair pair = CryptoUtils.generateKeyPair();
        long timestamp = 1234567890L;
        String summary = "Alice -> Bob : $50.0 (time: " + timestamp + ")";
        String signature = CryptoUtils.signData(summary, pair.getPrivate());

        SignedFinancialTransaction tx = new SignedFinancialTransaction(
                "Alice", "Bob", 50.0, pair.getPublic(), signature, null, timestamp);

        String expectedPrefix = summary;
        String actualString = tx.toString();
        
        // Verify the string contains both summary and ID
        assertTrue(actualString.startsWith(expectedPrefix), 
                "Transaction toString should start with the summary");
        assertTrue(actualString.contains("[ID: "), 
                "Transaction toString should include the ID");
    }
    
    /**
     * Tests that getTimestamp() returns the correct timestamp value
     */
    @Test
    void testTimestampGetter() throws Exception {
        KeyPair pair = CryptoUtils.generateKeyPair();
        long expectedTimestamp = 1234567890L;
        String summary = "Alice -> Bob : $50.0 (time: " + expectedTimestamp + ")";
        String signature = CryptoUtils.signData(summary, pair.getPrivate());

        SignedFinancialTransaction tx = new SignedFinancialTransaction(
                "Alice", "Bob", 50.0, pair.getPublic(), signature, null, expectedTimestamp);

        assertEquals(expectedTimestamp, tx.getTimestamp(), 
                "getTimestamp should return the timestamp used during construction");
    }
}
