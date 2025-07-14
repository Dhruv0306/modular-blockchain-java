package com.example.blockchain;

import com.example.blockchain.blockchain.Block;
import com.example.blockchain.blockchain.Blockchain;
import com.example.blockchain.consensus.Consensus;
import com.example.blockchain.consensus.ProofOfWork;
import com.example.blockchain.transactions.FinancialTransaction;

public class Main {
    public static void main(String[] args) {
        Blockchain<FinancialTransaction> blockchain = new Blockchain<>();
        Consensus<FinancialTransaction> consensus = new ProofOfWork<>();

        blockchain.addTransaction(new FinancialTransaction("Alice", "Bob", 100));
        blockchain.addTransaction(new FinancialTransaction("Charlie", "Dave", 75));

        Block<FinancialTransaction> newBlock = consensus.generateBlock(
                blockchain.getPendingTransactions(),
                blockchain.getLastBlock()
        );

        if (consensus.validateBlock(newBlock, blockchain.getLastBlock())) {
            blockchain.addBlock(newBlock);
            System.out.println("✅ Block added to chain");
        }

        for (Block<FinancialTransaction> block : blockchain.getChain()) {
            System.out.println("Block #" + block.getIndex() + " | Hash: " + block.getHash());
            for (FinancialTransaction tx : block.getTransactions()) {
                System.out.println("  - " + tx);
            }
        }
    }
}
