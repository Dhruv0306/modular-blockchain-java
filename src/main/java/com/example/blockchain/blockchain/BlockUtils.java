package com.example.blockchain.blockchain;

import java.security.MessageDigest;
import java.util.List;

public class BlockUtils {
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
            String input = index + prevHash + timestamp + txs.hashCode() + nonce;
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash)
                hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
