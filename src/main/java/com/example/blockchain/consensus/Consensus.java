package com.example.blockchain.consensus;

import java.util.List;

import com.example.blockchain.core.model.Block;
import com.example.blockchain.core.model.Transaction;

public interface Consensus<T extends Transaction> {
    boolean validateBlock(Block<T> newBlock, Block<T> previousBlock);

    Block<T> generateBlock(List<T> txs, Block<T> previousBlock);
}
