package com.example.blockchain.consensus;

import com.example.blockchain.blockchain.Block;
import com.example.blockchain.blockchain.Transaction;
import java.util.List;

public interface Consensus<T extends Transaction> {
    boolean validateBlock(Block<T> newBlock, Block<T> previousBlock);

    Block<T> generateBlock(List<T> txs, Block<T> previousBlock);
}
