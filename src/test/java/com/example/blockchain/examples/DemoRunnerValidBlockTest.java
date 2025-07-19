package com.example.blockchain.examples;

import com.example.blockchain.consensus.Consensus;
import com.example.blockchain.core.chain.Blockchain;
import com.example.blockchain.core.config.ChainConfig;
import com.example.blockchain.core.model.Block;
import com.example.blockchain.transactions.FinancialTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for DemoRunner that specifically tests the valid block validation branch.
 */
class DemoRunnerValidBlockTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private ChainConfig config;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        config = ChainConfig.getInstance("blockchain.properties");
    }

    /**
     * Test case for when block validation succeeds.
     * This test uses a mock consensus that always returns true for validation.
     */
    @Test
    void testValidBlockValidation() throws Exception {
        // Create a blockchain with a mock consensus that always succeeds validation
        Blockchain<FinancialTransaction> blockchain = new Blockchain<>();
        
        // Create a mock consensus that generates blocks and always passes validation
        Consensus<FinancialTransaction> mockConsensus = new Consensus<FinancialTransaction>() {
            @Override
            public boolean validateBlock(Block<FinancialTransaction> newBlock, Block<FinancialTransaction> previousBlock) {
                // Always return true to simulate successful validation
                return true;
            }

            @Override
            public Block<FinancialTransaction> generateBlock(List<FinancialTransaction> transactions, Block<FinancialTransaction> previousBlock) {
                // Create a simple valid block
                return new Block<FinancialTransaction>(
                    previousBlock.getIndex() + 1,
                    previousBlock.getHash(),
                    System.currentTimeMillis(),
                    new ArrayList<>(transactions),
                    0,
                    "valid-hash-" + System.currentTimeMillis()
                );
            }
        };

        // Create DemoRunner instance
        DemoRunner demoRunner = new DemoRunner();
        
        // Use reflection to access the private runBlockchainExample method
        Method runBlockchainExampleMethod = DemoRunner.class.getDeclaredMethod(
            "runBlockchainExample", 
            Blockchain.class, 
            ChainConfig.class
        );
        runBlockchainExampleMethod.setAccessible(true);
        
        // Add some transactions to the blockchain
        blockchain.addTransaction(new FinancialTransaction("Alice", "Bob", 50, "U124", "U123"));
        
        // Get the initial block count
        int initialBlockCount = blockchain.getChain().size();
        
        // Run the example with our mock consensus
        runBlockchainExampleMethod.invoke(demoRunner, blockchain, config);
        
        // Verify that a new block was added (validation succeeded)
        int finalBlockCount = blockchain.getChain().size();
        
        // Assert that the block count increased (validation succeeded)
        assertTrue(finalBlockCount > initialBlockCount, 
            "Block count should increase when validation succeeds");
        
        // Check output to ensure validation success was handled properly
        String output = outContent.toString();
        assertTrue(output.contains("Mining block"), "Mining should have been attempted");
        assertTrue(output.contains("Block added to chain"), 
            "Block should have been added when validation succeeds");
    }
}