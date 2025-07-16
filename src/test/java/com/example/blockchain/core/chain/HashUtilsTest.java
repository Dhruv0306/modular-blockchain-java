package com.example.blockchain.core.chain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.Objects;

import org.junit.jupiter.api.Test;

import com.example.blockchain.core.chain.HashUtils;
import com.example.blockchain.core.model.Block;
import com.example.blockchain.core.model.Transaction;

class HashUtilsTest {

    @Test
    void testComputeHashConsistency() {
        Block<MockTransaction> block = new Block<>(1, "0", System.currentTimeMillis(), new ArrayList<>(), 0, "dummy");
        String hash1 = HashUtils.computeHash(block);
        String hash2 = HashUtils.computeHash(
                block.getIndex(),
                block.getPreviousHash(),
                block.getTimestamp(),
                block.getTransactions(),
                block.getNonce());
        assertEquals(hash1, hash2, "Hashes from both compute methods should match");
    }

    @Test
    void testComputeHashNotNull() {
        Block<MockTransaction> block = new Block<>(2, "abc123", System.currentTimeMillis(), new ArrayList<>(), 5,
                "dummy");
        String hash = HashUtils.computeHash(block);
        assertNotNull(hash);
    }

    private static class MockTransaction implements Transaction {
        private final boolean valid;

        public MockTransaction(boolean valid) {
            this.valid = valid;
        }

        public boolean isValid() {
            return valid;
        }

        public String getSender() {
            return "sender";
        }

        public String getReceiver() {
            return "receiver";
        }

        public String getSummary() {
            return "mock transaction";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            MockTransaction that = (MockTransaction) o;
            return valid == that.valid;
        }

        @Override
        public int hashCode() {
            return Objects.hash(valid);
        }

        @Override
        public String getTransactionId() {
            return "mock-utils-" + (valid ? "valid" : "invalid") + "-" + hashCode();
        }
    }
}
