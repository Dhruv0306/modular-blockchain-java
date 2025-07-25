package com.example.blockchain.core.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.blockchain.core.config.ChainConfig;
import com.example.blockchain.core.config.CustomGenesisBlockFactory;
import com.example.blockchain.core.config.DefaultGenesisBlockFactory;
import com.example.blockchain.core.model.Block;
import com.example.blockchain.core.model.Transaction;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for GenesisBlockFactory implementations.
 * This class tests both DefaultGenesisBlockFactory and
 * CustomGenesisBlockFactory.
 */
class GenesisBlockFactoryTest {

    @BeforeEach
    void setUp() throws Exception {
        // Reset the singleton instance before each test
        resetBlockchainConfigSingleton();
    }

    /**
     * Helper method to reset the BlockchainConfig singleton instance using
     * reflection. This ensures a clean state for each test.
     */
    private void resetBlockchainConfigSingleton() throws Exception {
        Field instance = ChainConfig.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    /**
     * Test the DefaultGenesisBlockFactory to ensure it creates a genesis block
     * with expected default values.
     */
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
        assertEquals(ChainConfig.getInstance().getGenesisHash(), genesisBlock.getHash(),
                "Genesis block hash should match configuration");
        assertTrue(genesisBlock.getTransactions().isEmpty(),
                "Default genesis block should have no transactions");
    }

    /**
     * Test the CustomGenesisBlockFactory with minimal customization (only custom
     * hash).
     */
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

    /**
     * Test the CustomGenesisBlockFactory with custom transactions added
     * individually.
     */
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

    /**
     * Test the CustomGenesisBlockFactory with a list of transactions.
     */
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

    /**
     * Test the CustomGenesisBlockFactory with full customization including
     * custom hash, previous hash, nonce, transactions, and metadata.
     */
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
     * Simple transaction implementation for testing purposes.
     * This class implements the Transaction interface and provides
     * basic transaction functionality.
     */
    private static class TestTransaction implements Transaction {
        private final String sender;
        private final String receiver;
        private final double amount;

        /**
         * Constructs a new TestTransaction.
         *
         * @param sender   The sender of the transaction
         * @param receiver The receiver of the transaction
         * @param amount   The amount of the transaction
         */
        public TestTransaction(String sender, String receiver, double amount) {
            this.sender = sender;
            this.receiver = receiver;
            this.amount = amount;
        }

        @Override
        public boolean isValid() {
            // For testing purposes, all transactions are considered valid
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

        @Override
        public String getTransactionId() {
            return "test-" + sender + "-" + receiver + "-" + amount;
        }

        @Override
        public double getAmount() {
            return amount;
        }

        @Override
        public String getType() {
            return "TEST";
        }

        @Override
        public String getSenderID() {
            return sender;
        }

        @Override
        public String getReceiverID() {
            return receiver;
        }

        @Override
        public Object getHash() {
            return getTransactionId();
        }
    }
}
