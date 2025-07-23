package com.example.blockchain.core.pool;

import com.example.blockchain.core.model.Transaction;
import com.example.blockchain.logging.BlockchainLoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class Mempool {
    private static final org.slf4j.Logger logger = BlockchainLoggerFactory.getLogger(Mempool.class);

    private final Map<String, Transaction> transactionMap = new ConcurrentHashMap<>();

    public boolean addTransaction(Transaction tx) {
        if (transactionMap.containsKey(tx.getHash())) return false;
        try {
            if (!tx.isValid()) return false;
        } catch (NoSuchAlgorithmException e) {
            String error = "Failed To varify Transection. \nError: " + e.getMessage();
            logger.error(error, e.getMessage());
            throw new RuntimeException(error, e);
        }
        transactionMap.put(tx.getHash().toString(), tx);
        return true;
    }

    public boolean removeTransaction(String hash) {
        return transactionMap.remove(hash) != null;
    }

    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactionMap.values());
    }

    public List<Transaction> getTopN(int limit) {
        return transactionMap.values().stream().limit(limit).toList();
    }

    public void clear() {
        transactionMap.clear();
    }

    public boolean contains(String hash) {
        return transactionMap.containsKey(hash);
    }

    public int size() {
        return transactionMap.size();
    }

    public boolean isEmpty() {
        return transactionMap.isEmpty();
    }

    public void removeAllTransactions(List<Transaction> minedTransactions) {
        minedTransactions.forEach(tx -> transactionMap.remove(tx.getHash().toString()));
    }
}
