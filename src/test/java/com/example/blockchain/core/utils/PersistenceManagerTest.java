package com.example.blockchain.core.utils;

import com.example.blockchain.core.chain.Blockchain;
import com.example.blockchain.logging.BlockchainLoggerFactory;
import com.example.blockchain.transactions.FinancialTransaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for PersistenceManager functionality
 * Tests saving and loading of blockchain data to/from files
 */
class PersistenceManagerTest {

    // Temporary directory for test files
    @TempDir
    Path tempDir;
    
    // Test instance variables
    private Blockchain<FinancialTransaction> blockchain;
    private String testDirectory;
    private String testFilename = "test-chain.json";
    private static final org.slf4j.Logger logger = BlockchainLoggerFactory.getLogger(PersistenceManagerTest.class);
    
    /**
     * Set up test environment before each test
     * Creates a new blockchain and adds a sample transaction
     */
    @BeforeEach
    void setUp() {
        blockchain = new Blockchain<>();
        try {
            blockchain.addTransaction(new FinancialTransaction("Alice", "Bob", 100));
        } catch (NoSuchAlgorithmException e) {
            String error = "Failed to create Transection. \nError: " + e.getMessage();
            logger.error(error, e.getMessage());
            throw new RuntimeException(error, e);
        }
        testDirectory = tempDir.toString();
    }
    
    /**
     * Clean up test files after each test
     */
    @AfterEach
    void tearDown() {
        File file = new File(Path.of(testDirectory, testFilename).toString());
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * Test that saveIfEnabled creates a file and saves blockchain data
     */
    @Test
    void saveIfEnabled_shouldCreateFileAndSaveBlockchain() {
        // Act - Save the blockchain
        PersistenceManager.saveIfEnabled(blockchain, testDirectory, testFilename);
        
        // Assert - Verify file exists and has content
        File savedFile = new File(Path.of(testDirectory, testFilename).toString());
        assertTrue(savedFile.exists());
        assertTrue(savedFile.length() > 0);
    }
    
    /**
     * Test that loadIfConfigured successfully loads a saved blockchain
     */
    @Test
    void loadIfConfigured_shouldLoadSavedBlockchain() throws Exception {
        // Arrange - Save a blockchain first
        PersistenceManager.saveIfEnabled(blockchain, testDirectory, testFilename);
        
        // Act - Load the saved blockchain
        Optional<Blockchain<FinancialTransaction>> loadedBlockchain = 
            PersistenceManager.loadIfConfigured(FinancialTransaction.class, testDirectory, testFilename);
        
        // Assert - Verify loaded blockchain matches original
        assertTrue(loadedBlockchain.isPresent());
        assertEquals(blockchain.getChain().size(), loadedBlockchain.get().getChain().size());
    }
    
    /**
     * Test that loadIfConfigured returns empty Optional when file doesn't exist
     */
    @Test
    void loadIfConfigured_shouldReturnEmptyOptionalWhenFileDoesNotExist() {
        // Act - Try to load non-existent file
        Optional<Blockchain<FinancialTransaction>> result = 
            PersistenceManager.loadIfConfigured(FinancialTransaction.class, testDirectory, "non-existent-file.json");
        
        // Assert - Verify empty Optional is returned
        assertFalse(result.isPresent());
    }
    
    /**
     * Test that saveIfEnabled creates necessary directories
     */
    @Test
    void saveIfEnabled_shouldCreateDirectoriesIfTheyDoNotExist() {
        // Arrange - Create nested directory path
        String nestedDirectory = Path.of(testDirectory, "nested", "dirs").toString();
        
        // Act - Save blockchain to nested directory
        PersistenceManager.saveIfEnabled(blockchain, nestedDirectory, testFilename);
        
        // Assert - Verify file exists in nested directory
        File savedFile = new File(Path.of(nestedDirectory, testFilename).toString());
        assertTrue(savedFile.exists());
    }
    
    /**
     * Test that saveIfEnabled gracefully handles exceptions
     */
    @Test
    void saveIfEnabled_shouldHandleExceptions() {
        // Arrange - Create directory with same name as target file to force exception
        String invalidPath = Path.of(testDirectory, testFilename).toString();
        new File(invalidPath).mkdir();
        
        // Act & Assert - Verify no exception is thrown
        assertDoesNotThrow(() -> 
            PersistenceManager.saveIfEnabled(blockchain, testDirectory, testFilename)
        );
    }
}
