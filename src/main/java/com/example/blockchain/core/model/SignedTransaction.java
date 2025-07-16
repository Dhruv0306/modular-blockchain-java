package com.example.blockchain.core.model;

import java.security.PublicKey;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Interface representing a signed transaction in the blockchain.
 * Extends the base Transaction interface and adds signature verification functionality.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS, 
    include = JsonTypeInfo.As.PROPERTY, 
    property = "@class"
)
public interface SignedTransaction extends Transaction {
    /**
     * Gets the cryptographic signature of this transaction
     * @return The signature as a String
     */
    String getSignature();

    /**
     * Gets the public key of the transaction sender
     * @return The sender's public key
     */
    PublicKey getSenderPublicKey();

    /**
     * Verifies that the transaction signature is valid
     * Uses the sender's public key to verify the signature against the transaction content
     * @return true if signature is valid, false otherwise
     */
    boolean verifySignature(); // uses senderPublicKey + content
}
