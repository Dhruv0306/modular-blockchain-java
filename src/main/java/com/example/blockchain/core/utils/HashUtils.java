package com.example.blockchain.core.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.slf4j.Logger;

import com.example.blockchain.consensus.ProofOfWork;
import com.example.blockchain.core.model.Block;
import com.example.blockchain.core.model.Transaction;
import com.example.blockchain.logging.BlockchainLoggerFactory;

public class HashUtils {
    private static final Logger logger = BlockchainLoggerFactory.getLogger(ProofOfWork.class);
    public static <T extends Transaction> String computeHash(Block<T> block) {
        // logger.info("Computing hash for block index: {}, previous hash: {}, timestamp: {}, nonce: {}",
        //         block.getIndex(), block.getPreviousHash(), block.getTimestamp(), block.getNonce());
        try {
            return computeHash(
                    block.getIndex(),
                    block.getPreviousHash(),
                    block.getTimestamp(),
                    block.getTransactions(),
                    block.getNonce());
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            String error = "Failed to compute block hash: " + e.getMessage();
            logger.error(error, e.getMessage());
            throw new RuntimeException(error, e);
        }
    }

    public static <T extends Transaction> String computeHash(int index, String prevHash, long timestamp, List<T> txs,
            int nonce) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        // logger.info("Computing hash for block index: {}, previous hash: {}, timestamp: {}, nonce: {}",
        //         index, prevHash, timestamp, nonce);
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
            logger.error("SHA-256 algorithm not available for hash computation", e.getMessage());
            throw new NoSuchAlgorithmException("Failed to compute block hash: SHA-256 algorithm not available", e);
        } catch (java.io.UnsupportedEncodingException e) {
            logger.error("UTF-8 encoding not supported for hash computation", e.getMessage());
            throw new UnsupportedEncodingException("Failed to compute block hash: UTF-8 encoding not supported");
        } catch (Exception e) {
            logger.error("Unexpected error computing block hash for block with index " + index, e.getMessage());
            throw new RuntimeException("Failed to compute block hash: " + e.getMessage(), e);
        }
    }
}
