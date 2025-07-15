package com.example.blockchain.consensus;

import java.util.List;
import com.example.blockchain.blockchain.Block;
import com.example.blockchain.blockchain.BlockchainConfig;
import com.example.blockchain.logging.BlockchainLoggerFactory;
import com.example.blockchain.blockchain.BlockUtils;
import com.example.blockchain.blockchain.Transaction;
import org.slf4j.Logger;

public class ProofOfWork<T extends Transaction> implements Consensus<T> {
    private static final Logger logger = BlockchainLoggerFactory.getLogger(ProofOfWork.class);

    @Override
    public boolean validateBlock(Block<T> newBlock, Block<T> previousBlock) {
        int difficulty = BlockchainConfig.getInstance().getDifficulty();
        logger.debug("Validating block with difficulty {}", difficulty);

        boolean validPreviousHash = newBlock.getPreviousHash().equals(previousBlock.getHash());
        boolean validHash = newBlock.getHash().equals(BlockUtils.computeHash(newBlock));
        boolean validDifficulty = newBlock.getHash().startsWith("0".repeat(difficulty));

        if (!validPreviousHash) {
            logger.warn("Invalid previous hash: {} expected: {}", newBlock.getPreviousHash(), previousBlock.getHash());
        }

        if (!validHash) {
            logger.warn("Invalid block hash. Expected: {}, Actual: {}", BlockUtils.computeHash(newBlock), newBlock.getHash());
        }

        if (!validDifficulty) {
            logger.warn("Block hash doesn't meet difficulty requirement of {}", difficulty);
        }

        return validPreviousHash && validHash && validDifficulty;
    }

    @Override
    public Block<T> generateBlock(List<T> txs, Block<T> previousBlock) {
        int difficulty = BlockchainConfig.getInstance().getDifficulty();
        int index = previousBlock.getIndex() + 1;
        long timestamp = System.currentTimeMillis();
        int nonce = 0;
        String hash;

        logger.debug("Starting to mine block #{} with {} transactions, difficulty={}",
                index, txs.size(), difficulty);

        long startTime = System.currentTimeMillis();
        do {
            hash = BlockUtils.computeHash(index, previousBlock.getHash(), timestamp, txs, nonce);
            nonce++;

            // Log progress every 100,000 attempts
            if (nonce % 100000 == 0) {
                logger.debug("Mining in progress... nonce={}", nonce);
            }
        } while (!hash.startsWith("0".repeat(difficulty)));

        long endTime = System.currentTimeMillis();
        logger.info("Block #{} mined with nonce={} in {}ms", index, (nonce - 1), (endTime - startTime));

        return new Block<>(index, previousBlock.getHash(), timestamp, txs, nonce - 1, hash);
    }
}
