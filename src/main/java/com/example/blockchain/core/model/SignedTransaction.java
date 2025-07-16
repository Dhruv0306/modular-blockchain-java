package com.example.blockchain.core.model;

import java.security.PublicKey;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS, 
    include = JsonTypeInfo.As.PROPERTY, 
    property = "@class"
)
public interface SignedTransaction extends Transaction {
    String getSignature();

    PublicKey getSenderPublicKey();

    boolean verifySignature(); // uses senderPublicKey + content
}
