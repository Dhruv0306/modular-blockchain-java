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

    @Test
    void testToString() {
        FinancialTransaction tx = new FinancialTransaction("Alice", "Bob", 25.5);
        assertEquals("Alice -> Bob : $25.5", tx.toString());
    }

    @Test
    void testEquals() {
        FinancialTransaction tx1 = new FinancialTransaction("Alice", "Bob", 10.0);
        FinancialTransaction tx2 = new FinancialTransaction("Alice", "Bob", 10.0);
        FinancialTransaction tx3 = new FinancialTransaction("Alice", "Charlie", 10.0);
        FinancialTransaction tx4 = new FinancialTransaction("Charlie", "Bob", 10.0);
        FinancialTransaction tx5 = new FinancialTransaction("Alice", "Bob", 15.0);

        assertTrue(tx1.equals(tx1));
        assertTrue(tx1.equals(tx2));
        assertFalse(tx1.equals(tx3)); // different receiver
        assertFalse(tx1.equals(tx4)); // different sender
        assertFalse(tx1.equals(tx5)); // different amount
        assertFalse(tx1.equals(null));
        assertFalse(tx1.equals("string"));
    }

    @Test
    void testHashCode() {
        FinancialTransaction tx1 = new FinancialTransaction("Alice", "Bob", 10.0);
        FinancialTransaction tx2 = new FinancialTransaction("Alice", "Bob", 10.0);

        assertEquals(tx1.hashCode(), tx2.hashCode());
    }
}