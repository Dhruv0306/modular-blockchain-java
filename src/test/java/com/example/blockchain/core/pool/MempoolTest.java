package com.example.blockchain.core.pool;

import com.example.blockchain.core.model.Transaction;
import com.example.blockchain.logging.BlockchainLoggerFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class MempoolTest {

    private Mempool mempool;
    private Transaction validTransaction;
    private Transaction invalidTransaction;
    private Transaction duplicateTransaction;
    private static final org.slf4j.Logger logger = BlockchainLoggerFactory.getLogger(MempoolTest.class);

    @BeforeEach
    void setUp() {
        mempool = new Mempool();
        
        // Create a valid transaction mock
        validTransaction = Mockito.mock(Transaction.class);
        when(validTransaction.getHash()).thenReturn("valid_hash");
        try {
            when(validTransaction.isValid()).thenReturn(true);
        } catch (NoSuchAlgorithmException e) {
            String error = "Failed To varify Transection. \nError: " + e.getMessage();
            logger.error(error, e);
            throw new RuntimeException(error, e);
        }
        
        // Create an invalid transaction mock
        invalidTransaction = Mockito.mock(Transaction.class);
        when(invalidTransaction.getHash()).thenReturn("invalid_hash");
        try {
            when(invalidTransaction.isValid()).thenReturn(false);
        } catch (NoSuchAlgorithmException e) {
            String error = "Failed To varify Transection. \nError: " + e.getMessage();
            logger.error(error, e);
            throw new RuntimeException(error, e);
        }
        
        // Create a duplicate transaction mock with same hash as valid transaction
        duplicateTransaction = Mockito.mock(Transaction.class);
        when(duplicateTransaction.getHash()).thenReturn("valid_hash");
        try {
            when(duplicateTransaction.isValid()).thenReturn(true);
        } catch (NoSuchAlgorithmException e) {
            String error = "Failed To varify Transection. \nError: " + e.getMessage();
            logger.error(error, e);
            throw new RuntimeException(error, e);
        }
    }

    @Test
    void addTransaction_ValidTransaction_ReturnsTrue() {
        // Act
        boolean result = mempool.addTransaction(validTransaction);
        
        // Assert
        assertTrue(result);
        assertEquals(1, mempool.size());
        assertTrue(mempool.contains("valid_hash"));
    }

    @Test
    void addTransaction_InvalidTransaction_ReturnsFalse() {
        // Act
        boolean result = mempool.addTransaction(invalidTransaction);
        
        // Assert
        assertFalse(result);
        assertEquals(0, mempool.size());
        assertFalse(mempool.contains("invalid_hash"));
    }

    @Test
    void addTransaction_DuplicateTransaction_ReturnsFalse() {
        // Arrange
        mempool.addTransaction(validTransaction);
        
        // Act
        boolean result = mempool.addTransaction(duplicateTransaction);
        
        // Assert
        assertFalse(result);
        assertEquals(1, mempool.size());
    }
    
    @Test
    void removeTransaction_ExistingTransaction_ReturnsTrue() {
        // Arrange
        mempool.addTransaction(validTransaction);
        
        // Act
        boolean result = mempool.removeTransaction("valid_hash");
        
        // Assert
        assertTrue(result);
        assertEquals(0, mempool.size());
        assertFalse(mempool.contains("valid_hash"));
    }
    
    @Test
    void removeTransaction_NonExistingTransaction_ReturnsFalse() {
        // Act
        boolean result = mempool.removeTransaction("non_existing_hash");
        
        // Assert
        assertFalse(result);
        assertEquals(0, mempool.size());
    }
    
    @Test
    void contains_ExistingTransaction_ReturnsTrue() {
        // Arrange
        mempool.addTransaction(validTransaction);
        
        // Act
        boolean result = mempool.contains("valid_hash");
        
        // Assert
        assertTrue(result);
    }
    
    @Test
    void contains_NonExistingTransaction_ReturnsFalse() {
        // Act
        boolean result = mempool.contains("non_existing_hash");
        
        // Assert
        assertFalse(result);
    }
    
    @Test
    void size_EmptyMempool_ReturnsZero() {
        // Act
        int result = mempool.size();
        
        // Assert
        assertEquals(0, result);
    }
    
    @Test
    void size_WithTransactions_ReturnsCorrectCount() {
        // Arrange
        mempool.addTransaction(validTransaction);
        
        // Create another valid transaction with different hash
        Transaction anotherTransaction = Mockito.mock(Transaction.class);
        when(anotherTransaction.getHash()).thenReturn("another_hash");
        try {
            when(anotherTransaction.isValid()).thenReturn(true);
        } catch (NoSuchAlgorithmException e) {
            String error = "Failed To varify Transection. \nError: " + e.getMessage();
            logger.error(error, e);
            throw new RuntimeException(error, e);
        }
        mempool.addTransaction(anotherTransaction);
        
        // Act
        int result = mempool.size();
        
        // Assert
        assertEquals(2, result);
    }
    
    @Test
    void removeAllTransactions_RemovesSpecifiedTransactions() {
        // Arrange
        mempool.addTransaction(validTransaction);
        
        // Create another valid transaction with different hash
        Transaction anotherTransaction = Mockito.mock(Transaction.class);
        when(anotherTransaction.getHash()).thenReturn("another_hash");
        try {
            when(anotherTransaction.isValid()).thenReturn(true);
        } catch (NoSuchAlgorithmException e) {
            String error = "Failed To varify Transection. \nError: " + e.getMessage();
            logger.error(error, e);
            throw new RuntimeException(error, e);
        }
        mempool.addTransaction(anotherTransaction);
        
        // Create a third transaction that won't be removed
        Transaction thirdTransaction = Mockito.mock(Transaction.class);
        when(thirdTransaction.getHash()).thenReturn("third_hash");
        try {
            when(thirdTransaction.isValid()).thenReturn(true);
        } catch (NoSuchAlgorithmException e) {
            String error = "Failed To varify Transection. \nError: " + e.getMessage();
            logger.error(error, e);
            throw new RuntimeException(error, e);
        }
        mempool.addTransaction(thirdTransaction);
        
        // Create list of transactions to remove
        List<Transaction> transactionsToRemove = Arrays.asList(validTransaction, anotherTransaction);
        
        // Act
        mempool.removeAllTransactions(transactionsToRemove);
        
        // Assert
        assertEquals(1, mempool.size());
        assertFalse(mempool.contains("valid_hash"));
        assertFalse(mempool.contains("another_hash"));
        assertTrue(mempool.contains("third_hash"));
    }
    
    @Test
    void removeAllTransactions_EmptyList_DoesNothing() {
        // Arrange
        mempool.addTransaction(validTransaction);
        List<Transaction> emptyList = Collections.emptyList();
        
        // Act
        mempool.removeAllTransactions(emptyList);
        
        // Assert
        assertEquals(1, mempool.size());
        assertTrue(mempool.contains("valid_hash"));
    }
}