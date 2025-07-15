package com.example.blockchain.blockchain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * A customizable implementation of GenesisBlockFactory that allows specifying
 * custom transactions, hash values, and metadata for the genesis block.
 *
 * @param <T> The transaction type stored in the blockchain
 */
public class CustomGenesisBlockFactory<T extends Transaction> implements GenesisBlockFactory<T> {
    
    private final String genesisHash;
    private final List<T> initialTransactions;
    private final Map<String, Object> metadata;
    private final String previousHash;
    private final int nonce;
    
    /**
     * Builder class for creating CustomGenesisBlockFactory instances.
     */
    public static class Builder<T extends Transaction> {
        private String genesisHash = BlockchainConfig.getInstance().getGenesisHash();
        private List<T> initialTransactions = new ArrayList<>();
        private Map<String, Object> metadata = new HashMap<>();
        private String previousHash = "0";
        private int nonce = 0;
        
        /**
         * Sets the hash for the genesis block.
         *
         * @param hash The hash value
         * @return This builder
         */
        public Builder<T> withHash(String hash) {
            this.genesisHash = hash;
            return this;
        }
        
        /**
         * Sets initial transactions for the genesis block.
         *
         * @param transactions List of transactions
         * @return This builder
         */
        public Builder<T> withTransactions(List<T> transactions) {
            this.initialTransactions = new ArrayList<>(transactions);
            return this;
        }
        
        /**
         * Adds a single transaction to the genesis block.
         *
         * @param transaction Transaction to add
         * @return This builder
         */
        public Builder<T> addTransaction(T transaction) {
            this.initialTransactions.add(transaction);
            return this;
        }
        
        /**
         * Sets the previous hash for the genesis block.
         *
         * @param previousHash The previous hash value
         * @return This builder
         */
        public Builder<T> withPreviousHash(String previousHash) {
            this.previousHash = previousHash;
            return this;
        }
        
        /**
         * Sets the nonce value for the genesis block.
         *
         * @param nonce The nonce value
         * @return This builder
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
         * @param key Metadata key
         * @param value Metadata value
         * @return This builder
         */
        public Builder<T> withMetadata(String key, Object value) {
            this.metadata.put(key, value);
            return this;
        }
        
        /**
         * Builds a new CustomGenesisBlockFactory with the configured properties.
         *
         * @return A new CustomGenesisBlockFactory instance
         */
        public CustomGenesisBlockFactory<T> build() {
            return new CustomGenesisBlockFactory<>(this);
        }
    }
    
    /**
     * Creates a new builder for CustomGenesisBlockFactory.
     *
     * @return A new builder instance
     */
    public static <T extends Transaction> Builder<T> builder() {
        return new Builder<>();
    }
    
    private CustomGenesisBlockFactory(Builder<T> builder) {
        this.genesisHash = builder.genesisHash;
        this.initialTransactions = builder.initialTransactions;
        this.metadata = builder.metadata;
        this.previousHash = builder.previousHash;
        this.nonce = builder.nonce;
    }
    
    @Override
    public Block<T> createGenesisBlock() {
        return new Block<>(0, previousHash, System.currentTimeMillis(), 
                          initialTransactions, nonce, genesisHash);
    }
    
    /**
     * Gets the metadata associated with this genesis block factory.
     *
     * @return Map of metadata
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }
} 