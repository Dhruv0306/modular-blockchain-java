package com.example.blockchain.blockchain;

import java.security.MessageDigest;
import java.util.List;

import org.slf4j.Logger;

import com.example.blockchain.consensus.ProofOfWork;
import com.example.blockchain.logging.BlockchainLoggerFactory;

public class BlockUtils {
    private static final Logger logger = BlockchainLoggerFactory.getLogger(ProofOfWork.class);
    public static <T extends Transaction> String computeHash(Block<T> block) {
        return computeHash(
                block.getIndex(),
                block.getPreviousHash(),
                block.getTimestamp(),
                block.getTransactions(),
                block.getNonce());
    }

    public static <T extends Transaction> String computeHash(int index, String prevHash, long timestamp, List<T> txs,
            int nonce) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Create a deterministic string representation of all transaction details
            StringBuilder txDetails = new StringBuilder();
            if (txs != null) {
                for (T tx : txs) {
                    // Include complete details of each transaction in the hash calculation
                    txDetails.append(tx.getTransactionId())
                            .append(tx.getSender())
                            .append(tx.getReceiver())
                            .append(tx.getSummary())
                            .append(tx.isValid());
                }
            }

            String input = index + prevHash + timestamp + txDetails.toString() + nonce;
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash)
                sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            logger.error("SHA-256 algorithm not available for hash computation", e);
            throw new RuntimeException("Failed to compute block hash: SHA-256 algorithm not available", e);
        } catch (java.io.UnsupportedEncodingException e) {
            logger.error("UTF-8 encoding not supported for hash computation", e);
            throw new RuntimeException("Failed to compute block hash: UTF-8 encoding not supported", e);
        } catch (Exception e) {
            logger.error("Unexpected error computing block hash for block with index " + index, e);
            throw new RuntimeException("Failed to compute block hash: " + e.getMessage(), e);
        }
    }
}
