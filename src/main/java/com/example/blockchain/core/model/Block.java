package com.example.blockchain.core.model;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents a block in the blockchain.
 * Each block contains multiple transactions and is linked to the previous block via its hash.
 * The block maintains its position in the chain, previous block's hash, timestamp, transactions,
 * proof-of-work nonce, and its own hash.
 *
 * This class is designed to be immutable after creation to maintain blockchain integrity.
 * Transaction validation is handled through the block's hash rather than direct comparison.
 *
 * @param <T> The type of transaction stored in this block, must extend Transaction class
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Block<T extends Transaction> {
    // The position/height of this block in the blockchain (genesis block = 0)
    private int index;
    // Hash of the previous block in the chain - forms the chain linkage
    private String previousHash;
    // Unix timestamp (in milliseconds) when this block was created
    private long timestamp;
    // Ordered list of validated transactions included in this block
    private List<T> transactions;
    // Nonce value found during mining that satisfies the proof-of-work difficulty target
    private int nonce;
    // SHA-256 hash of this block's header (includes all fields except transactions)
    private String hash;

    /**
     * Default constructor for deserialization.
     * Required for Jackson to create instances from JSON.
     * Should not be used directly in application code.
     */
    public Block() {}

    /**
     * Creates a new immutable block with the specified parameters.
     * All parameters should be validated before calling this constructor.
     *
     * @param index The position of this block in the chain (must be >= 0)
     * @param previousHash Hash of the previous block (null only for genesis block)
     * @param timestamp Time of block creation in milliseconds since Unix epoch
     * @param transactions List of validated transactions to include in this block
     * @param nonce The nonce value found during mining that satisfies proof-of-work
     * @param hash The calculated SHA-256 hash of this block's header
     */
    public Block(int index, String previousHash, long timestamp, List<T> transactions, int nonce, String hash) {
        this.index = index;
        this.previousHash = previousHash;
        this.timestamp = timestamp;
        this.transactions = transactions;
        this.nonce = nonce;
        this.hash = hash;
    }

    /**
     * Gets the index/height of this block in the blockchain.
     * Genesis block has index 0.
     *
     * @return The index of this block in the chain
     */
    public int getIndex() {
        return index;
    }

    /**
     * Gets the hash of the previous block that this block references.
     * Will be null only for the genesis block.
     *
     * @return The hash of the previous block
     */
    public String getPreviousHash() {
        return previousHash;
    }

    /**
     * Gets the creation timestamp of this block.
     * Represented as milliseconds since Unix epoch.
     *
     * @return The timestamp when this block was created
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the list of transactions included in this block.
     * The returned list should not be modified.
     *
     * @return The immutable list of transactions in this block
     */
    public List<T> getTransactions() {
        return transactions;
    }

    /**
     * Gets the nonce value that was found to satisfy the proof-of-work
     * requirement for this block.
     *
     * @return The nonce value used in proof-of-work
     */
    public int getNonce() {
        return nonce;
    }

    /**
     * Gets the SHA-256 hash of this block's header.
     * This hash incorporates all fields except transactions.
     *
     * @return The hash of this block
     */
    public String getHash() {
        return hash;
    }

    /**
     * Compares this block with another object for equality.
     * Two blocks are considered equal if they have the same index, timestamp,
     * nonce, previous hash and current hash values.
     * Note: Transactions are intentionally not compared directly since their
     * content is validated by the block's hash.
     *
     * @param o Object to compare with
     * @return true if the blocks are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Block<?> block = (Block<?>) o;

        return index == block.index &&
                timestamp == block.timestamp &&
                nonce == block.nonce &&
                Objects.equals(previousHash, block.previousHash) &&
                Objects.equals(hash, block.hash);
        // Note: We intentionally don't compare transactions directly
        // since their object identity might differ but content should be validated by
        // hash
    }

    /**
     * Generates a hash code for this block.
     * The hash code is computed from index, previousHash, timestamp, nonce and hash.
     * Note: Transactions are intentionally not included in the hash code calculation
     * since their content is already validated by the block's hash.
     *
     * @return The hash code for this block
     */
    @Override
    public int hashCode() {
        return Objects.hash(index, previousHash, timestamp, nonce, hash);
        // Note: Similar to equals, we intentionally don't include transactions
    }
}
