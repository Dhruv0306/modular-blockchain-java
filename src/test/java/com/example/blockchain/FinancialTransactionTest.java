package com.example.blockchain;

import com.example.blockchain.transactions.FinancialTransaction;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FinancialTransactionTest {

    @Test
    void testIsValidTrue() {
        FinancialTransaction tx = new FinancialTransaction("Alice", "Bob", 10.0);
        assertTrue(tx.isValid());
    }

    @Test
    void testIsValidNullSender() {
        FinancialTransaction tx = new FinancialTransaction(null, "Bob", 10.0);
        assertFalse(tx.isValid());
    }

    @Test
    void testIsValidNullReceiver() {
        FinancialTransaction tx = new FinancialTransaction("Alice", null, 10.0);
        assertFalse(tx.isValid());
    }

    @Test
    void testIsValidZeroAmount() {
        FinancialTransaction tx = new FinancialTransaction("Alice", "Bob", 0.0);
        assertFalse(tx.isValid());
    }

    @Test
    void testIsValidNegativeAmount() {
        FinancialTransaction tx = new FinancialTransaction("Alice", "Bob", -5.0);
        assertFalse(tx.isValid());
    }

    @Test
    void testGetSummary() {
        FinancialTransaction tx = new FinancialTransaction("Alice", "Bob", 25.5);
        assertEquals("Alice -> Bob : $25.5", tx.getSummary());
    }
}