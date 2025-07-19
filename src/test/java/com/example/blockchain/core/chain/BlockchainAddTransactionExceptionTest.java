package com.example.blockchain.core.chain;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.Test;

import com.example.blockchain.core.model.Transaction;

/**
 * Tests for the exception handling in the addTransaction method of the Blockchain class.
 */
public class BlockchainAddTransactionExceptionTest {

    /**
     * Test that verifies a RuntimeException is thrown when a transaction's isValid method
     * throws a NoSuchAlgorithmException.
     */
    @Test
    public void testAddTransactionNoSuchAlgorithmException() {
        // Create a new blockchain
        Blockchain<Transaction> blockchain = new Blockchain<>();
        
        // Create a mock transaction that throws NoSuchAlgorithmException when isValid is called
        Transaction mockTransaction = mock(Transaction.class);
        try {
            when(mockTransaction.isValid()).thenThrow(new NoSuchAlgorithmException("Test exception"));
        } catch (NoSuchAlgorithmException e) {
            // This won't happen during mock setup
        }
        
        // Verify that a RuntimeException is thrown when adding the transaction
        assertThrows(RuntimeException.class, () -> {
            blockchain.addTransaction(mockTransaction);
        });
    }
}