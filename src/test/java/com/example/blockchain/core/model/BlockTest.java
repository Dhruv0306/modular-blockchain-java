package com.example.blockchain.core.model;

import org.junit.jupiter.api.Test;

import com.example.blockchain.core.model.Block;
import com.example.blockchain.core.model.Transaction;

import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Block functionality
 * Tests equals() and hashCode() implementations
 */
class BlockTest {

    /**
     * Test that a block equals itself
     */
    @Test
    void testEqualsWithSameObject() {
        Block<MockTransaction> block = createTestBlock();
        assertEquals(block, block);
    }

    /**
     * Test that a block is not equal to null
     */
    @Test
    void testEqualsWithNull() {
        Block<MockTransaction> block = createTestBlock();
        assertNotEquals(block, null);
    }

    /**
     * Test that a block is not equal to an object of a different class
     */
    @Test
    void testEqualsWithDifferentClass() {
        Block<MockTransaction> block = createTestBlock();
        assertNotEquals(block, "not a block");
    }

    /**
     * Test that two blocks with identical properties are equal
     */
    @Test
    void testEqualsWithIdenticalBlocks() {
        Block<MockTransaction> block1 = createTestBlock();
        Block<MockTransaction> block2 = new Block<>(1, "prev", 123456L, new ArrayList<>(), 42, "hash");
        assertEquals(block1, block2);
    }

    /**
     * Test that blocks with different indices are not equal
     */
    @Test
    void testEqualsWithDifferentIndex() {
        Block<MockTransaction> block1 = createTestBlock();
        Block<MockTransaction> block2 = new Block<>(2, "prev", 123456L, new ArrayList<>(), 42, "hash");
        assertNotEquals(block1, block2);
    }

    /**
     * Test that blocks with different previous hashes are not equal
     */
    @Test
    void testEqualsWithDifferentPreviousHash() {
        Block<MockTransaction> block1 = createTestBlock();
        Block<MockTransaction> block2 = new Block<>(1, "different", 123456L, new ArrayList<>(), 42, "hash");
        assertNotEquals(block1, block2);
    }

    /**
     * Test that blocks with different timestamps are not equal
     */
    @Test
    void testEqualsWithDifferentTimestamp() {
        Block<MockTransaction> block1 = createTestBlock();
        Block<MockTransaction> block2 = new Block<>(1, "prev", 999999L, new ArrayList<>(), 42, "hash");
        assertNotEquals(block1, block2);
    }

    /**
     * Test that blocks with different nonce values are not equal
     */
    @Test
    void testEqualsWithDifferentNonce() {
        Block<MockTransaction> block1 = createTestBlock();
        Block<MockTransaction> block2 = new Block<>(1, "prev", 123456L, new ArrayList<>(), 99, "hash");
        assertNotEquals(block1, block2);
    }

    /**
     * Test that blocks with different hashes are not equal
     */
    @Test
    void testEqualsWithDifferentHash() {
        Block<MockTransaction> block1 = createTestBlock();
        Block<MockTransaction> block2 = new Block<>(1, "prev", 123456L, new ArrayList<>(), 42, "different");
        assertNotEquals(block1, block2);
    }

    /**
     * Test that hashCode returns consistent results for the same block
     */
    @Test
    void testHashCodeConsistency() {
        Block<MockTransaction> block = createTestBlock();
        int hash1 = block.hashCode();
        int hash2 = block.hashCode();
        assertEquals(hash1, hash2);
    }

    /**
     * Test that equal blocks have equal hash codes
     */
    @Test
    void testHashCodeEqualObjects() {
        Block<MockTransaction> block1 = createTestBlock();
        Block<MockTransaction> block2 = new Block<>(1, "prev", 123456L, new ArrayList<>(), 42, "hash");
        assertEquals(block1.hashCode(), block2.hashCode());
    }

    /**
     * Test that different blocks have different hash codes
     */
    @Test
    void testHashCodeDifferentObjects() {
        Block<MockTransaction> block1 = createTestBlock();
        Block<MockTransaction> block2 = new Block<>(2, "prev", 123456L, new ArrayList<>(), 42, "hash");
        assertNotEquals(block1.hashCode(), block2.hashCode());
    }

    /**
     * Helper method to create a test block with predefined values
     * @return A Block instance with mock transaction
     */
    private Block<MockTransaction> createTestBlock() {
        return new Block<>(1, "prev", 123456L, new ArrayList<>(), 42, "hash");
    }

    /**
     * Mock implementation of Transaction interface for testing
     */
    private static class MockTransaction implements Transaction {
        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public String getSender() {
            return "sender";
        }

        @Override
        public String getReceiver() {
            return "receiver";
        }

        @Override
        public String getSummary() {
            return "mock";
        }

        @Override
        public String getTransactionId() {
            return "mock-test-" + hashCode();
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
            return "MOCK-SENDER-ID";
        }

        @Override
        public String getReceiverID() {
            return "MOCK-RECEIVER-ID"; 
        }

        @Override
        public Object getHash() {
            return getTransactionId();
        }
    }
}
