package com.example.blockchain;

import com.example.blockchain.blockchain.Block;
import com.example.blockchain.blockchain.Blockchain;
import com.example.blockchain.blockchain.DefaultGenesisBlockFactory;
import com.example.blockchain.blockchain.Transaction;
import com.example.blockchain.consensus.Consensus;
import com.example.blockchain.consensus.ProofOfWork;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import static org.junit.jupiter.api.Assertions.*;

public class BlockchainTest {

    private Blockchain<MockTransaction> blockchain;
    private ProofOfWork<MockTransaction> pow;

    @BeforeEach
    void setUp() {
        pow = new ProofOfWork<>();
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
    
    @Test
    void testDefaultConstructor() {
        Blockchain<MockTransaction> defaultBlockchain = new Blockchain<>();
        assertEquals(1, defaultBlockchain.getChain().size());
        assertEquals("GENESIS_HASH", defaultBlockchain.getLastBlock().getHash());
        assertEquals(0, defaultBlockchain.getPendingTransactions().size());
    }
    
    @Test
    void testSingleParameterConstructor() {
        Blockchain<MockTransaction> blockchain = new Blockchain<>(new DefaultGenesisBlockFactory<>());
        assertEquals(1, blockchain.getChain().size());
        assertEquals("GENESIS_HASH", blockchain.getLastBlock().getHash());
        assertEquals(0, blockchain.getPendingTransactions().size());
    }
    
    @Test
    void testIsValidChainWithValidChain() {
        // A blockchain with just the genesis block should be valid
        assertTrue(blockchain.isValidChain());
        
        // Add a new valid block
        List<MockTransaction> txs = new ArrayList<>();
        txs.add(new MockTransaction(true));
        Block<MockTransaction> validBlock = pow.generateBlock(txs, blockchain.getLastBlock());
        blockchain.addBlock(validBlock);
        
        // Chain should still be valid
        assertTrue(blockchain.isValidChain());
    }
    
    @Test
    void testIsValidChainWithTamperedBlock() {
        // Create a valid chain with two blocks
        List<MockTransaction> txs = new ArrayList<>();
        txs.add(new MockTransaction(true));
        Block<MockTransaction> validBlock = pow.generateBlock(txs, blockchain.getLastBlock());
        blockchain.addBlock(validBlock);
        
        // Chain should be valid
        assertTrue(blockchain.isValidChain());
        
        // Now tamper with the transaction in the second block
        List<Block<MockTransaction>> chain = blockchain.getChain();
        Block<MockTransaction> originalBlock = chain.get(1);
        
        // Create a tampered block with modified transactions but the same hash
        List<MockTransaction> tamperedTxs = new ArrayList<>();
        tamperedTxs.add(new MockTransaction(false)); // Different transaction
        Block<MockTransaction> tamperedBlock = new Block<>(
            originalBlock.getIndex(),
            originalBlock.getPreviousHash(),
            originalBlock.getTimestamp(),
            tamperedTxs,
            originalBlock.getNonce(),
            originalBlock.getHash() // Same hash, but should be different due to transaction change
        );
        
        // Replace the original block with the tampered block
        chain.set(1, tamperedBlock);
        
        // Chain should now be invalid
        assertFalse(blockchain.isValidChain());
    }
    
    @Test
    void testIsValidChainWithNonSequentialIndex() {
        // Create a blockchain with a mock consensus that always validates blocks
        Consensus<MockTransaction> mockConsensus = new Consensus<MockTransaction>() {
            @Override
            public Block<MockTransaction> generateBlock(List<MockTransaction> transactions, Block<MockTransaction> previousBlock) {
                return new Block<>(previousBlock.getIndex() + 1, previousBlock.getHash(), 
                    System.currentTimeMillis(), transactions, 0, "valid_hash");
            }
            
            @Override
            public boolean validateBlock(Block<MockTransaction> newBlock, Block<MockTransaction> previousBlock) {
                return true; // Always return true to bypass consensus validation
            }
        };
        
        // Create blockchain with mock consensus
        Blockchain<MockTransaction> testBlockchain = new Blockchain<>(new DefaultGenesisBlockFactory<>(), mockConsensus);
        
        // Add a block with non-sequential index (should be 1, but we set it to 5)
        Block<MockTransaction> nonSequentialBlock = new Block<>(
            5, // Should be 1, but we're setting it to 5
            testBlockchain.getLastBlock().getHash(),
            System.currentTimeMillis(),
            new ArrayList<>(),
            0,
            "valid_hash"
        );
        
        testBlockchain.addBlock(nonSequentialBlock);
        
        // Chain should be invalid due to non-sequential index
        assertFalse(testBlockchain.isValidChain());
    }
    
    @Test
    void testIsValidChainWithInvalidPreviousHash() {
        // Create a block with invalid previous hash
        List<MockTransaction> txs = new ArrayList<>();
        Block<MockTransaction> invalidBlock = new Block<>(
            1,
            "INVALID_PREVIOUS_HASH", // Should be the hash of the genesis block
            System.currentTimeMillis(),
            txs,
            0,
            "0000abcdef"  // This is just a dummy hash
        );
        
        blockchain.addBlock(invalidBlock);
        
        // Chain should be invalid due to incorrect previous hash
        assertFalse(blockchain.isValidChain());
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
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MockTransaction that = (MockTransaction) o;
            return valid == that.valid;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(valid);
        }
    }
}