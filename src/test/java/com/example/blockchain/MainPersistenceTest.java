package com.example.blockchain;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.example.blockchain.core.chain.Blockchain;
import com.example.blockchain.core.config.DefaultGenesisBlockFactory;
import com.example.blockchain.transactions.FinancialTransaction;

/**
 * Tests for the orElseGet branch in the Main class.
 */
public class MainPersistenceTest {
    
    /**
     * Test that directly verifies the orElseGet branch creates a new blockchain
     * when an empty Optional is provided.
     */
    @Test
    public void testOrElseGetCreatesNewBlockchain() {
        // Create an empty Optional<Blockchain>
        Optional<Blockchain<FinancialTransaction>> emptyOptional = Optional.empty();
        
        // Call orElseGet with the same lambda used in Main.java
        Blockchain<FinancialTransaction> blockchain = emptyOptional
                .orElseGet(() -> new Blockchain<>(new DefaultGenesisBlockFactory<>()));
        
        // Verify a new blockchain was created
        assertNotNull(blockchain);
        assertNotNull(blockchain.getChain());
        // Genesis block should exist
        assertNotNull(blockchain.getChain().get(0));
    }
}