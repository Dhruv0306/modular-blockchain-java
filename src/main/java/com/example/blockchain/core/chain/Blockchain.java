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

@JsonIgnoreProperties(ignoreUnknown = true)
public class Blockchain<T extends Transaction> {
    private static final Logger logger = BlockchainLoggerFactory.getLogger(Blockchain.class);
    private final List<Block<T>> chain = new ArrayList<>();
    private final List<T> pendingTransactions = new ArrayList<>();
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

    public void addTransaction(T tx) {
        if (tx.isValid())
            pendingTransactions.add(tx);
    }

    public List<T> getPendingTransactions() {
        return pendingTransactions;
    }

    public Block<T> getLastBlock() {
        return chain.get(chain.size() - 1);
    }

    public void addBlock(Block<T> block) {
        chain.add(block);
        pendingTransactions.clear();
    }

    public List<Block<T>> getChain() {
        return chain;
    }

    /**
     * Validates the entire blockchain.
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

    public void exportToJson(File file) throws Exception {
        JsonUtils.writeToFile(this, file);
    }

    public static <T extends Transaction> Blockchain<T> importFromJson(File file, Class<T> transactionClass)
            throws Exception {
        return JsonUtils.readFromFile(file, JsonUtils.getBlockchainType(transactionClass));
    }
}
