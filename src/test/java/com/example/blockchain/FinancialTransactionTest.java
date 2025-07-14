package com.example.blockchain;

import com.example.blockchain.transactions.FinancialTransaction;
import junit.framework.TestCase;

public class FinancialTransactionTest extends TestCase {

    public void testIsValidTrue() {
        FinancialTransaction tx = new FinancialTransaction("Alice", "Bob", 10.0);
        assertTrue(tx.isValid());
    }

    public void testIsValidNullSender() {
        FinancialTransaction tx = new FinancialTransaction(null, "Bob", 10.0);
        assertFalse(tx.isValid());
    }

    public void testIsValidNullReceiver() {
        FinancialTransaction tx = new FinancialTransaction("Alice", null, 10.0);
        assertFalse(tx.isValid());
    }

    public void testIsValidZeroAmount() {
        FinancialTransaction tx = new FinancialTransaction("Alice", "Bob", 0.0);
        assertFalse(tx.isValid());
    }

    public void testIsValidNegativeAmount() {
        FinancialTransaction tx = new FinancialTransaction("Alice", "Bob", -5.0);
        assertFalse(tx.isValid());
    }

    public void testGetSummary() {
        FinancialTransaction tx = new FinancialTransaction("Alice", "Bob", 25.5);
        assertEquals("Alice -> Bob : $25.5", tx.getSummary());
    }
}