package com.example.blockchain.consensus;

import com.example.blockchain.consensus.ProofOfWork;
import com.example.blockchain.core.chain.HashUtils;
import com.example.blockchain.core.model.Block;
import com.example.blockchain.core.model.Transaction;

import java.lang.reflect.Method;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import static org.junit.jupiter.api.Assertions.*;

public class ProofOfWorkTest {

    private ProofOfWork<MockTransaction> pow;
    private Block<MockTransaction> genesisBlock;

    @BeforeEach
    void setUp() {
        pow = new ProofOfWork<>();
        genesisBlock = new Block<>(0, "0", System.currentTimeMillis(), new ArrayList<>(), 0, "0000genesis");
    }

    @Test
    void testValidateBlockValid() {
        List<MockTransaction> txs = new ArrayList<>();
        Block<MockTransaction> validBlock = pow.generateBlock(txs, genesisBlock);
        assertTrue(pow.validateBlock(validBlock, genesisBlock));
    }

    @Test
    void testValidateBlockInvalidPreviousHash() {
        Block<MockTransaction> invalidBlock = new Block<>(1, "wrong", System.currentTimeMillis(), new ArrayList<>(), 0,
                "0000hash");
        assertFalse(pow.validateBlock(invalidBlock, genesisBlock));
    }

    @Test
    void testValidateBlockInvalidHash() {
        Block<MockTransaction> invalidBlock = new Block<>(1, genesisBlock.getHash(), System.currentTimeMillis(),
                new ArrayList<>(), 0, "wronghash");
        assertFalse(pow.validateBlock(invalidBlock, genesisBlock));
    }

    @Test
    void testGenerateBlock() {
        List<MockTransaction> txs = new ArrayList<>();
        txs.add(new MockTransaction(true));
        Block<MockTransaction> newBlock = pow.generateBlock(txs, genesisBlock);

        assertEquals(1, newBlock.getIndex());
        assertEquals(genesisBlock.getHash(), newBlock.getPreviousHash());
        assertTrue(newBlock.getHash().startsWith("0000"));
        assertEquals(txs, newBlock.getTransactions());
    }

    @Test
    void testInvalidDifficulty() {
        // Create a block with insufficient difficulty (only 3 leading zeros instead of
        // 4)
        // The default difficulty is 4, so a hash with only 3 leading zeros should be
        // invalid
        Block<MockTransaction> invalidBlock = new Block<>(
                1,
                genesisBlock.getHash(),
                System.currentTimeMillis(),
                new ArrayList<>(),
                123,
                "000abcdefghijklmnopqrstuvwxyz" // Only 3 leading zeros, not 4
        );

        // Validation should fail due to insufficient difficulty
        assertFalse(pow.validateBlock(invalidBlock, genesisBlock));
    }

    @Test
    void testTamperedBlock() {
        List<MockTransaction> txs = new ArrayList<>();
        txs.add(new MockTransaction(true));
        Block<MockTransaction> newBlock = pow.generateBlock(txs, genesisBlock);

        // Create a tampered copy of the block with modified transaction but same hash
        List<MockTransaction> tamperedTxs = new ArrayList<>();
        tamperedTxs.add(new MockTransaction(false)); // Different transaction
        Block<MockTransaction> tamperedBlock = new Block<>(
                newBlock.getIndex(),
                newBlock.getPreviousHash(),
                newBlock.getTimestamp(),
                tamperedTxs, // Modified transaction list
                newBlock.getNonce(),
                newBlock.getHash() // Same hash as original, but should be different due to transaction change
        );

        // Validation should fail because the hash doesn't match the block contents
        assertFalse(pow.validateBlock(tamperedBlock, genesisBlock));
    }

    @Test
    void testComputeHashError() {
        ProofOfWork<Transaction> powGeneric = new ProofOfWork<>();
        Block<Transaction> genesisBlockGeneric = new Block<>(0, "0", System.currentTimeMillis(), new ArrayList<>(), 0,
                "0000genesis");

        List<Transaction> txs = new ArrayList<>();
        txs.add(new ThrowingMockTransaction());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            powGeneric.generateBlock(txs, genesisBlockGeneric);
        });

        assertNotNull(exception.getCause());
    }

    @Test
    void testValidHashButInvalidDifficulty() throws Exception {
        // Create block parameters
        int index = 1;
        String prevHash = genesisBlock.getHash();
        long timestamp = System.currentTimeMillis();
        List<MockTransaction> txs = new ArrayList<>();
        int nonce = 123;

        // Use reflection to compute the correct hash
        Method computeHashMethod = HashUtils.class.getDeclaredMethod("computeHash", int.class, String.class,
                long.class, List.class, int.class);
        computeHashMethod.setAccessible(true);
        String correctHash = (String) computeHashMethod.invoke(pow, index, prevHash, timestamp, txs, nonce);

        // Ensure the hash doesn't meet difficulty (doesn't start with 0000)
        // If it accidentally does, modify the nonce until we get one that doesn't
        while (correctHash.startsWith("0000")) {
            nonce++;
            correctHash = (String) computeHashMethod.invoke(pow, index, prevHash, timestamp, txs, nonce);
        }

        // Create block with correct hash but insufficient difficulty
        Block<MockTransaction> blockWithBadDifficulty = new Block<>(index, prevHash, timestamp, txs, nonce,
                correctHash);

        // This should fail validation due to difficulty, not hash mismatch
        assertFalse(pow.validateBlock(blockWithBadDifficulty, genesisBlock));
    }

    @Test
    void testComputeHashWithNullTransactions() throws Exception {
        Method computeHashMethod = HashUtils.class.getDeclaredMethod("computeHash", int.class, String.class,
                long.class, List.class, int.class);
        computeHashMethod.setAccessible(true);
        String hash = (String) computeHashMethod.invoke(pow, 1, "prevHash", 123L, null, 0);
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
            return "mock-" + (valid ? "valid" : "invalid") + "-" + hashCode();
        }
    }

    private static class ThrowingMockTransaction implements Transaction {
        public boolean isValid() {
            throw new RuntimeException("Test exception");
        }

        public String getSender() {
            return "sender";
        }

        public String getReceiver() {
            return "receiver";
        }

        public String getSummary() {
            return "throwing transaction";
        }

        @Override
        public String getTransactionId() {
            return "throwing-mock-transaction";
        }
    }
}