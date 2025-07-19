package com.example.blockchain.core.model;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

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
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeySpecException 
     */
    PublicKey getSenderPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException;

    /**
     * Verifies that the transaction signature is valid
     * Uses the sender's public key to verify the signature against the transaction content
     * @return true if signature is valid, false otherwise
     * @throws NoSuchAlgorithmException 
     */
    boolean verifySignature() throws NoSuchAlgorithmException; // uses senderPublicKey + content
}
