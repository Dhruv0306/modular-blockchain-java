package com.example.blockchain.examples;

import com.example.blockchain.consensus.Consensus;
import com.example.blockchain.core.chain.Blockchain;
import com.example.blockchain.core.config.ChainConfig;
import com.example.blockchain.core.model.Block;
import com.example.blockchain.transactions.FinancialTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for DemoRunner that specifically tests the invalid block validation branch.
 */
class DemoRunnerInvalidBlockTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private ChainConfig config;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        config = ChainConfig.getInstance("blockchain.properties");
    }

    /**
     * Test case for when block validation fails.
     * This test uses a mock consensus that always returns false for validation.
     */
    @Test
    void testInvalidBlockValidation() throws Exception {
        // Create a blockchain with a mock consensus that always fails validation
        Blockchain<FinancialTransaction> blockchain = new Blockchain<>();
        
        // Create a mock consensus that generates blocks but fails validation
        Consensus<FinancialTransaction> mockConsensus = new Consensus<FinancialTransaction>() {
            @Override
            public boolean validateBlock(Block<FinancialTransaction> newBlock, Block<FinancialTransaction> previousBlock) {
                // Always return false to simulate validation failure
                return false;
            }

            @Override
            public Block<FinancialTransaction> generateBlock(List<FinancialTransaction> transactions, Block<FinancialTransaction> previousBlock) {
                // Create a simple block (details don't matter as validation will fail)
                return new Block<FinancialTransaction>(
                    previousBlock.getIndex() + 1,
                    previousBlock.getHash(),
                    System.currentTimeMillis(),
                    new ArrayList<>(transactions),
                    0,
                    "invalid-hash"
                );
            }
        };

        // Create DemoRunner instance
        DemoRunner demoRunner = new DemoRunner();
        
        // Use reflection to access the private runBlockchainExample method with consensus parameter
        Method runBlockchainExampleMethod = DemoRunner.class.getDeclaredMethod(
            "runBlockchainExample", 
            Blockchain.class, 
            ChainConfig.class,
            Consensus.class
        );
        runBlockchainExampleMethod.setAccessible(true);
        
        // Add some transactions to the blockchain
        blockchain.addTransaction(new FinancialTransaction("Alice", "Bob", 50, "U124", "U123"));
        
        // Get the initial block count
        int initialBlockCount = blockchain.getChain().size();
        
        // Run the example with our mock consensus
        runBlockchainExampleMethod.invoke(demoRunner, blockchain, config, mockConsensus);
        
        // Verify that no new blocks were added (validation failed)
        int finalBlockCount = blockchain.getChain().size();
        
        // Assert that the block count didn't change (validation failed)
        assertTrue(initialBlockCount == finalBlockCount, 
            "Block count should remain unchanged when validation fails");
        
        // Check output to ensure validation failure was handled properly
        String output = outContent.toString();
        assertTrue(output.contains("Mining block"), "Mining should have been attempted");
        assertFalse(output.contains("Block added to chain"), 
            "Block should not have been added when validation fails");
    }
}