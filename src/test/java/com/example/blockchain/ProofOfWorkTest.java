package com.example.blockchain;

import com.example.blockchain.blockchain.Block;
import com.example.blockchain.blockchain.Transaction;
import com.example.blockchain.consensus.ProofOfWork;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
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
        Block<MockTransaction> invalidBlock = new Block<>(1, "wrong", System.currentTimeMillis(), new ArrayList<>(), 0, "0000hash");
        assertFalse(pow.validateBlock(invalidBlock, genesisBlock));
    }

    @Test
    void testValidateBlockInvalidHash() {
        Block<MockTransaction> invalidBlock = new Block<>(1, genesisBlock.getHash(), System.currentTimeMillis(), new ArrayList<>(), 0, "wronghash");
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