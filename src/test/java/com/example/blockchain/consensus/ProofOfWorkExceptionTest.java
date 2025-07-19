package com.example.blockchain.consensus;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.example.blockchain.core.config.ChainConfig;
import com.example.blockchain.core.model.Block;
import com.example.blockchain.core.utils.HashUtils;
import com.example.blockchain.transactions.FinancialTransaction;

/**
 * Tests for exception handling in the ProofOfWork class.
 */
public class ProofOfWorkExceptionTest {

    @Test
    public void testNoSuchAlgorithmException() {
        // Create test data
        ProofOfWork<FinancialTransaction> pow = new ProofOfWork<>();
        List<FinancialTransaction> transactions = new ArrayList<>();
        Block<FinancialTransaction> previousBlock = new Block<>(0, "genesis", 1000L, new ArrayList<>(), 0, "hash");
        
        // Mock ChainConfig to return a fixed difficulty
        ChainConfig mockConfig = ChainConfig.getInstance();
        
        try (MockedStatic<ChainConfig> mockedConfig = mockStatic(ChainConfig.class);
             MockedStatic<HashUtils> mockedHashUtils = mockStatic(HashUtils.class)) {
            
            // Configure mocks
            mockedConfig.when(ChainConfig::getInstance).thenReturn(mockConfig);
            
            // Mock HashUtils to throw NoSuchAlgorithmException
            mockedHashUtils.when(() -> HashUtils.computeHash(
                anyInt(), anyString(), anyLong(), anyList(), anyInt()))
                .thenThrow(new NoSuchAlgorithmException("Test exception"));
            
            // Verify that RuntimeException is thrown
            assertThrows(RuntimeException.class, () -> {
                pow.generateBlock(transactions, previousBlock);
            });
        }
    }
    
    @Test
    public void testUnsupportedEncodingException() {
        // Create test data
        ProofOfWork<FinancialTransaction> pow = new ProofOfWork<>();
        List<FinancialTransaction> transactions = new ArrayList<>();
        Block<FinancialTransaction> previousBlock = new Block<>(0, "genesis", 1000L, new ArrayList<>(), 0, "hash");
        
        // Mock ChainConfig to return a fixed difficulty
        ChainConfig mockConfig = ChainConfig.getInstance();
        
        try (MockedStatic<ChainConfig> mockedConfig = mockStatic(ChainConfig.class);
             MockedStatic<HashUtils> mockedHashUtils = mockStatic(HashUtils.class)) {
            
            // Configure mocks
            mockedConfig.when(ChainConfig::getInstance).thenReturn(mockConfig);
            
            // Mock HashUtils to throw UnsupportedEncodingException
            mockedHashUtils.when(() -> HashUtils.computeHash(
                anyInt(), anyString(), anyLong(), anyList(), anyInt()))
                .thenThrow(new UnsupportedEncodingException("Test exception"));
            
            // Verify that RuntimeException is thrown
            assertThrows(RuntimeException.class, () -> {
                pow.generateBlock(transactions, previousBlock);
            });
        }
    }
}