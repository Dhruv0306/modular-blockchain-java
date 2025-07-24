package com.example.blockchain.transactions;

import com.example.blockchain.logging.BlockchainLoggerFactory;
import com.example.blockchain.transactions.FinancialTransaction;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.security.NoSuchAlgorithmException;

/**
 * Test class for FinancialTransaction
 * Tests the validation, equality, and string representation functionality
 */
public class FinancialTransactionTest {
    private static final org.slf4j.Logger logger = BlockchainLoggerFactory.getLogger(FinancialTransactionTest.class);

    /**
     * Test that a valid transaction with non-null sender/receiver and positive
     * amount passes validation
     */
    @Test
    void testIsValidTrue() {
        FinancialTransaction tx;
        try {
            tx = new FinancialTransaction("Alice", "Bob", 10.0);
        } catch (NoSuchAlgorithmException e) {
            String error = "Failed To create Transection. \nError: " + e.getMessage();
            logger.error(error, e.getMessage());
            throw new RuntimeException(error, e);
        }
        assertTrue(tx.isValid());
    }

    /**
     * Test that a transaction with null sender fails validation
     */
    @Test
    void testIsValidNullSender() {
        FinancialTransaction tx;
        try {
            tx = new FinancialTransaction(null, "Bob", 10.0);
        } catch (NoSuchAlgorithmException e) {
            String error = "Failed To create Transection. \nError: " + e.getMessage();
            logger.error(error, e.getMessage());
            throw new RuntimeException(error, e);
        }
        assertFalse(tx.isValid());
    }

    /**
     * Test that a transaction with null receiver fails validation
     */
    @Test
    void testIsValidNullReceiver() {
        FinancialTransaction tx;
        try {
            tx = new FinancialTransaction("Alice", null, 10.0);
        } catch (NoSuchAlgorithmException e) {
            String error = "Failed To create Transection. \nError: " + e.getMessage();
            logger.error(error, e.getMessage());
            throw new RuntimeException(error, e);
        }
        assertFalse(tx.isValid());
    }

    /**
     * Test that a transaction with zero amount fails validation
     */
    @Test
    void testIsValidZeroAmount() {
        FinancialTransaction tx;
        try {
            tx = new FinancialTransaction("Alice", "Bob", 0.0);
        } catch (NoSuchAlgorithmException e) {
String error = "Failed To create Transection. \nError: " + e.getMessage();
            logger.error(error, e.getMessage());
            throw new RuntimeException(error, e);
        }
        assertFalse(tx.isValid());
    }

    /**
     * Test that a transaction with negative amount fails validation
     */
    @Test
    void testIsValidNegativeAmount() {
        FinancialTransaction tx;
        try {
            tx = new FinancialTransaction("Alice", "Bob", -5.0);
        } catch (NoSuchAlgorithmException e) {
            String error = "Failed To create Transection. \nError: " + e.getMessage();
            logger.error(error, e.getMessage());
            throw new RuntimeException(error, e);
        }
        assertFalse(tx.isValid());
    }

    /**
     * Test the getSummary() method returns correct string format
     */
    @Test
    void testGetSummary() {
        FinancialTransaction tx;
        try {
            tx = new FinancialTransaction("Alice", "Bob", 25.5);
        } catch (NoSuchAlgorithmException e) {
            String error = "Failed To create Transection. \nError: " + e.getMessage();
            logger.error(error, e.getMessage());
            throw new RuntimeException(error, e);
        }
        assertEquals("Alice -> Bob : $25.5", tx.getSummary());
    }

    /**
     * Test the toString() method returns correct string format including
     * transaction ID
     */
    @Test
    void testToString() {
        FinancialTransaction tx;
        try {
            tx = new FinancialTransaction("Alice", "Bob", 25.5,
                    "74c4f2fc-5c91-31ed-a105-471ca9c83bd1");
        } catch (NoSuchAlgorithmException e) {
            String error = "Failed To create Transection. \nError: " + e.getMessage();
            logger.error(error, e.getMessage());
            throw new RuntimeException(error, e);
        }
        assertEquals("Alice -> Bob : $25.5 [ID: 74c4f2fc-5c91-31ed-a105-471ca9c83bd1]", tx.toString());
    }

    /**
     * Test equals() method for various scenarios:
     * - Same object reference
     * - Equal objects
     * - Different receiver
     * - Different sender
     * - Different amount
     * - Null comparison
     * - Different type comparison
     */
    @Test
    void testEquals() {
        FinancialTransaction tx1;
        FinancialTransaction tx2;
        FinancialTransaction tx3;
        FinancialTransaction tx4;
        FinancialTransaction tx5;
        try {
            tx1 = new FinancialTransaction("Alice", "Bob", 10.0);
            tx2 = new FinancialTransaction("Alice", "Bob", 10.0);
            tx3 = new FinancialTransaction("Alice", "Charlie", 10.0);
            tx4 = new FinancialTransaction("Charlie", "Bob", 10.0);
            tx5 = new FinancialTransaction("Alice", "Bob", 15.0);
        } catch (NoSuchAlgorithmException e) {
            String error = "Failed To create Transection. \nError: " + e.getMessage();
            logger.error(error, e.getMessage());
            throw new RuntimeException(error, e);
        }
        assertTrue(tx1.equals(tx1));
        assertTrue(tx1.equals(tx2));
        assertFalse(tx1.equals(tx3)); // different receiver
        assertFalse(tx1.equals(tx4)); // different sender
        assertFalse(tx1.equals(tx5)); // different amount
        assertFalse(tx1.equals(null));
        assertFalse(tx1.equals("string"));
    }

    /**
     * Test that hashCode() returns different value for transactions with same
     * values
     * since they have different transaction IDs generated with timestamps.
     */
    @Test
    void testHashCode() throws InterruptedException {
        FinancialTransaction tx1;
        try {
            tx1 = new FinancialTransaction("Alice", "Bob", 10.0);
        } catch (NoSuchAlgorithmException e) {
            String error = "Failed To create Transection. \nError: " + e.getMessage();
            logger.error(error, e.getMessage());
            throw new RuntimeException(error, e);
        }
        // Add a small delay to ensure different timestamps in transaction ID generation
        Thread.sleep(5);
        FinancialTransaction tx2;
        try {
            tx2 = new FinancialTransaction("Alice", "Bob", 10.0);
        } catch (NoSuchAlgorithmException e) {
            String error = "Failed To create Transection. \nError: " + e.getMessage();
            logger.error(error, e.getMessage());
            throw new RuntimeException(error, e);
        }

        System.out.println("First Transaction: " + tx1);
        System.out.println("Second Transaction: " + tx2);

        assertNotEquals(tx1.hashCode(), tx2.hashCode(),
                "Should be different since both have different transaction IDs");
    }
}
