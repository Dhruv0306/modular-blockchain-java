package com.example.blockchain;

import com.example.blockchain.blockchain.Block;
import com.example.blockchain.blockchain.BlockchainConfig;
import com.example.blockchain.blockchain.CustomGenesisBlockFactory;
import com.example.blockchain.blockchain.DefaultGenesisBlockFactory;
import com.example.blockchain.blockchain.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GenesisBlockFactoryTest {

    @BeforeEach
    void setUp() throws Exception {
        // Reset the singleton instance before each test
        resetBlockchainConfigSingleton();
    }

    /**
     * Helper method to reset the BlockchainConfig singleton instance using reflection
     */
    private void resetBlockchainConfigSingleton() throws Exception {
        Field instance = BlockchainConfig.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    void testDefaultGenesisBlockFactory() {
        // Create a default genesis block factory
        DefaultGenesisBlockFactory<TestTransaction> factory = new DefaultGenesisBlockFactory<>();
        
        // Create a genesis block
        Block<TestTransaction> genesisBlock = factory.createGenesisBlock();
        
        // Verify properties
        assertEquals(0, genesisBlock.getIndex(), "Genesis block should have index 0");
        assertEquals("0", genesisBlock.getPreviousHash(), "Genesis block should have previous hash '0'");
        assertEquals(0, genesisBlock.getNonce(), "Genesis block should have nonce 0");
        assertEquals(BlockchainConfig.getInstance().getGenesisHash(), genesisBlock.getHash(), 
                    "Genesis block hash should match configuration");
        assertTrue(genesisBlock.getTransactions().isEmpty(), 
                  "Default genesis block should have no transactions");
    }

    @Test
    void testCustomGenesisBlockFactoryMinimal() {
        // Create a minimal custom genesis block factory with just a custom hash
        CustomGenesisBlockFactory<TestTransaction> factory = CustomGenesisBlockFactory
                .<TestTransaction>builder()
                .withHash("CUSTOM_HASH")
                .build();
        
        // Create a genesis block
        Block<TestTransaction> genesisBlock = factory.createGenesisBlock();
        
        // Verify properties
        assertEquals(0, genesisBlock.getIndex(), "Genesis block should have index 0");
        assertEquals("0", genesisBlock.getPreviousHash(), "Genesis block should have previous hash '0'");
        assertEquals(0, genesisBlock.getNonce(), "Genesis block should have nonce 0");
        assertEquals("CUSTOM_HASH", genesisBlock.getHash(), "Genesis block should have custom hash");
        assertTrue(genesisBlock.getTransactions().isEmpty(), 
                  "Genesis block should have no transactions if none specified");
    }

    @Test
    void testCustomGenesisBlockFactoryWithTransactions() {
        // Create test transactions
        TestTransaction tx1 = new TestTransaction("Alice", "Bob", 1000.0);
        TestTransaction tx2 = new TestTransaction("Genesis", "Alice", 5000.0);
        
        // Create a custom genesis block factory with transactions
        CustomGenesisBlockFactory<TestTransaction> factory = CustomGenesisBlockFactory
                .<TestTransaction>builder()
                .withHash("GENESIS_WITH_TX")
                .addTransaction(tx1)
                .addTransaction(tx2)
                .build();
        
        // Create a genesis block
        Block<TestTransaction> genesisBlock = factory.createGenesisBlock();
        
        // Verify properties
        assertEquals(0, genesisBlock.getIndex(), "Genesis block should have index 0");
        assertEquals("GENESIS_WITH_TX", genesisBlock.getHash(), "Genesis block should have custom hash");
        assertEquals(2, genesisBlock.getTransactions().size(), "Genesis block should have 2 transactions");
        assertEquals(tx1, genesisBlock.getTransactions().get(0), "First transaction should match");
        assertEquals(tx2, genesisBlock.getTransactions().get(1), "Second transaction should match");
    }

    @Test
    void testCustomGenesisBlockFactoryWithTransactionList() {
        // Create test transactions
        TestTransaction tx1 = new TestTransaction("Alice", "Bob", 1000.0);
        TestTransaction tx2 = new TestTransaction("Genesis", "Alice", 5000.0);
        List<TestTransaction> transactions = Arrays.asList(tx1, tx2);
        
        // Create a custom genesis block factory with a transaction list
        CustomGenesisBlockFactory<TestTransaction> factory = CustomGenesisBlockFactory
                .<TestTransaction>builder()
                .withHash("GENESIS_WITH_TX_LIST")
                .withTransactions(transactions)
                .build();
        
        // Create a genesis block
        Block<TestTransaction> genesisBlock = factory.createGenesisBlock();
        
        // Verify properties
        assertEquals(2, genesisBlock.getTransactions().size(), "Genesis block should have 2 transactions");
        assertEquals(tx1, genesisBlock.getTransactions().get(0), "First transaction should match");
        assertEquals(tx2, genesisBlock.getTransactions().get(1), "Second transaction should match");
    }

    @Test
    void testCustomGenesisBlockFactoryFullyCustomized() {
        // Create test transactions
        TestTransaction tx = new TestTransaction("System", "Alice", 1000.0);
        
        // Create a fully customized genesis block factory
        CustomGenesisBlockFactory<TestTransaction> factory = CustomGenesisBlockFactory
                .<TestTransaction>builder()
                .withHash("FULLY_CUSTOM")
                .withPreviousHash("NONE")
                .withNonce(42)
                .addTransaction(tx)
                .withMetadata("creator", "Test")
                .withMetadata("timestamp", 1234567890L)
                .build();
        
        // Create a genesis block
        Block<TestTransaction> genesisBlock = factory.createGenesisBlock();
        
        // Verify properties
        assertEquals(0, genesisBlock.getIndex(), "Genesis block should have index 0");
        assertEquals("NONE", genesisBlock.getPreviousHash(), "Genesis block should have custom previous hash");
        assertEquals(42, genesisBlock.getNonce(), "Genesis block should have custom nonce");
        assertEquals("FULLY_CUSTOM", genesisBlock.getHash(), "Genesis block should have custom hash");
        assertEquals(1, genesisBlock.getTransactions().size(), "Genesis block should have 1 transaction");
        
        // Verify metadata
        Map<String, Object> metadata = factory.getMetadata();
        assertEquals("Test", metadata.get("creator"), "Metadata should contain creator");
        assertEquals(1234567890L, metadata.get("timestamp"), "Metadata should contain timestamp");
    }

    /**
     * Simple transaction implementation for testing
     */
    private static class TestTransaction implements Transaction {
        private final String sender;
        private final String receiver;
        private final double amount;

        public TestTransaction(String sender, String receiver, double amount) {
            this.sender = sender;
            this.receiver = receiver;
            this.amount = amount;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public String getSender() {
            return sender;
        }

        @Override
        public String getReceiver() {
            return receiver;
        }

        @Override
        public String getSummary() {
            return sender + " -> " + receiver + ": " + amount;
        }
    }
} 