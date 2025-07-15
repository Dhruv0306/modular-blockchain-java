package com.example.blockchain;

import com.example.blockchain.blockchain.Block;
import com.example.blockchain.blockchain.Transaction;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class BlockTest {

    @Test
    void testEqualsWithSameObject() {
        Block<MockTransaction> block = createTestBlock();
        assertEquals(block, block);
    }

    @Test
    void testEqualsWithNull() {
        Block<MockTransaction> block = createTestBlock();
        assertNotEquals(block, null);
    }

    @Test
    void testEqualsWithDifferentClass() {
        Block<MockTransaction> block = createTestBlock();
        assertNotEquals(block, "not a block");
    }

    @Test
    void testEqualsWithIdenticalBlocks() {
        Block<MockTransaction> block1 = createTestBlock();
        Block<MockTransaction> block2 = new Block<>(1, "prev", 123456L, new ArrayList<>(), 42, "hash");
        assertEquals(block1, block2);
    }

    @Test
    void testEqualsWithDifferentIndex() {
        Block<MockTransaction> block1 = createTestBlock();
        Block<MockTransaction> block2 = new Block<>(2, "prev", 123456L, new ArrayList<>(), 42, "hash");
        assertNotEquals(block1, block2);
    }

    @Test
    void testEqualsWithDifferentPreviousHash() {
        Block<MockTransaction> block1 = createTestBlock();
        Block<MockTransaction> block2 = new Block<>(1, "different", 123456L, new ArrayList<>(), 42, "hash");
        assertNotEquals(block1, block2);
    }

    @Test
    void testEqualsWithDifferentTimestamp() {
        Block<MockTransaction> block1 = createTestBlock();
        Block<MockTransaction> block2 = new Block<>(1, "prev", 999999L, new ArrayList<>(), 42, "hash");
        assertNotEquals(block1, block2);
    }

    @Test
    void testEqualsWithDifferentNonce() {
        Block<MockTransaction> block1 = createTestBlock();
        Block<MockTransaction> block2 = new Block<>(1, "prev", 123456L, new ArrayList<>(), 99, "hash");
        assertNotEquals(block1, block2);
    }

    @Test
    void testEqualsWithDifferentHash() {
        Block<MockTransaction> block1 = createTestBlock();
        Block<MockTransaction> block2 = new Block<>(1, "prev", 123456L, new ArrayList<>(), 42, "different");
        assertNotEquals(block1, block2);
    }

    @Test
    void testHashCodeConsistency() {
        Block<MockTransaction> block = createTestBlock();
        int hash1 = block.hashCode();
        int hash2 = block.hashCode();
        assertEquals(hash1, hash2);
    }

    @Test
    void testHashCodeEqualObjects() {
        Block<MockTransaction> block1 = createTestBlock();
        Block<MockTransaction> block2 = new Block<>(1, "prev", 123456L, new ArrayList<>(), 42, "hash");
        assertEquals(block1.hashCode(), block2.hashCode());
    }

    @Test
    void testHashCodeDifferentObjects() {
        Block<MockTransaction> block1 = createTestBlock();
        Block<MockTransaction> block2 = new Block<>(2, "prev", 123456L, new ArrayList<>(), 42, "hash");
        assertNotEquals(block1.hashCode(), block2.hashCode());
    }

    private Block<MockTransaction> createTestBlock() {
        return new Block<>(1, "prev", 123456L, new ArrayList<>(), 42, "hash");
    }

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
    }
}