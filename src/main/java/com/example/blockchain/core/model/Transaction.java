package com.example.blockchain.core.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Interface representing a transaction in the blockchain system.
 * Defines the core functionality required for any transaction type.
 * Uses Jackson annotation for polymorphic type handling during serialization.
 *
 * This interface provides the base contract that all transaction types must
 * implement.
 * It includes methods for transaction identification, validation, and accessing
 * core
 * transaction properties like sender and receiver.
 *
 * The interface uses Jackson annotations to support polymorphic
 * serialization/deserialization
 * of different transaction implementations. The @JsonTypeInfo annotation
 * configures type
 * information to be included as a property named "type" using the full class
 * name.
 *
 * Currently supported transaction types are:
 * - FinancialTransaction: For basic financial transfers
 * - SignedFinancialTransaction: For financial transfers with digital signatures
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, // Uses name as type identifier
        include = JsonTypeInfo.As.PROPERTY, // Includes type info as a property
        property = "type" // Property name for type information
)
@JsonSubTypes({
        // Maps the FinancialTransaction class to the name "FinancialTransaction" for
        // type info
        @JsonSubTypes.Type(value = com.example.blockchain.transactions.FinancialTransaction.class, name = "FinancialTransaction"),
        // Maps the SignedFinancialTransaction class to the name
        // "SignedFinancialTransaction" for type info
        @JsonSubTypes.Type(value = com.example.blockchain.transactions.SignedFinancialTransaction.class, name = "SignedFinancialTransaction")
})
public interface Transaction {
    /**
     * Returns a unique identifier for this transaction.
     * This ID should be unique across the entire blockchain system
     * and can be used to reference this specific transaction.
     * 
     * The transaction ID typically includes:
     * - A timestamp component
     * - A random or sequential number
     * - A hash of transaction details
     * 
     * @return A string that uniquely identifies this transaction
     * @throws IllegalStateException if the transaction ID cannot be generated
     */
    String getTransactionId();

    /**
     * Checks if this transaction is valid according to business rules.
     * Implementations should verify all transaction-specific requirements
     * such as sufficient funds, valid signatures, etc.
     * 
     * Validation typically includes checking:
     * - Sender has sufficient funds/resources
     * - Transaction signatures are valid
     * - Transaction amounts are positive
     * - Sender and receiver are valid entities
     * - Transaction complies with business rules
     * 
     * @return true if the transaction is valid, false otherwise
     * @throws IllegalStateException if validation cannot be performed
     */
    boolean isValid();

    /**
     * Gets the sender of this transaction.
     * The sender is the entity initiating the transaction and
     * should be uniquely identifiable within the system.
     * 
     * The sender identifier could be:
     * - A public key
     * - A wallet address
     * - A user ID
     * - An account number
     * 
     * @return The sender identifier
     * @throws IllegalStateException if the sender information is not available
     */
    String getSender();

    /**
     * Gets the receiver of this transaction.
     * The receiver is the entity that is the target of the transaction
     * and should be uniquely identifiable within the system.
     * 
     * The receiver identifier could be:
     * - A public key
     * - A wallet address
     * - A user ID
     * - An account number
     * 
     * @return The receiver identifier
     * @throws IllegalStateException if the receiver information is not available
     */
    String getReceiver();

    /**
     * Gets a human-readable summary of this transaction.
     * This should provide a clear, concise description of the
     * transaction details for display or logging purposes.
     * 
     * The summary typically includes:
     * - Transaction type
     * - Sender and receiver identifiers
     * - Transaction amount/details
     * - Timestamp
     * - Status information
     * 
     * @return A string summarizing the transaction
     * @throws IllegalStateException if the summary cannot be generated
     */
    String getSummary();

    /**
     * Gets the amount involved in this transaction.
     * This represents the value being transferred between
     * the sender and receiver.
     * 
     * The amount should be:
     * - A positive number
     * - Within allowed transaction limits
     * - In the system's standard currency/unit
     *
     * @return The transaction amount as a double value
     * @throws IllegalStateException if the amount cannot be retrieved
     */
    double getAmount();

    /**
     * Gets the type of this transaction.
     * The type indicates the category or classification of
     * the transaction within the system.
     *
     * Transaction types may include:
     * - Financial transfer
     * - Smart contract execution
     * - Asset transfer
     * - System operation
     *
     * @return A string identifying the transaction type
     * @throws IllegalStateException if the type cannot be determined
     */
    String getType();

    /**
     * Gets the unique identifier of the transaction sender.
     * This ID should be a system-wide unique identifier that
     * can be used to look up the sender's full details.
     *
     * The sender ID format depends on the system implementation
     * but should be consistent across all transactions.
     *
     * @return The unique identifier of the sender
     * @throws IllegalStateException if the sender ID is not available
     */
    String getSenderID();

    /**
     * Gets the unique identifier of the transaction receiver.
     * This ID should be a system-wide unique identifier that
     * can be used to look up the receiver's full details.
     *
     * The receiver ID format depends on the system implementation
     * but should be consistent across all transactions.
     *
     * @return The unique identifier of the receiver
     * @throws IllegalStateException if the receiver ID is not available
     */
    String getReceiverID();
}
