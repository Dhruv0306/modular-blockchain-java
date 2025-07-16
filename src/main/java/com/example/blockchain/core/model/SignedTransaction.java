package com.example.blockchain.core.model;

import java.security.PublicKey;

public interface SignedTransaction extends Transaction {
    String getSignature();

    PublicKey getSenderPublicKey();

    boolean verifySignature(); // uses senderPublicKey + content
}
