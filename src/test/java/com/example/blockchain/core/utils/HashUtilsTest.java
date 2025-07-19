package com.example.blockchain.core.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Objects;

import org.junit.jupiter.api.Test;

import com.example.blockchain.core.model.Block;
import com.example.blockchain.core.model.Transaction;
import com.example.blockchain.core.utils.HashUtils;
import com.example.blockchain.logging.BlockchainLoggerFactory;

/**
 * Test class for HashUtils functionality
 * Verifies hash computation methods and consistency
 */
class HashUtilsTest {
    private static final org.slf4j.Logger logger = BlockchainLoggerFactory.getLogger(HashUtilsTest.class);

    /**
     * Test that both hash computation methods produce identical results
     * Compares direct block hashing vs component-wise hashing
     */
    @Test
    void testComputeHashConsistency() {
        // Create test block with dummy data
        Block<MockTransaction> block = new Block<>(1, "0", System.currentTimeMillis(), new ArrayList<>(), 0, "dummy");

        // Compute hash using block object
        String hash1;
        // Compute hash using block components
        String hash2;
        try {
            hash1 = HashUtils.computeHash(block);
            hash2 = HashUtils.computeHash(
                    block.getIndex(),
                    block.getPreviousHash(),
                    block.getTimestamp(),
                    block.getTransactions(),
                    block.getNonce());
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            String error = "Failed to compute block hash: " + e.getMessage();
            logger.error(error, e);
            throw new RuntimeException(error, e);
        }

        assertEquals(hash1, hash2, "Hashes from both compute methods should match");
    }

    /**
     * Test that hash computation returns non-null value
     */
    @Test
    void testComputeHashNotNull() {
        // Create test block with dummy data
        Block<MockTransaction> block = new Block<>(2, "abc123", System.currentTimeMillis(), new ArrayList<>(), 5,
                "dummy");
        String hash = HashUtils.computeHash(block);
        assertNotNull(hash);
    }

    /**
     * Mock Transaction class for testing purposes
     * Implements basic Transaction interface functionality
     */
    private static class MockTransaction implements Transaction {
        private final boolean valid;

        /**
         * Constructor for mock transaction
         * 
         * @param valid boolean indicating if transaction is valid
         */
        public MockTransaction(boolean valid) {
            this.valid = valid;
        }

        /**
         * @return validity status of transaction
         */
        public boolean isValid() {
            return valid;
        }

        /**
         * @return mock sender address
         */
        public String getSender() {
            return "sender";
        }

        /**
         * @return mock receiver address
         */
        public String getReceiver() {
            return "receiver";
        }

        /**
         * @return mock transaction summary
         */
        public String getSummary() {
            return "mock transaction";
        }

        /**
         * Equals method for comparing MockTransactions
         * 
         * @param o Object to compare with
         * @return true if objects are equal
         */
        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            MockTransaction that = (MockTransaction) o;
            return valid == that.valid;
        }

        /**
         * @return hash code for MockTransaction
         */
        @Override
        public int hashCode() {
            return Objects.hash(valid);
        }

        /**
         * @return mock transaction ID based on validity and hash
         */
        @Override
        public String getTransactionId() {
            return "mock-utils-" + (valid ? "valid" : "invalid") + "-" + hashCode();
        }

        @Override
        public double getAmount() {
            return 0.0;
        }

        @Override
        public String getType() {
            return "MOCK";
        }

        @Override
        public String getSenderID() {
            return "mock-sender-id";
        }

        @Override
        public String getReceiverID() {
            return "mock-receiver-id";
        }

        @Override
        public Object getHash() {
            return getTransactionId();
        }
    }
}
