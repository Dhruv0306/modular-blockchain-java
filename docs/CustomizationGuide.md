# Modular Blockchain Java - Customization Guide

This guide provides detailed instructions on how to customize the modular blockchain framework to meet your specific needs. Whether you're building a private blockchain for research, education, or a proof of concept, this guide will walk you through all customization options.

## Table of Contents

- [Transaction Types](#transaction-types)
  - [Understanding the Transaction Interface](#understanding-the-transaction-interface)
  - [Creating Custom Transaction Types](#creating-custom-transaction-types)
    - [Supply Chain Transaction Example](#supply-chain-transaction-example)
    - [Voting Transaction Example](#voting-transaction-example)
  - [Best Practices for Transaction Design](#best-practices-for-transaction-design)
  - [Implementing Digital Signatures for Transactions](#implementing-digital-signatures-for-transactions)
  - [Digitally Signed Transactions](#digitally-signed-transactions)
- [Consensus Algorithms](#consensus-algorithms)
  - [Understanding the Consensus Interface](#understanding-the-consensus-interface)
  - [Built-in Consensus: Proof of Work](#built-in-consensus-proof-of-work)
  - [Creating Custom Consensus Algorithms](#creating-custom-consensus-algorithms)
    - [Proof of Authority Example](#proof-of-authority-example)
    - [Practical Byzantine Fault Tolerance (Simplified Example)](#practical-byzantine-fault-tolerance-simplified-example)
  - [Consensus Algorithm Selection Criteria](#consensus-algorithm-selection-criteria)
- [Block Structure](#block-structure)
  - [The Block Class Structure](#the-block-class-structure)
  - [Extending the Block Class](#extending-the-block-class)
- [Genesis Block Customization](#genesis-block-customization)
  - [Understanding the GenesisBlockFactory Interface](#understanding-the-genesisblockfactory-interface)
  - [Using the Default Genesis Block](#using-the-default-genesis-block)
  - [Creating Custom Genesis Blocks](#creating-custom-genesis-blocks)
  - [Practical Use Cases for Custom Genesis Blocks](#practical-use-cases-for-custom-genesis-blocks)
- [Configuration System](#configuration-system)
  - [Understanding the BlockchainConfig Class](#understanding-the-blockchainconfig-class)
  - [Configuration Properties](#configuration-properties)
  - [Environment-Specific Configurations](#environment-specific-configurations)
  - [Runtime Configuration Changes](#runtime-configuration-changes)
  - [Adding Custom Configuration Properties](#adding-custom-configuration-properties)
- [REST API Customization](#rest-api-customization)
  - [Understanding the BlockchainController](#understanding-the-blockchaincontroller)
  - [Adding Custom Endpoints](#adding-custom-endpoints)
  - [Customizing Response Formats](#customizing-response-formats)
  - [Securing the API](#securing-the-api)
- [Wallet Customization](#wallet-customization)
  - [Understanding the Wallet System](#understanding-the-wallet-system)
  - [Customizing Wallet Functionality](#customizing-wallet-functionality)
  - [Wallet Security Considerations](#wallet-security-considerations)
- [Advanced Customizations](#advanced-customizations)
  - [JSON Serialization and Persistence](#json-serialization-and-persistence)
    - [Using JsonUtils](#using-jsonutils)
    - [Exporting and Importing Blockchain](#exporting-and-importing-blockchain)
    - [Using Automatic Persistence](#using-automatic-persistence)
    - [Creating JSON-Compatible Transaction Types](#creating-json-compatible-transaction-types)
  - [Implementing Merkle Trees](#implementing-merkle-trees)
  - [Customizing Chain Persistence](#customizing-chain-persistence)
  - [Adding Network Communication](#adding-network-communication)
- [Testing Your Customizations](#testing-your-customizations)
  - [Testing Transaction Types](#testing-transaction-types)
  - [Testing Consensus Algorithms](#testing-consensus-algorithms)
  - [Testing REST API Endpoints](#testing-rest-api-endpoints)
  - [Integration Testing](#integration-testing)
  - [Testing Edge Cases](#testing-edge-cases)
  - [Testing Configuration Handling](#testing-configuration-handling)
- [Best Practices](#best-practices)
  - [Security Considerations](#security-considerations)
  - [Performance Optimization](#performance-optimization)
  - [Code Organization](#code-organization)
- [Next Steps](#next-steps)

---

## Transaction Types

The core of any blockchain application is its transaction model. In this framework, all transactions must implement the `Transaction` interface.

### Understanding the Transaction Interface

```java
public interface Transaction {
    boolean isValid();
    String getSender();
    String getReceiver();
    String getSummary();
    String getTransactionId();
    String getSenderID();
    String getReceiverID();
    Object getHash();
}
```

- `isValid()`: Determines whether a transaction meets your business rules
- `getSender()`: Returns the identity of the transaction creator
- `getReceiver()`: Returns the identity of the transaction recipient
- `getSummary()`: Provides a human-readable description of the transaction

### Creating Custom Transaction Types

The framework includes a `FinancialTransaction` example, but you can create any transaction type that fits your domain model. Here are some examples:

#### Supply Chain Transaction Example

```java
public class SupplyChainTransaction implements Transaction {
    private final String supplier;
    private final String receiver;
    private final String productId;
    private final int quantity;
    private final String status;
    private final String transactionId;
    private final String senderID;
    private final String receiverID;
    
    public SupplyChainTransaction(String supplier, String receiver, 
                                  String productId, int quantity, String status) {
        this.supplier = supplier;
        this.receiver = receiver;
        this.productId = productId;
        this.quantity = quantity;
        this.status = status;
        this.senderID = supplier;
        this.receiverID = receiver;
        // Generate hash-based transaction ID
        this.transactionId = HashUtils.sha256(supplier + receiver + productId + quantity + status);
    }
    
    @Override
    public boolean isValid() {
        return supplier != null && receiver != null && 
               productId != null && quantity > 0 &&
               (status.equals("shipped") || status.equals("delivered") || 
                status.equals("returned"));
    }
    
    @Override
    public String getSender() {
        return supplier;
    }
    
    @Override
    public String getReceiver() {
        return receiver;
    }
    
    @Override
    public String getSenderID() {
        return senderID;
    }
    
    @Override
    public String getReceiverID() {
        return receiverID;
    }
    
    @Override
    public String getTransactionId() {
        return transactionId;
    }
    
    @Override
    public Object getHash() {
        return transactionId;
    }
    
    @Override
    public String getSummary() {
        return supplier + " " + status + " " + quantity + 
               " units of product " + productId + " to " + receiver;
    }
}
```

#### Voting Transaction Example

```java
public class VotingTransaction implements Transaction {
    private final String voter;
    private final String candidate;
    private final String electionId;
    private final long timestamp;
    private final String transactionId;
    private final String senderID;
    private final String receiverID;
    
    public VotingTransaction(String voter, String candidate, String electionId) {
        this.voter = voter;
        this.candidate = candidate;
        this.electionId = electionId;
        this.timestamp = System.currentTimeMillis();
        this.senderID = voter;
        this.receiverID = candidate;
        // Generate hash-based transaction ID
        this.transactionId = HashUtils.sha256(voter + candidate + electionId + timestamp);
    }
    
    @Override
    public boolean isValid() {
        return voter != null && candidate != null && electionId != null;
    }
    
    @Override
    public String getSender() {
        return voter;
    }
    
    @Override
    public String getReceiver() {
        return candidate;
    }
    
    @Override
    public String getSenderID() {
        return senderID;
    }
    
    @Override
    public String getReceiverID() {
        return receiverID;
    }
    
    @Override
    public String getTransactionId() {
        return transactionId;
    }
    
    @Override
    public Object getHash() {
        return transactionId;
    }
    
    @Override
    public String getSummary() {
        return "Vote from " + voter + " for " + candidate + 
               " in election " + electionId;
    }
}
```

### Best Practices for Transaction Design

1. **Immutability**: Make transaction objects immutable to prevent modification after creation.
2. **Validation Logic**: Include comprehensive validation in the `isValid()` method.
3. **Digital Signatures**: For production systems, consider adding digital signature verification.
4. **Data Minimization**: Only store essential data that needs to be on the blockchain.
5. **Privacy Considerations**: Be careful about storing personally identifiable information.

### Implementing Digital Signatures for Transactions

#### Step 1: Understand the SignedTransaction Interface

The `SignedTransaction` interface extends the base `Transaction` interface with methods for digital signature verification:

```java
public interface SignedTransaction extends Transaction {
    String getSignature();
    PublicKey getSenderPublicKey();
    boolean verifySignature();
}
```

#### Step 2: Generate Key Pairs for Transaction Signing

Use the `CryptoUtils` class to generate RSA key pairs for transaction signing:

```java
// Generate a new key pair for a user
KeyPair aliceKeyPair = CryptoUtils.generateKeyPair();
KeyPair bobKeyPair = CryptoUtils.generateKeyPair();

// Store these securely in a real application
```

#### Step 3: Create a Signed Transaction Implementation

```java
public class CustomSignedTransaction implements SignedTransaction {
    private final String sender;
    private final String receiver;
    private final String data;
    private final PublicKey senderPublicKey;
    private final String signature;
    
    public CustomSignedTransaction(String sender, String receiver, 
                                  String data, KeyPair senderKeyPair) {
        this.sender = sender;
        this.receiver = receiver;
        this.data = data;
        this.senderPublicKey = senderKeyPair.getPublic();
        
        // Sign the transaction data
        this.signature = CryptoUtils.signData(this.getSummary(), 
                                            senderKeyPair.getPrivate());
    }
    
    @Override
    public boolean isValid() {
        // Transaction is valid only if signature verification passes
        return sender != null && receiver != null && verifySignature();
    }
    
    @Override
    public String getSender() { return sender; }
    
    @Override
    public String getReceiver() { return receiver; }
    
    @Override
    public String getSummary() {
        return sender + ":" + receiver + ":" + data;
    }
    
    @Override
    public String getSignature() { return signature; }
    
    @Override
    public PublicKey getSenderPublicKey() { return senderPublicKey; }
    
    @Override
    public boolean verifySignature() {
        return CryptoUtils.verifySignature(getSummary(), signature, senderPublicKey);
    }
}
```

#### Step 4: Use Signed Transactions in Your Blockchain

```java
// Create a blockchain that works with signed transactions
Blockchain<SignedTransaction> blockchain = new Blockchain<>();

// Generate key pairs for users
KeyPair aliceKeyPair = CryptoUtils.generateKeyPair();
KeyPair bobKeyPair = CryptoUtils.generateKeyPair();

// Create and add signed transactions
SignedTransaction tx1 = new CustomSignedTransaction(
    "Alice", "Bob", "Transfer 100 coins", aliceKeyPair);
blockchain.addTransaction(tx1);

// The blockchain will automatically verify signatures during validation
```

#### Step 5: Verify Transaction Integrity

When validating the blockchain, each signed transaction's signature will be verified:

```java
// This will check all transaction signatures as part of validation
boolean isValid = blockchain.isChainValid();
```

### Digitally Signed Transactions

You can define secure, signed transactions by implementing the `SignedTransaction` interface, which extends `Transaction` and adds:

```java
public interface SignedTransaction extends Transaction {
    String getSignature();
    PublicKey getSenderPublicKey();
    boolean verifySignature();
}
```

Use `CryptoUtils` to handle signing and verification:

```java
KeyPair keyPair = CryptoUtils.generateKeyPair();
String summary = tx.getSummary();
String signature = CryptoUtils.signData(summary, keyPair.getPrivate());

boolean isVerified = CryptoUtils.verifySignature(summary, signature, keyPair.getPublic());
```

Include digital signature checks inside your isValid() method for secure validation.

---

## Consensus Algorithms

The consensus algorithm determines how blocks are created and validated in your blockchain. The framework provides a flexible interface for implementing different consensus mechanisms.

### Understanding the Consensus Interface

```java
public interface Consensus<T extends Transaction> {
    boolean validateBlock(Block<T> newBlock, Block<T> previousBlock);
    Block<T> generateBlock(List<T> txs, Block<T> previousBlock);
}
```

- `validateBlock()`: Determines if a block is valid according to consensus rules
- `generateBlock()`: Creates a new block from pending transactions

### Built-in Consensus: Proof of Work

The framework includes a basic implementation of Proof of Work consensus, which:

- Requires finding a block hash with a specified number of leading zeros
- Adjusts difficulty through the configuration system (see [Configuration System](#configuration-system))
- Uses SHA-256 for secure hashing

### Creating Custom Consensus Algorithms

Here are examples of alternative consensus mechanisms:

#### Proof of Authority Example

```java
public class ProofOfAuthority<T extends Transaction> implements Consensus<T> {
    private final List<String> authorizedValidators;
    private final String currentValidator;
    
    public ProofOfAuthority(List<String> validators, String currentValidator) {
        this.authorizedValidators = validators;
        this.currentValidator = currentValidator;
    }
    
    @Override
    public boolean validateBlock(Block<T> newBlock, Block<T> previousBlock) {
        // Check basic block integrity
        if (!newBlock.getPreviousHash().equals(previousBlock.getHash())) {
            return false;
        }
        
        // Validate block creator is authorized
        String blockCreator = extractBlockCreator(newBlock);
        return authorizedValidators.contains(blockCreator);
    }
    
    @Override
    public Block<T> generateBlock(List<T> txs, Block<T> previousBlock) {
        // Only authorized validators can generate blocks
        if (!authorizedValidators.contains(currentValidator)) {
            throw new IllegalStateException("Not authorized to generate blocks");
        }
        
        int index = previousBlock.getIndex() + 1;
        long timestamp = System.currentTimeMillis();
        
        // Include validator signature or identifier
        String hash = computeHash(index, previousBlock.getHash(), 
                                  timestamp, txs, currentValidator);
        
        return new Block<>(index, previousBlock.getHash(), timestamp, 
                          txs, 0, hash);
    }
    
    private String extractBlockCreator(Block<T> block) {
        // In a real implementation, this would extract a signature or 
        // identifier from the block that proves who created it
        return "validator1"; // Placeholder
    }
    
    private String computeHash(int index, String prevHash, long timestamp, 
                              List<T> txs, String validator) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String input = index + prevHash + timestamp + txs.hashCode() + validator;
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```

#### Practical Byzantine Fault Tolerance (Simplified Example)

```java
public class SimplifiedPBFT<T extends Transaction> implements Consensus<T> {
    private final List<String> nodes;
    private final int requiredConsensus;
    
    public SimplifiedPBFT(List<String> nodes) {
        this.nodes = nodes;
        // Typically 2f+1 nodes are required where f is the number of faulty nodes
        this.requiredConsensus = (2 * possibleFaultyNodes()) + 1;
    }
    
    private int possibleFaultyNodes() {
        return (nodes.size() - 1) / 3;
    }
    
    @Override
    public boolean validateBlock(Block<T> newBlock, Block<T> previousBlock) {
        // Basic validation
        if (!newBlock.getPreviousHash().equals(previousBlock.getHash())) {
            return false;
        }
        
        // In a real PBFT system, this would verify that the block has
        // digital signatures from a sufficient number of validators
        int validSignatures = countValidSignatures(newBlock);
        return validSignatures >= requiredConsensus;
    }
    
    @Override
    public Block<T> generateBlock(List<T> txs, Block<T> previousBlock) {
        int index = previousBlock.getIndex() + 1;
        long timestamp = System.currentTimeMillis();
        
        // In a real implementation, this would:
        // 1. Broadcast a pre-prepare message to all nodes
        // 2. Collect prepare messages from other nodes
        // 3. Broadcast commit messages
        // 4. Create the block when sufficient commitments received
        
        // Simplified version
        String hash = computeHash(index, previousBlock.getHash(), timestamp, txs);
        return new Block<>(index, previousBlock.getHash(), timestamp, txs, 0, hash);
    }
    
    private int countValidSignatures(Block<T> block) {
        // In a real implementation, this would verify digital signatures 
        // from the validators stored with the block
        return nodes.size(); // Placeholder
    }
    
    private String computeHash(int index, String prevHash, long timestamp, List<T> txs) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String input = index + prevHash + timestamp + txs.hashCode();
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```

### Consensus Algorithm Selection Criteria

When choosing or designing a consensus algorithm, consider:

1. **Trust Model**: Is your network permissioned or permissionless?
2. **Performance Requirements**: What are your transaction throughput needs?
3. **Energy Considerations**: Is power consumption a concern?
4. **Finality**: Do you need immediate transaction finality?
5. **Byzantine Fault Tolerance**: How many malicious nodes can your system tolerate?

---

## Block Structure

The framework's `Block` class provides a generic structure that can be extended or modified.

### The Block Class Structure

```java
public class Block<T extends Transaction> {
    private int index;
    private String previousHash;
    private long timestamp;
    private List<T> transactions;
    private int nonce;
    private String hash;
    
    // Constructor and getters...
}
```

### Extending the Block Class

For more advanced use cases, you might want to extend the Block class:

```java
public class EnhancedBlock<T extends Transaction> extends Block<T> {
    private final String blockProducer;
    private final byte[] signature;
    private final Map<String, String> metadata;
    
    public EnhancedBlock(int index, String previousHash, long timestamp, 
                         List<T> transactions, int nonce, String hash,
                         String blockProducer, byte[] signature,
                         Map<String, String> metadata) {
        super(index, previousHash, timestamp, transactions, nonce, hash);
        this.blockProducer = blockProducer;
        this.signature = signature;
        this.metadata = metadata;
    }
    
    // Additional getters...
    
    public String getBlockProducer() {
        return blockProducer;
    }
    
    public byte[] getSignature() {
        return signature;
    }
    
    public Map<String, String> getMetadata() {
        return metadata;
    }
}
```

To use this enhanced block, you would need to modify your consensus implementation to produce these enhanced blocks.

---

## Genesis Block Customization

The genesis block is the first block in any blockchain and defines the initial state of the system. The framework provides a flexible way to customize the genesis block through the `GenesisBlockFactory` interface.

### Understanding the GenesisBlockFactory Interface

The `GenesisBlockFactory` interface defines a contract for creating genesis blocks:

```java
public interface GenesisBlockFactory<T extends Transaction> {
    Block<T> createGenesisBlock();
}
```

This simple interface allows for multiple implementations with different genesis block creation strategies.

### Using the Default Genesis Block

By default, the blockchain uses a `DefaultGenesisBlockFactory` that creates a basic genesis block:

```java
// Default constructor uses DefaultGenesisBlockFactory internally
Blockchain<FinancialTransaction> blockchain = new Blockchain<>();
```

The default genesis block has:
- Index: 0
- Previous hash: "0"
- Timestamp: Current time
- No transactions
- Nonce: 0
- Hash: From BlockchainConfig (see [Configuration System](#configuration-system))

This means that you can change the genesis hash for all default blockchains by modifying the `genesis_hash` property in your configuration file or environment variables.

### Creating Custom Genesis Blocks

For more control over the genesis block, you can use the `CustomGenesisBlockFactory` with a builder pattern:

```java
// Create initial genesis transactions
List<FinancialTransaction> genesisTransactions = new ArrayList<>();
genesisTransactions.add(new FinancialTransaction("Genesis", "Alice", 1000));
genesisTransactions.add(new FinancialTransaction("Genesis", "Bob", 1000));

// Create custom genesis block factory
CustomGenesisBlockFactory<FinancialTransaction> customFactory = 
    CustomGenesisBlockFactory.<FinancialTransaction>builder()
        .withHash("CUSTOM_GENESIS_HASH_WITH_INITIAL_FUNDS")
        .withTransactions(genesisTransactions)
        .withMetadata("creator", "Satoshi")
        .withMetadata("version", "1.0")
        .build();

// Create blockchain with custom genesis
Blockchain<FinancialTransaction> customBlockchain = new Blockchain<>(customFactory);
```

The `CustomGenesisBlockFactory.Builder` provides methods for:

- `withHash(String)`: Sets a custom hash for the genesis block
- `withTransactions(List<T>)`: Sets initial transactions
- `addTransaction(T)`: Adds a single transaction
- `withPreviousHash(String)`: Sets the previous hash (default is "0")
- `withNonce(int)`: Sets the nonce value (default is 0)
- `withMetadata(String, Object)`: Adds metadata (not stored in the block, but available for custom implementations)

### Practical Use Cases for Custom Genesis Blocks

1. **Token Pre-allocation**

   When creating a cryptocurrency or token system, you often need to pre-allocate tokens:

   ```java
   CustomGenesisBlockFactory<FinancialTransaction> factory = 
       CustomGenesisBlockFactory.<FinancialTransaction>builder()
           .addTransaction(new FinancialTransaction("Genesis", "Foundation", 5000000))
           .addTransaction(new FinancialTransaction("Genesis", "Development", 3000000))
           .addTransaction(new FinancialTransaction("Genesis", "Marketing", 2000000))
           .build();
   ```

2. **Blockchain Identity and Metadata**

   Store information about the blockchain's purpose, creation date, or creator:

   ```java
   CustomGenesisBlockFactory<FinancialTransaction> factory = 
       CustomGenesisBlockFactory.<FinancialTransaction>builder()
           .withMetadata("name", "Educational Blockchain")
           .withMetadata("created", LocalDateTime.now().toString())
           .withMetadata("creator", "Blockchain University")
           .withMetadata("purpose", "Teaching blockchain concepts")
           .build();
   ```

3. **Testing Different Initial States**

   For testing, you might want different initial blockchain states:

   ```java
   // Test factory for empty initial state
   CustomGenesisBlockFactory<FinancialTransaction> emptyFactory = 
       CustomGenesisBlockFactory.<FinancialTransaction>builder().build();
       
   // Test factory for initial state with transactions
   CustomGenesisBlockFactory<FinancialTransaction> populatedFactory = 
       CustomGenesisBlockFactory.<FinancialTransaction>builder()
           .addTransaction(new FinancialTransaction("Genesis", "TestAccount", 1000))
           .build();
   ```

4. **Implementing Multiple Blockchain Instances**

   Different blockchain instances might need different genesis configurations:

   ```java
   // Main network
   CustomGenesisBlockFactory<FinancialTransaction> mainnetFactory = 
       CustomGenesisBlockFactory.<FinancialTransaction>builder()
           .withHash("MAINNET_GENESIS_HASH")
           .build();
           
   // Test network
   CustomGenesisBlockFactory<FinancialTransaction> testnetFactory = 
       CustomGenesisBlockFactory.<FinancialTransaction>builder()
           .withHash("TESTNET_GENESIS_HASH")
           .build();
   ```

---

## Configuration System

The blockchain framework provides a flexible configuration system that allows you to customize various parameters without changing code. This is particularly useful for running your blockchain in different environments (development, testing, production).

### Understanding the BlockchainConfig Class

The `BlockchainConfig` class is implemented as a singleton that manages configuration properties from multiple sources:

```java
// Get the default configuration
BlockchainConfig config = BlockchainConfig.getInstance();

// Get configuration with a specific file
BlockchainConfig customConfig = BlockchainConfig.getInstance("custom-config.properties");

// Access configuration values
int difficulty = config.getDifficulty();
String genesisHash = config.getGenesisHash();
```

### Configuration Properties

The current implementation supports the following configuration properties:

| Property             | Description                                      | Default            |
|---------------------|--------------------------------------------------|--------------------|
| `difficulty`         | Number of leading zeros required for PoW hashing | 4                  |
| `genesis_hash`       | Hash value used for the genesis block            | GENESIS_HASH       |
| `persistence.enabled`| Enable automatic blockchain persistence          | true               |
| `persistence.file`   | File path for blockchain persistence storage     | data/chain-data.json |

### Environment-Specific Configurations

You can create different configuration files for different environments:

1. **Default Configuration**: `blockchain.properties`
2. **Development Configuration**: `blockchain-dev.properties` 
3. **Production Configuration**: `blockchain-prod.properties`

Example of a development configuration file:

```properties
# Lower difficulty for faster development testing
difficulty=2
genesis_hash=DEV_GENESIS_HASH
```

Example of a production configuration file:

```properties
# Higher difficulty for production security
difficulty=6
genesis_hash=PROD_GENESIS_HASH
```

### Runtime Configuration Changes

The configuration can be changed at runtime using the following methods:

```java
BlockchainConfig config = BlockchainConfig.getInstance();

// Change configuration file
config.setConfigFile("blockchain-dev.properties");
config.reloadConfig();
```

### Adding Custom Configuration Properties

To extend the configuration system with your own properties:

1. Add private fields and getters to the `BlockchainConfig` class
2. Update the `loadConfig` method to load your custom properties
3. Add default values for your properties

Example of extending the configuration:

```java
// In BlockchainConfig.java
private static final int DEFAULT_MAX_BLOCK_SIZE = 1000000;
private int maxBlockSize;

private void loadConfig() {
    // Existing code...
    
    // Load max block size
    String maxBlockSizeEnv = System.getenv("BLOCKCHAIN_MAX_BLOCK_SIZE");
    if (maxBlockSizeEnv != null && !maxBlockSizeEnv.isEmpty()) {
        maxBlockSize = Integer.parseInt(maxBlockSizeEnv);
    } else if (configLoaded) {
        maxBlockSize = Integer.parseInt(
            properties.getProperty("max_block_size", 
            String.valueOf(DEFAULT_MAX_BLOCK_SIZE)));
    } else {
        maxBlockSize = DEFAULT_MAX_BLOCK_SIZE;
    }
}

public int getMaxBlockSize() {
    return maxBlockSize;
}
```

## REST API Customization

The framework now includes a Spring Boot REST API for interacting with the blockchain. You can customize this API to fit your specific needs.

### Understanding the BlockchainController

The `BlockchainController` class provides the REST endpoints for blockchain operations:

```java
@RestController
@RequestMapping("/api")
public class BlockchainController {
    private final Blockchain<FinancialTransaction> blockchain;
    private final ProofOfWork<FinancialTransaction> consensus;
    
    // Constructor and endpoints...
    
    @GetMapping("/chain")
    public List<Block<FinancialTransaction>> getBlockchain() {
        return blockchain.getChain();
    }
    
    @PostMapping("/transactions")
    public String addTransaction(@RequestBody FinancialTransaction tx) {
        boolean added = blockchain.addTransaction(tx);
        return added ? "Transaction added." : "Invalid transaction.";
    }
    
    @PostMapping("/mine")
    public String mineBlock() {
        // Mining logic...
    }
    
    @GetMapping("/pending")
    public List<FinancialTransaction> getPendingTransactions() {
        return blockchain.getPendingTransactions();
    }
    
    @GetMapping("/validate")
    public String validateChain() {
        return blockchain.isChainValid() ? "Chain is valid." : "Chain is invalid!";
    }
}
```

### Adding Custom Endpoints

You can extend the API by adding custom endpoints to the `BlockchainController` or by creating new controllers:

```java
// Add a new endpoint to get block by index
@GetMapping("/block/{index}")
public ResponseEntity<Block<FinancialTransaction>> getBlockByIndex(@PathVariable int index) {
    if (index < 0 || index >= blockchain.getChain().size()) {
        return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(blockchain.getChain().get(index));
}

// Add an endpoint to get transaction statistics
@GetMapping("/stats")
public Map<String, Object> getStatistics() {
    Map<String, Object> stats = new HashMap<>();
    stats.put("blockCount", blockchain.getChain().size());
    stats.put("pendingTransactions", blockchain.getPendingTransactions().size());
    
    // Calculate total transaction count
    int totalTx = blockchain.getChain().stream()
        .mapToInt(block -> block.getTransactions().size())
        .sum();
    stats.put("totalTransactions", totalTx);
    
    return stats;
}
```

### Customizing Response Formats

You can customize the response format by creating Data Transfer Objects (DTOs) that represent your data in a specific way:

```java
// Create a DTO for block responses
public class BlockDTO {
    private int index;
    private String hash;
    private long timestamp;
    private int transactionCount;
    private List<String> transactionIds;
    
    // Constructor, getters, setters...
    
    // Static factory method to convert Block to BlockDTO
    public static BlockDTO fromBlock(Block<FinancialTransaction> block) {
        BlockDTO dto = new BlockDTO();
        dto.setIndex(block.getIndex());
        dto.setHash(block.getHash());
        dto.setTimestamp(block.getTimestamp());
        dto.setTransactionCount(block.getTransactions().size());
        
        // Extract transaction IDs
        List<String> txIds = block.getTransactions().stream()
            .map(Transaction::getTransactionId)
            .collect(Collectors.toList());
        dto.setTransactionIds(txIds);
        
        return dto;
    }
}

// Use the DTO in your controller
@GetMapping("/blocks")
public List<BlockDTO> getBlocksWithCustomFormat() {
    return blockchain.getChain().stream()
        .map(BlockDTO::fromBlock)
        .collect(Collectors.toList());
}
```

### Securing the API

For production use, you should secure your API. Spring Security provides a comprehensive security framework:

```java
// Add Spring Security dependency to pom.xml
// <dependency>
//     <groupId>org.springframework.boot</groupId>
//     <artifactId>spring-boot-starter-security</artifactId>
// </dependency>

// Create a security configuration class
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/api/chain", "/api/block/*").permitAll()
                .anyRequest().authenticated()
            .and()
            .httpBasic();
    }
    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
            .withUser("admin")
            .password(passwordEncoder().encode("password"))
            .roles("ADMIN");
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

## Wallet Customization

The blockchain framework now includes a wallet management system that can be customized to fit your specific needs.

### Understanding the Wallet System

The wallet system consists of several key components:

- `Wallet`: Represents a user's wallet containing cryptographic keys
- `WalletList`: Manages the collection of wallets in the system
- `WalletController`: Provides REST API endpoints for wallet operations
- `WalletDTO`: Data Transfer Object for wallet information

### Customizing Wallet Functionality

#### Extending the Wallet Class

You can extend the `Wallet` class to add additional functionality:

```java
public class EnhancedWallet extends Wallet {
    private final Map<String, Double> assetBalances;
    
    public EnhancedWallet(String userId, String userName, KeyPair keyPair) {
        super(userId, userName, keyPair);
        this.assetBalances = new HashMap<>();
    }
    
    public void addAsset(String assetId, double amount) {
        assetBalances.put(assetId, getAssetBalance(assetId) + amount);
    }
    
    public double getAssetBalance(String assetId) {
        return assetBalances.getOrDefault(assetId, 0.0);
    }
    
    // Additional methods...
}
```

#### Creating Custom Wallet DTOs

You can create custom DTOs to expose additional wallet information:

```java
public class EnhancedWalletDTO extends WalletDTO {
    private final Map<String, Double> assetBalances;
    
    public EnhancedWalletDTO(EnhancedWallet wallet) {
        super(wallet);
        this.assetBalances = new HashMap<>(wallet.getAssetBalances());
    }
    
    public Map<String, Double> getAssetBalances() {
        return assetBalances;
    }
}
```

#### Adding Custom Wallet Endpoints

You can extend the `WalletController` or create a new controller to add custom endpoints:

```java
@RestController
@RequestMapping("/api/wallet-assets")
public class WalletAssetController {
    @Autowired
    private WalletList walletList;
    
    @PostMapping("/add")
    public ResponseEntity<String> addAsset(
            @RequestParam("userId") String userId,
            @RequestParam("assetId") String assetId,
            @RequestParam("amount") double amount,
            @RequestParam("privateKey") MultipartFile privateKeyFile) {
        
        // Implementation...
    }
    
    @GetMapping("/balance")
    public ResponseEntity<Map<String, Double>> getAssetBalances(
            @RequestParam("userId") String userId) {
        
        // Implementation...
    }
}
```

### Wallet Security Considerations

When customizing the wallet system, consider these security best practices:

1. **Never store private keys**: Private keys should never be stored on the server
2. **Use secure key generation**: Ensure proper entropy for key generation
3. **Implement proper authentication**: Verify ownership before sensitive operations
4. **Encrypt wallet backups**: Add encryption to exported wallet data
5. **Implement rate limiting**: Prevent brute force attacks on wallet endpoints

## Mempool Customization

The Mempool is a transaction pool that manages pending transactions awaiting inclusion in blocks. It provides thread-safe operations and transaction deduplication.

### Understanding the Mempool

```java
public class Mempool<T extends Transaction> {
    private final ConcurrentHashMap<Object, T> transactions;
    
    public Mempool() {
        this.transactions = new ConcurrentHashMap<>();
    }
    
    public boolean addTransaction(T transaction) {
        if (transaction == null || !transaction.isValid()) {
            return false;
        }
        
        // Use transaction hash as key for deduplication
        transactions.putIfAbsent(transaction.getHash(), transaction);
        return true;
    }
    
    public List<T> getTransactions() {
        return new ArrayList<>(transactions.values());
    }
    
    public void clear() {
        transactions.clear();
    }
    
    public void removeTransactions(List<T> processedTransactions) {
        for (T tx : processedTransactions) {
            transactions.remove(tx.getHash());
        }
    }
    
    public int size() {
        return transactions.size();
    }
    
    public boolean contains(T transaction) {
        return transactions.containsKey(transaction.getHash());
    }
}
```

### Using the Mempool

The Mempool is used to manage pending transactions before they are included in blocks:

```java
// Create a mempool for your transaction type
Mempool<FinancialTransaction> mempool = new Mempool<>();

// Add transactions to the mempool
mempool.addTransaction(new FinancialTransaction("Alice", "Bob", 100));
mempool.addTransaction(new FinancialTransaction("Charlie", "Dave", 75));

// Get all pending transactions
List<FinancialTransaction> pendingTransactions = mempool.getTransactions();

// Generate a block with pending transactions
Block<FinancialTransaction> newBlock = consensus.generateBlock(
        pendingTransactions.subList(0, Math.min(pendingTransactions.size(), maxTxPerBlock)),
        blockchain.getLastBlock()
);

// After adding the block to the blockchain, remove the processed transactions from the mempool
if (blockchain.addBlock(newBlock)) {
    mempool.removeTransactions(pendingTransactions);
}
```

### Customizing the Mempool

You can extend the Mempool class to add custom functionality:

```java
public class PriorityMempool<T extends Transaction> extends Mempool<T> {
    private final PriorityQueue<T> priorityQueue;
    
    public PriorityMempool(Comparator<T> comparator) {
        super();
        this.priorityQueue = new PriorityQueue<>(comparator);
    }
    
    @Override
    public boolean addTransaction(T transaction) {
        boolean added = super.addTransaction(transaction);
        if (added) {
            priorityQueue.add(transaction);
        }
        return added;
    }
    
    @Override
    public List<T> getTransactions() {
        // Return transactions in priority order
        List<T> result = new ArrayList<>(priorityQueue);
        return result;
    }
    
    @Override
    public void removeTransactions(List<T> processedTransactions) {
        super.removeTransactions(processedTransactions);
        priorityQueue.removeAll(processedTransactions);
    }
    
    @Override
    public void clear() {
        super.clear();
        priorityQueue.clear();
    }
}
```

## Advanced Customizations

### JSON Serialization and Persistence

The framework provides built-in support for JSON serialization of blockchain data, allowing you to save and load your blockchain state. Additionally, it now includes automatic persistence to save the blockchain state between application runs.

#### Using JsonUtils

The `JsonUtils` class provides utility methods for JSON serialization and deserialization:

```java
// Serialize an object to a JSON file
JsonUtils.writeToFile(myObject, new File("data.json"));

// Deserialize from a JSON file
MyClass obj = JsonUtils.readFromFile(new File("data.json"), MyClass.class);

// Convert object to JSON string
String json = JsonUtils.toJson(myObject);

// Parse JSON string to object
MyClass obj = JsonUtils.fromJson(jsonString, MyClass.class);
```

#### Exporting and Importing Blockchain

The `Blockchain` class provides methods for exporting to and importing from JSON files:

```java
// Export blockchain to JSON file
blockchain.exportToJson(new File("blockchain.json"));

// Import blockchain from JSON file
Blockchain<FinancialTransaction> importedChain = 
    Blockchain.importFromJson(new File("blockchain.json"), FinancialTransaction.class);
```

#### Using Automatic Persistence

The framework now includes a `PersistenceManager` utility class that handles automatic persistence:

```java
// Load blockchain from persistence storage
Optional<Blockchain<FinancialTransaction>> loadedChain = 
    PersistenceManager.loadBlockchain(FinancialTransaction.class);

// Save blockchain to persistence storage
PersistenceManager.saveBlockchain(blockchain);
```

In the REST API controller, persistence is handled automatically:

```java
// In BlockchainController constructor
PersistenceManager.loadBlockchain(FinancialTransaction.class)
    .ifPresent(loadedChain -> {
        this.blockchain.getChain().addAll(loadedChain.getChain());
        this.blockchain.getPendingTransactions().addAll(loadedChain.getPendingTransactions());
    });

// Before application shutdown
@PreDestroy
public void cleanup() {
    PersistenceManager.saveBlockchain(blockchain);
}
```

#### Creating JSON-Compatible Transaction Types

To ensure your custom transaction types work with JSON serialization:

1. Add the `@JsonIgnoreProperties(ignoreUnknown = true)` annotation to your class
2. Provide a no-argument constructor (required by Jackson)
3. Ensure all fields have proper getters and setters

```java
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomTransaction implements Transaction {
    private String sender;
    private String receiver;
    private String data;
    
    // Required for Jackson deserialization
    public CustomTransaction() {}
    
    public CustomTransaction(String sender, String receiver, String data) {
        this.sender = sender;
        this.receiver = receiver;
        this.data = data;
    }
    
    // Getters and setters
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
    public String getReceiver() { return receiver; }
    public void setReceiver(String receiver) { this.receiver = receiver; }
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
    
    @Override
    public boolean isValid() {
        return sender != null && receiver != null && data != null;
    }
    
    @Override
    public String getSummary() {
        return sender + ":" + receiver + ":" + data;
    }
}
```

### Implementing Merkle Trees

For more efficient verification of transactions within blocks, consider implementing a Merkle tree:

```java
public class MerkleTree<T extends Transaction> {
    private final List<String> leaves;
    private final String root;
    
    public MerkleTree(List<T> transactions) {
        // Convert transactions to hash strings
        this.leaves = transactions.stream()
            .map(tx -> hashTransaction(tx))
            .collect(Collectors.toList());
        
        // Build the tree and calculate root
        this.root = buildTree(leaves);
    }
    
    private String hashTransaction(T transaction) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String input = transaction.getSummary();
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private String buildTree(List<String> hashes) {
        if (hashes.size() == 1) {
            return hashes.get(0);
        }
        
        List<String> parents = new ArrayList<>();
        
        // Create parent nodes by combining child nodes
        for (int i = 0; i < hashes.size(); i += 2) {
            String left = hashes.get(i);
            String right = (i + 1 < hashes.size()) ? hashes.get(i + 1) : left;
            
            String combinedHash = hashPair(left, right);
            parents.add(combinedHash);
        }
        
        // Recursively build up the tree
        return buildTree(parents);
    }
    
    private String hashPair(String left, String right) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String combined = left + right;
            byte[] hash = digest.digest(combined.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public String getRoot() {
        return root;
    }
    
    // Method to verify a transaction is in the tree
    public boolean verifyTransaction(T transaction, String[] proof, int index) {
        // Implementation of verification logic
        // Would walk up the tree using the provided proof path
        return true; // Placeholder
    }
}
```

### Customizing Chain Persistence

The framework now includes automatic persistence through the `PersistenceManager` utility class, which saves and loads the blockchain state to/from a JSON file. If you need more advanced persistence options, you can implement a custom persistence layer:

```java
public interface BlockchainStorage<T extends Transaction> {
    void saveBlock(Block<T> block);
    Block<T> loadBlock(int index);
    List<Block<T>> loadAllBlocks();
    void savePendingTransactions(List<T> transactions);
    List<T> loadPendingTransactions();
}

// Simple file-based implementation
public class FileBasedStorage<T extends Transaction> implements BlockchainStorage<T> {
    private final String dataDirectory;
    
    // Implementation...
}
```

### Adding Network Communication

To create a distributed blockchain network, you can add P2P networking capabilities:

```java
public interface BlockchainNode<T extends Transaction> {
    void broadcastTransaction(T transaction);
    void broadcastBlock(Block<T> block);
    void syncChain();
    void addPeer(String peerAddress);
    void removePeer(String peerAddress);
    List<String> getPeers();
}

// WebSocket-based implementation
public class WebSocketBlockchainNode<T extends Transaction> implements BlockchainNode<T> {
    private final Blockchain<T> blockchain;
    private final Set<String> peers;
    private final WebSocketServer server;
    
    // Implementation...
}
```

## Testing Your Customizations

### Testing Transaction Types

When testing custom transaction types, focus on:

1. **Validation Logic**: Test that `isValid()` correctly identifies valid and invalid transactions
2. **Immutability**: Ensure transaction objects cannot be modified after creation
3. **Serialization**: Test that transactions can be properly serialized and deserialized

Example test:

```java
@Test
void testCustomTransactionValidation() {
    // Valid transaction
    CustomTransaction validTx = new CustomTransaction("Alice", "Bob", "Test data");
    assertTrue(validTx.isValid());
    
    // Invalid transaction (null sender)
    CustomTransaction invalidTx = new CustomTransaction(null, "Bob", "Test data");
    assertFalse(invalidTx.isValid());
}
```

### Testing Consensus Algorithms

When testing custom consensus algorithms, focus on:

1. **Block Generation**: Test that blocks are generated correctly
2. **Block Validation**: Test that valid blocks are accepted and invalid blocks are rejected
3. **Chain Integrity**: Test that the consensus algorithm maintains chain integrity

Example test:

```java
@Test
void testCustomConsensusValidation() {
    // Create a custom consensus algorithm
    List<String> validators = Arrays.asList("validator1", "validator2", "validator3");
    ProofOfAuthority<FinancialTransaction> consensus = 
        new ProofOfAuthority<>(validators, "validator1");
    
    // Create a blockchain with the custom consensus
    Blockchain<FinancialTransaction> blockchain = new Blockchain<>();
    
    // Add a transaction
    blockchain.addTransaction(new FinancialTransaction("Alice", "Bob", 100));
    
    // Generate a block
    Block<FinancialTransaction> block = consensus.generateBlock(
        blockchain.getPendingTransactions(), blockchain.getLastBlock());
    
    // Validate the block
    assertTrue(consensus.validateBlock(block, blockchain.getLastBlock()));
}
```

### Testing REST API Endpoints

When testing custom API endpoints, use Spring's `MockMvc`:

```java
@SpringBootTest
@AutoConfigureMockMvc
public class CustomApiTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void testCustomEndpoint() throws Exception {
        mockMvc.perform(get("/api/custom-endpoint"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.key").value("expectedValue"));
    }
}
```

### Integration Testing

Test how your customizations work together:

```java
@Test
void testEndToEndCustomization() {
    // Create custom components
    CustomConsensus<CustomTransaction> consensus = new CustomConsensus<>();
    Blockchain<CustomTransaction> blockchain = new Blockchain<>(consensus);
    
    // Add transactions
    blockchain.addTransaction(new CustomTransaction("Alice", "Bob", "Data 1"));
    blockchain.addTransaction(new CustomTransaction("Charlie", "Dave", "Data 2"));
    
    // Generate and add a block
    Block<CustomTransaction> block = consensus.generateBlock(
        blockchain.getPendingTransactions(), blockchain.getLastBlock());
    blockchain.addBlock(block);
    
    // Verify chain state
    assertEquals(2, blockchain.getChain().size());
    assertTrue(blockchain.isChainValid());
    assertEquals(0, blockchain.getPendingTransactions().size());
}
```

### Testing Edge Cases

Always test edge cases and error conditions:

```java
@Test
void testEmptyTransactionList() {
    CustomConsensus<CustomTransaction> consensus = new CustomConsensus<>();
    Blockchain<CustomTransaction> blockchain = new Blockchain<>(consensus);
    
    // Generate block with no transactions
    Block<CustomTransaction> block = consensus.generateBlock(
        new ArrayList<>(), blockchain.getLastBlock());
    
    // Should still be valid
    assertTrue(consensus.validateBlock(block, blockchain.getLastBlock()));
}

@Test
void testInvalidBlock() {
    CustomConsensus<CustomTransaction> consensus = new CustomConsensus<>();
    
    // Create valid blocks
    Block<CustomTransaction> previousBlock = new Block<>(0, "0", System.currentTimeMillis(),
        new ArrayList<>(), 0, "genesis");
    Block<CustomTransaction> validBlock = new Block<>(1, "genesis", System.currentTimeMillis(),
        new ArrayList<>(), 0, "valid");
    
    // Create invalid block (wrong previous hash)
    Block<CustomTransaction> invalidBlock = new Block<>(1, "wrong", System.currentTimeMillis(),
        new ArrayList<>(), 0, "invalid");
    
    // Validation results
    assertTrue(consensus.validateBlock(validBlock, previousBlock));
    assertFalse(consensus.validateBlock(invalidBlock, previousBlock));
}
```

### Testing Configuration Handling

Test that your custom configuration properties are loaded correctly:

```java
@Test
void testCustomConfigurationProperties() {
    // Create a temporary properties file
    File tempFile = File.createTempFile("test-config", ".properties");
    try (FileWriter writer = new FileWriter(tempFile)) {
        writer.write("custom_property=test_value\n");
        writer.write("difficulty=3\n");
    }
    
    // Load the configuration
    BlockchainConfig config = BlockchainConfig.getInstance(tempFile.getAbsolutePath());
    
    // Verify properties
    assertEquals(3, config.getDifficulty());
    assertEquals("test_value", config.getCustomProperty());
    
    // Clean up
    tempFile.delete();
}
```

## Best Practices

### Security Considerations

1. **Input Validation**: Always validate inputs, especially in API endpoints
2. **Digital Signatures**: Use digital signatures for transaction authentication
3. **Secure Key Management**: Never store private keys on the server
4. **Rate Limiting**: Implement rate limiting for API endpoints
5. **Secure Configuration**: Don't hardcode sensitive values in your code

### Performance Optimization

1. **Efficient Data Structures**: Use appropriate data structures for your use case
2. **Batch Processing**: Process transactions in batches when possible
3. **Caching**: Cache frequently accessed data
4. **Asynchronous Processing**: Use asynchronous processing for non-blocking operations
5. **Database Indexing**: If using a database, ensure proper indexing

### Code Organization

1. **Package Structure**: Organize code into logical packages
2. **Separation of Concerns**: Keep different responsibilities in different classes
3. **Interface-Based Design**: Design to interfaces, not implementations
4. **Documentation**: Document your code thoroughly
5. **Testing**: Write comprehensive tests for all components

## Next Steps

After customizing your blockchain, consider:

1. **Deployment**: Deploy your blockchain to a production environment
2. **Monitoring**: Add monitoring and alerting
3. **User Interface**: Create a web or mobile interface
4. **Documentation**: Create comprehensive documentation for users
5. **Community**: Build a community around your blockchain project