package com.example.blockchain.core.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS, 
    include = JsonTypeInfo.As.PROPERTY, 
    property = "@class"
)
public interface Transaction {
    /**
     * Returns a unique identifier for this transaction.
     * 
     * @return A string that uniquely identifies this transaction
     */
    String getTransactionId();
    
    /**
     * Checks if this transaction is valid according to business rules.
     * 
     * @return true if the transaction is valid, false otherwise
     */
    boolean isValid();

    /**
     * Gets the sender of this transaction.
     * 
     * @return The sender identifier
     */
    String getSender();

    /**
     * Gets the receiver of this transaction.
     * 
     * @return The receiver identifier
     */
    String getReceiver();

    /**
     * Gets a human-readable summary of this transaction.
     * 
     * @return A string summarizing the transaction
     */
    String getSummary();
}
