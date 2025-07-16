package com.example.blockchain.core.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.blockchain.core.model.Block;
import com.example.blockchain.core.model.Transaction;

import java.util.HashMap;

/**
 * A customizable implementation of GenesisBlockFactory that allows specifying
 * custom transactions, hash values, and metadata for the genesis block.
 * This factory provides a flexible way to create the initial block (genesis block)
 * of a blockchain with custom configurations.
 *
 * @param <T> The transaction type stored in the blockchain
 */
public class CustomGenesisBlockFactory<T extends Transaction> implements GenesisBlockFactory<T> {

    // Hash value for the genesis block
    private final String genesisHash;
    
    // List of initial transactions to include in genesis block
    private final List<T> initialTransactions;
    
    // Additional metadata for custom block implementations
    private final Map<String, Object> metadata;
    
    // Previous hash value (typically "0" for genesis block)
    private final String previousHash;
    
    // Nonce value for proof-of-work
    private final int nonce;

    /**
     * Builder class for creating CustomGenesisBlockFactory instances.
     * Provides a fluent interface to configure all aspects of the genesis block.
     */
    public static class Builder<T extends Transaction> {
        // Default genesis hash from chain configuration
        private String genesisHash = ChainConfig.getInstance().getGenesisHash();
        
        // Empty list to store initial transactions
        private List<T> initialTransactions = new ArrayList<>();
        
        // Empty map to store metadata key-value pairs
        private Map<String, Object> metadata = new HashMap<>();
        
        // Default previous hash for genesis block
        private String previousHash = "0";
        
        // Default nonce value
        private int nonce = 0;

        /**
         * Sets the hash for the genesis block.
         * This hash uniquely identifies the genesis block in the chain.
         *
         * @param hash The hash value to use for the genesis block
         * @return This builder instance for method chaining
         */
        public Builder<T> withHash(String hash) {
            this.genesisHash = hash;
            return this;
        }

        /**
         * Sets initial transactions for the genesis block.
         * Creates a new ArrayList to avoid modifying the input list.
         *
         * @param transactions List of transactions to include in genesis block
         * @return This builder instance for method chaining
         */
        public Builder<T> withTransactions(List<T> transactions) {
            this.initialTransactions = new ArrayList<>(transactions);
            return this;
        }

        /**
         * Adds a single transaction to the genesis block.
         * Appends the transaction to the existing list of transactions.
         *
         * @param transaction Transaction to add to genesis block
         * @return This builder instance for method chaining
         */
        public Builder<T> addTransaction(T transaction) {
            this.initialTransactions.add(transaction);
            return this;
        }

        /**
         * Sets the previous hash for the genesis block.
         * For genesis blocks, this is typically "0" but can be customized.
         *
         * @param previousHash The previous hash value to use
         * @return This builder instance for method chaining
         */
        public Builder<T> withPreviousHash(String previousHash) {
            this.previousHash = previousHash;
            return this;
        }

        /**
         * Sets the nonce value for the genesis block.
         * Used in proof-of-work calculations.
         *
         * @param nonce The nonce value to use
         * @return This builder instance for method chaining
         */
        public Builder<T> withNonce(int nonce) {
            this.nonce = nonce;
            return this;
        }

        /**
         * Adds metadata to the genesis block factory.
         * Metadata won't be stored in the block directly but can be used
         * by custom block implementations that support metadata.
         *
         * @param key   Metadata key to add
         * @param value Metadata value associated with the key
         * @return This builder instance for method chaining
         */
        public Builder<T> withMetadata(String key, Object value) {
            this.metadata.put(key, value);
            return this;
        }

        /**
         * Builds a new CustomGenesisBlockFactory with the configured properties.
         * Creates an immutable instance with all the builder's settings.
         *
         * @return A new CustomGenesisBlockFactory instance
         */
        public CustomGenesisBlockFactory<T> build() {
            return new CustomGenesisBlockFactory<>(this);
        }
    }

    /**
     * Creates a new builder for CustomGenesisBlockFactory.
     * Static factory method to start the builder pattern.
     *
     * @return A new builder instance
     */
    public static <T extends Transaction> Builder<T> builder() {
        return new Builder<>();
    }

    /**
     * Private constructor that takes a Builder instance.
     * Copies all values from the builder to create an immutable instance.
     *
     * @param builder Builder containing the configuration
     */
    private CustomGenesisBlockFactory(Builder<T> builder) {
        this.genesisHash = builder.genesisHash;
        this.initialTransactions = builder.initialTransactions;
        this.metadata = builder.metadata;
        this.previousHash = builder.previousHash;
        this.nonce = builder.nonce;
    }

    /**
     * Creates and returns the genesis block with the configured properties.
     * Implements the GenesisBlockFactory interface method.
     *
     * @return A new Block instance representing the genesis block
     */
    @Override
    public Block<T> createGenesisBlock() {
        // Create new block with index 0 (genesis), configured properties, and current timestamp
        return new Block<>(0, previousHash, System.currentTimeMillis(),
                initialTransactions, nonce, genesisHash);
    }

    /**
     * Gets the metadata associated with this genesis block factory.
     * Returns the internal metadata map for use by custom implementations.
     *
     * @return Unmodifiable map of metadata key-value pairs
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }
}
