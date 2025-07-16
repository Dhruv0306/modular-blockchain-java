package com.example.blockchain.core.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Interface representing a transaction in the blockchain system.
 * Defines the core functionality required for any transaction type.
 * Uses Jackson annotation for polymorphic type handling during serialization.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS, // Uses class name as type identifier
    include = JsonTypeInfo.As.PROPERTY, // Includes type info as a property
    property = "@class" // Property name for type information
)
public interface Transaction {
    /**
     * Returns a unique identifier for this transaction.
     * This ID should be unique across the entire blockchain system
     * and can be used to reference this specific transaction.
     * 
     * @return A string that uniquely identifies this transaction
     */
    String getTransactionId();
    
    /**
     * Checks if this transaction is valid according to business rules.
     * Implementations should verify all transaction-specific requirements
     * such as sufficient funds, valid signatures, etc.
     * 
     * @return true if the transaction is valid, false otherwise
     */
    boolean isValid();

    /**
     * Gets the sender of this transaction.
     * The sender is the entity initiating the transaction and
     * should be uniquely identifiable within the system.
     * 
     * @return The sender identifier
     */
    String getSender();

    /**
     * Gets the receiver of this transaction.
     * The receiver is the entity that is the target of the transaction
     * and should be uniquely identifiable within the system.
     * 
     * @return The receiver identifier
     */
    String getReceiver();

    /**
     * Gets a human-readable summary of this transaction.
     * This should provide a clear, concise description of the
     * transaction details for display or logging purposes.
     * 
     * @return A string summarizing the transaction
     */
    String getSummary();
}
