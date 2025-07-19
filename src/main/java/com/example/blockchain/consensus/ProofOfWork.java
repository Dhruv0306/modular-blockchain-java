package com.example.blockchain.consensus;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.example.blockchain.core.config.ChainConfig;
import com.example.blockchain.core.model.Block;
import com.example.blockchain.core.model.Transaction;
import com.example.blockchain.core.utils.HashUtils;
import com.example.blockchain.logging.BlockchainLoggerFactory;

import org.slf4j.Logger;

/**
 * Implementation of Proof of Work consensus algorithm.
 * Validates and generates blocks by requiring computational work to be
 * performed.
 * The difficulty level determines how many leading zeros the block hash must
 * have.
 *
 * @param <T> Type of transaction stored in blocks
 */
public class ProofOfWork<T extends Transaction> implements Consensus<T> {
    private static final Logger logger = BlockchainLoggerFactory.getLogger(ProofOfWork.class);

    /**
     * Validates a new block against the previous block in the chain.
     * Checks three conditions:
     * 1. The previous hash matches
     * 2. The block hash is valid
     * 3. The block hash meets the difficulty requirement
     *
     * @param newBlock      Block to validate
     * @param previousBlock Previous block in the chain
     * @return true if block is valid, false otherwise
     */
    @Override
    public boolean validateBlock(Block<T> newBlock, Block<T> previousBlock) {
        int difficulty = ChainConfig.getInstance().getDifficulty();
        logger.debug("Validating block with difficulty {}", difficulty);

        // Check if previous block hash matches
        boolean validPreviousHash = newBlock.getPreviousHash().equals(previousBlock.getHash());
        // Verify block hash matches computed hash
        boolean validHash = newBlock.getHash().equals(HashUtils.computeHash(newBlock));
        // Check if hash meets difficulty requirement (starts with required number of
        // zeros)
        boolean validDifficulty = newBlock.getHash().startsWith("0".repeat(difficulty));

        if (!validPreviousHash) {
            logger.warn("Invalid previous hash: {} expected: {}", newBlock.getPreviousHash(), previousBlock.getHash());
        }

        if (!validHash) {
            // For blocks loaded from persistence, we'll log a warning but still consider
            // them valid
            // if they meet the difficulty requirement
            logger.warn("Invalid block hash. Expected: {}, Actual: {}", HashUtils.computeHash(newBlock),
                    newBlock.getHash());
            // We'll still consider the block valid if it meets the difficulty requirement
            // validHash = true;
        }

        if (!validDifficulty) {
            logger.warn("Block hash doesn't meet difficulty requirement of {}", difficulty);
        }

        return validPreviousHash && validHash && validDifficulty;
    }

    /**
     * Generates a new block by performing proof of work.
     * Incrementally tries different nonce values until a hash is found that meets
     * the difficulty requirement.
     *
     * @param txs           List of transactions to include in the block
     * @param previousBlock Previous block in the chain
     * @return New block that has been mined successfully
     */
    @Override
    public Block<T> generateBlock(List<T> txs, Block<T> previousBlock) {
        // Get current difficulty level from chain config
        int difficulty = ChainConfig.getInstance().getDifficulty();
        int index = previousBlock.getIndex() + 1;
        long timestamp = System.currentTimeMillis();
        int nonce = 0;
        String hash;

        logger.info("Starting to mine block #{} with {} transactions, difficulty={}",
                index, txs.size(), difficulty);

        // Start mining process
        long startTime = System.currentTimeMillis();
        do {
            // Try new hash with incremented nonce
            try {
                hash = HashUtils.computeHash(index, previousBlock.getHash(), timestamp, txs, nonce);
            } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                String error = "Error computing hash. \nError: " + e.getMessage();
                logger.error(error, e);
                throw new RuntimeException(error, e);
            }
            nonce++;

            // Log progress every 100,000 attempts
            if (nonce % 100000 == 0) {
                logger.debug("Mining in progress... nonce={}", nonce);
            }
        } while (!hash.startsWith("0".repeat(difficulty)));

        // Mining complete - log statistics
        long endTime = System.currentTimeMillis();
        logger.info("Block #{} mined with nonce={} in {}ms", index, (nonce - 1), (endTime - startTime));

        // Create and return the new block
        return new Block<>(index, previousBlock.getHash(), timestamp, txs, nonce - 1, hash);
    }
}
