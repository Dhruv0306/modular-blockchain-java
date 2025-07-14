package com.example.blockchain.consensus;

import java.util.List;

import com.example.blockchain.blockchain.Block;
import java.security.MessageDigest;
import com.example.blockchain.blockchain.Transaction;

public class ProofOfWork<T extends Transaction> implements Consensus<T> {
    private static final int DIFFICULTY = 4;

    @Override
    public boolean validateBlock(Block<T> newBlock, Block<T> previousBlock) {
        return newBlock.getPreviousHash().equals(previousBlock.getHash())
            && newBlock.getHash().equals(computeHash(newBlock))
            && newBlock.getHash().startsWith("0".repeat(DIFFICULTY));
    }

    @Override
    public Block<T> generateBlock(List<T> txs, Block<T> previousBlock) {
        int index = previousBlock.getIndex() + 1;
        long timestamp = System.currentTimeMillis();
        int nonce = 0;
        String hash;
        do {
            hash = computeHash(index, previousBlock.getHash(), timestamp, txs, nonce);
            nonce++;
        } while (!hash.startsWith("0".repeat(DIFFICULTY)));
        return new Block<>(index, previousBlock.getHash(), timestamp, txs, nonce - 1, hash);
    }

    private String computeHash(Block<T> block) {
        return computeHash(block.getIndex(), block.getPreviousHash(), block.getTimestamp(), block.getTransactions(), block.getNonce());
    }

    private String computeHash(int index, String prevHash, long time, List<T> txs, int nonce) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String input = index + prevHash + time + txs.hashCode() + nonce;
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
