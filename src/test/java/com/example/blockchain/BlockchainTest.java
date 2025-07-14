package com.example.blockchain;

import com.example.blockchain.blockchain.Block;
import com.example.blockchain.blockchain.Blockchain;
import com.example.blockchain.blockchain.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class BlockchainTest {

    private Blockchain<MockTransaction> blockchain;

    @BeforeEach
    void setUp() {
        blockchain = new Blockchain<>();
    }

    @Test
    void testAddValidTransaction() {
        MockTransaction validTx = new MockTransaction(true);
        blockchain.addTransaction(validTx);
        assertEquals(1, blockchain.getPendingTransactions().size());
        assertTrue(blockchain.getPendingTransactions().contains(validTx));
    }

    @Test
    void testAddInvalidTransaction() {
        MockTransaction invalidTx = new MockTransaction(false);
        blockchain.addTransaction(invalidTx);
        assertEquals(0, blockchain.getPendingTransactions().size());
    }

    @Test
    void testAddMultipleValidTransactions() {
        MockTransaction tx1 = new MockTransaction(true);
        MockTransaction tx2 = new MockTransaction(true);
        blockchain.addTransaction(tx1);
        blockchain.addTransaction(tx2);
        assertEquals(2, blockchain.getPendingTransactions().size());
    }

    @Test
    void testAddMixedTransactions() {
        MockTransaction validTx = new MockTransaction(true);
        MockTransaction invalidTx = new MockTransaction(false);
        blockchain.addTransaction(validTx);
        blockchain.addTransaction(invalidTx);
        assertEquals(1, blockchain.getPendingTransactions().size());
        assertTrue(blockchain.getPendingTransactions().contains(validTx));
    }

    @Test
    void testGetPendingTransactions() {
        MockTransaction tx = new MockTransaction(true);
        blockchain.addTransaction(tx);
        assertEquals(1, blockchain.getPendingTransactions().size());
        assertEquals(tx, blockchain.getPendingTransactions().get(0));
    }

    @Test
    void testGetLastBlock() {
        Block<MockTransaction> lastBlock = blockchain.getLastBlock();
        assertEquals("GENESIS_HASH", lastBlock.getHash());
        assertEquals(0, lastBlock.getIndex());
    }

    @Test
    void testAddBlock() {
        MockTransaction tx = new MockTransaction(true);
        blockchain.addTransaction(tx);
        assertEquals(1, blockchain.getPendingTransactions().size());
        
        Block<MockTransaction> newBlock = new Block<>(1, "prev", System.currentTimeMillis(), new ArrayList<>(), 0, "hash");
        blockchain.addBlock(newBlock);
        
        assertEquals(2, blockchain.getChain().size());
        assertEquals(0, blockchain.getPendingTransactions().size());
        assertEquals(newBlock, blockchain.getLastBlock());
    }

    @Test
    void testGetChain() {
        assertEquals(1, blockchain.getChain().size());
        assertEquals("GENESIS_HASH", blockchain.getChain().get(0).getHash());
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
    }
}