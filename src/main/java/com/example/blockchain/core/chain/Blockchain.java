package com.example.blockchain.core.chain;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.example.blockchain.consensus.Consensus;
import com.example.blockchain.consensus.ProofOfWork;
import com.example.blockchain.core.config.DefaultGenesisBlockFactory;
import com.example.blockchain.core.config.GenesisBlockFactory;
import com.example.blockchain.core.model.Block;
import com.example.blockchain.core.model.Transaction;
import com.example.blockchain.core.utils.JsonUtils;
import com.example.blockchain.logging.BlockchainLoggerFactory;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.slf4j.Logger;

/**
 * Main blockchain class that manages the chain of blocks and pending
 * transactions.
 * Uses a generic type T that extends Transaction to allow for different
 * transaction types.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Blockchain<T extends Transaction> {
    // Logger instance for this class
    private static final Logger logger = BlockchainLoggerFactory.getLogger(Blockchain.class);

    // The main chain of blocks
    private final List<Block<T>> chain = new ArrayList<>();

    // Transactions waiting to be included in next block
    private final List<T> pendingTransactions = new ArrayList<>();

    // Consensus mechanism used for block validation
    private final Consensus<T> consensus;

    /**
     * Creates a new blockchain with a default genesis block and proof of work
     * consensus.
     */
    public Blockchain() {
        this(new DefaultGenesisBlockFactory<>(), new ProofOfWork<>());
    }

    /**
     * Creates a new blockchain with a custom genesis block factory and default
     * consensus.
     *
     * @param genesisBlockFactory The factory to create the genesis block
     */
    public Blockchain(GenesisBlockFactory<T> genesisBlockFactory) {
        this(genesisBlockFactory, new ProofOfWork<>());
    }

    /**
     * Creates a new blockchain with a custom genesis block factory and consensus
     * mechanism.
     *
     * @param genesisBlockFactory The factory to create the genesis block
     * @param consensus           The consensus mechanism to use
     */
    public Blockchain(GenesisBlockFactory<T> genesisBlockFactory, Consensus<T> consensus) {
        this.consensus = consensus;
        chain.add(genesisBlockFactory.createGenesisBlock());
    }

    /**
     * Adds a new transaction to the pending transactions pool if it's valid.
     * 
     * @param tx The transaction to add
     * @return 
     */
    public boolean addTransaction(T tx) {
        if (tx.isValid()){
            pendingTransactions.add(tx);
            return true;
        }
        return false;
    }

    /**
     * Returns a copy of the current pending transactions list.
     * 
     * @return List of pending transactions
     */
    public List<T> getPendingTransactions() {
        return new ArrayList<>(pendingTransactions);
    }

    /**
     * Gets the most recent block in the chain.
     * 
     * @return The last block in the chain
     */
    public Block<T> getLastBlock() {
        return chain.get(chain.size() - 1);
    }

    /**
     * Adds a new block to the chain and clears pending transactions.
     * 
     * @param block The block to add to the chain
     */
    public void addBlock(Block<T> block) {
        chain.add(block);
        pendingTransactions.clear();
    }

    /**
     * Returns the complete blockchain.
     * 
     * @return List of all blocks in the chain
     */
    public List<Block<T>> getChain() {
        return chain;
    }

    /**
     * Validates the entire blockchain by checking:
     * 1. Block sequence is valid
     * 2. Each block is valid according to consensus rules
     * 3. Block indices are sequential
     * 
     * @return true if the blockchain is valid, false otherwise
     */
    public boolean isChainValid() {
        logger.debug("Validating blockchain with {} blocks", chain.size());

        // A blockchain with only the genesis block is always valid
        if (chain.size() == 1) {
            return true;
        }

        // Check each block in the chain
        for (int i = 1; i < chain.size(); i++) {
            Block<T> currentBlock = chain.get(i);
            Block<T> previousBlock = chain.get(i - 1);

            // Validate current block using consensus mechanism
            if (!consensus.validateBlock(currentBlock, previousBlock)) {
                logger.warn("Invalid block at index {}: {}", i, currentBlock.getHash());
                return false;
            }

            // Check that block index is sequential
            if (currentBlock.getIndex() != previousBlock.getIndex() + 1) {
                logger.warn("Non-sequential block index at block {}", i);
                return false;
            }
        }

        return true;
    }

    /**
     * Exports the blockchain to a JSON file.
     * 
     * @param file The file to write the blockchain to
     * @throws Exception if there is an error writing to the file
     */
    public void exportToJson(File file) throws Exception {
        JsonUtils.writeToFile(this, file);
    }

    /**
     * Imports a blockchain from a JSON file.
     * 
     * @param file             The file to read the blockchain from
     * @param transactionClass The class type of transactions in the blockchain
     * @return A new Blockchain instance loaded from the file
     * @throws Exception if there is an error reading from the file
     */
    public static <T extends Transaction> Blockchain<T> importFromJson(File file, Class<T> transactionClass)
            throws Exception {
        return JsonUtils.readFromFile(file, JsonUtils.getBlockchainType(transactionClass));
    }
}
