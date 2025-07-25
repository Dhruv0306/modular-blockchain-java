# Modular Blockchain Java

A lightweight, pluggable, and customizable **private blockchain framework** written in Java.

This project is designed for developers, researchers, and educators who want to **define their own consensus algorithms** and **transaction formats** while building a fully functional private blockchain.

> Think of this as a blockchain *engine*, not a coin. No tokens. Just logic.

---

## Table of Contents

- [Overview](#-overview)
- [Core Features](#-core-features)
- [Digital Signatures & Chain Validation](#-digital-signatures--chain-validation)
- [Automatic Persistence](#-automatic-persistence)
- [Wallet Management](#-wallet-management)
- [Mempool Management](#-mempool-management)
- [Architecture Overview](#️-architecture-overview)
- [Example Use Case](#-example-use-case)
- [How to Run](#-how-to-run)
- [CLI Interface](#-cli-interface)
- [Key Packages](#-key-packages)
- [Utility Classes](#-utility-classes)
- [Configuration](#️-configuration)
- [Logging](#-logging)
- [Customizing the Blockchain](#️-customizing-the-blockchain)
  - [Define Your Own Transaction Type](#-1-define-your-own-transaction-type)
  - [Implement or Plug in a Consensus Algorithm](#-2-implement-or-plug-in-a-consensus-algorithm)
  - [Customize Genesis Block](#-3-customize-genesis-block)
- [Testing](#-testing)
- [Planned Features](#-planned-features-future-phases)
- [Technologies Used](#️-technologies-used)
- [Contributing](#-contributing)
- [License](#-license)
- [Inspiration](#-inspiration)

---

## 🚀 Overview

`modular-blockchain-java` is a modular and extensible blockchain skeleton that allows you to:

- 🔧 Plug in your own **consensus algorithm** (e.g., Proof of Work, Authority, or Custom)
- 💼 Define your own **transaction structure**
- 🧠 Experiment with blockchain logic, block creation, and chain validation
- 🧪 Run a simple, in-memory blockchain for development or testing

---

## 🧱 Core Features

| Feature                     | Description                                                                  |
|----------------------------|------------------------------------------------------------------------------|
| 🧩 Pluggable Consensus      | Use or implement your own logic via `Consensus<T>` interface                 |
| 💬 Custom Transaction Type | Implement the `Transaction` interface to define your own domain-specific data |
| 🧠 Generic Blockchain Core  | Built using Java generics for flexibility and clean separation                |
| 🧪 In-Memory Blockchain     | Lightweight, runs without external dependencies                              |
| 🔐 SHA-256 Hashing          | Secure hashing mechanism for PoW/validation                                  |
| ⚙️ Environment-based Config | Customize difficulty and other parameters per environment                    |
| 🧿 Customizable Genesis     | Define your own genesis block with custom transactions and metadata          |
| 💾 JSON Serialization       | Export and import blockchain data to/from JSON files                          |
| 💿 Automatic Persistence    | Blockchain state is saved on shutdown and restored on startup                |
| 📝 Structured Logging       | SLF4J logging with environment-specific configurations                       |
| 🧪 Comprehensive Testing    | JUnit 5 test suite with high coverage for all components                     |
| 🌐 REST API                | Spring Boot REST API for blockchain interaction                              |
| 🖥️ CLI Interface          | Command-line interface for blockchain and wallet operations                   |
| 👛 Wallet Management       | Create, export, import, and manage cryptographic wallets                     |
| 🏊 Mempool Management      | Thread-safe transaction pool with deduplication and validation               |

---

## 🔐 Digital Signatures & Chain Validation

The blockchain now supports **transaction-level digital signatures** using RSA.

### Key Enhancements

- **SignedTransaction Interface**  
  Introduced `SignedTransaction` to define contracts for verifiable transactions.

- **SignedFinancialTransaction**  
  A concrete class implementing signature verification for financial transfers.

- **CryptoUtils**  
  Utility for RSA keypair generation, signing, and verifying messages.

- **Chain Validation**  
  `isChainValid()` now ensures:
  - All blocks are sequential
  - All hashes match computed ones
  - All signed transactions are verified

## 💾 Automatic Persistence

The blockchain now includes **automatic state persistence** between application runs.

### Key Features

- **PersistenceManager**  
  Utility class that handles saving and loading blockchain state.

- **Configurable Persistence**  
  Enable/disable persistence and specify storage location via configuration.

- **Automatic Lifecycle Integration**  
  Blockchain state is automatically saved on shutdown and loaded on startup.

- **Error Handling**  
  Graceful handling of missing, corrupted, or invalid persistence files.

- **Validation Before Persistence**  
  Only valid blockchains are persisted to prevent corruption.

## 👛 Wallet Management

The blockchain now includes a comprehensive wallet management system for secure transaction signing.

### Key Features

- **Wallet Generation**  
  Create wallets with secure RSA key pairs for transaction signing.

- **Wallet Import/Export**  
  Securely backup and restore wallets with authentication.

- **Key Management**  
  Download and manage public/private key pairs for transaction signing.

- **Authentication**  
  Private key authentication for sensitive wallet operations.

- **Persistence**  
  Automatic saving and loading of wallet data between application runs.

## 🏊 Mempool Management

The blockchain now includes a dedicated Mempool (transaction pool) component for efficient transaction management.

### Key Features

- **Thread-safe Transaction Pool**  
  Uses `ConcurrentHashMap` to manage pending transactions safely in a multi-threaded environment.

- **Transaction Deduplication**  
  Prevents duplicate transactions using content-addressable storage based on transaction hashes.

- **Transaction Validation**  
  Validates transactions before adding them to the pool.

- **Configurable Block Size**  
  Supports configurable maximum transactions per block via `blockchain.max_transactions_per_block` property.

- **Content-Addressable Storage**  
  Uses transaction hash as unique identifier through the new `getHash()` method in the `Transaction` interface.

---

## 🏗️ Architecture Overview

```mermaid
%%{init: {"themeVariables": { "fontFamily": "Roboto, sans-serif", "fontSize" : "17px" }}}%%
classDiagram
    class Mempool~T~ {
        -transactions ConcurrentHashMap
        +addTransaction(T) boolean
        +getTransactions() List~T~
        +clear() void
        +removeTransactions(List~T~) void
        +size() int
        +contains(T) boolean
    }
    
    class Transaction {
        <<interface>>
        +getTransactionId() String
        +getHash() Object
        +isValid() boolean
        +getSender() String
        +getReceiver() String
        +getSummary() String
    }
    
    class SignedTransaction {
        <<interface>>
        +getSignature() String
        +getSenderPublicKey() PublicKey
        +verifySignature() boolean
    }
    
    class Block~T~ {
        -index int
        -timestamp long
        -transactions List~T~
        -previousHash String
        -hash String
        -nonce int
    }
    
    class Consensus~T~ {
        <<interface>>
        +validateBlock(Block~T~, Block~T~) boolean
        +generateBlock(List~T~, Block~T~) Block~T~
    }
    
    class GenesisBlockFactory~T~ {
        <<interface>>
        +createGenesisBlock() Block~T~
    }
    
    class DefaultGenesisBlockFactory~T~ {
        +createGenesisBlock() Block~T~
    }
    
    class CustomGenesisBlockFactory~T~ {
        -hash String
        -transactions List~T~
        -metadata Map~String,String~
        +createGenesisBlock() Block~T~
    }
    
    class Blockchain~T~ {
        -chain List~Block~T~~
        -consensus Consensus~T~
        +addBlock(Block~T~) void
        +isChainValid() boolean
        +getBlockCount() int
        +exportToJson(File) void
        +importFromJson(File, Class) Blockchain~T~
    }
    
    class FinancialTransaction {
        -sender String
        -receiver String
        -amount double
        -transactionId String
        -senderID String
        -receiverID String
        +getAmount() double
        +getHash() Object
    }
    
    class SignedFinancialTransaction {
        -sender String
        -receiver String
        -amount double
        -senderPublicKey PublicKey
        -signature String
        -transactionId String
        -timestamp long
        -senderID String
        -receiverID String
        +getAmount() double
        +getTimestamp() long
        +getHash() Object
    }
    
    class ProofOfWork~T~ {
        +validateBlock(Block~T~, Block~T~) boolean
        +generateBlock(List~T~, Block~T~) Block~T~
    }
    
    class CryptoUtils {
        <<utility>>
        +generateKeyPair() KeyPair
        +signData(String, PrivateKey) String
        +verifySignature(String, String, PublicKey) boolean
    }
    
    class HashUtils {
        <<utility>>
        +computeHash(Block) String
        +computeHash(int, String, long, List, int) String
    }
    
    class ChainConfig {
        <<singleton>>
        -instance ChainConfig
        +getInstance() ChainConfig
        +getDifficulty() int
        +getGenesisHash() String
        +isPersistenceEnabled() boolean
        +getPersistenceFile() String
        +getMaxTransactionsPerBlock() int
    }
    
    class JsonUtils {
        <<utility>>
        +writeToFile(Object, File) void
        +readFromFile(File, Class) Object
        +toJson(Object) String
        +fromJson(String, Class) Object
    }
    
    class PersistenceManager~T~ {
        <<utility>>
        +loadBlockchain(Class) Optional~Blockchain~T~~
        +saveBlockchain(Blockchain) void
    }
    
    class BlockchainController {
        -blockchain Blockchain~FinancialTransaction~
        -consensus ProofOfWork~FinancialTransaction~
        -mempool Mempool~FinancialTransaction~
        +getBlockchain() List~Block~
        +addTransaction(FinancialTransaction) String
        +mineBlock() String
        +getPendingTransactions() List~FinancialTransaction~
        +validateChain() String
    }
    
    class BlockchainApplication {
        +main(String[]) void
    }
    
    class Wallet {
        -userId String
        -userName String
        -keyPair KeyPair
        +getPublicKey() PublicKey
        +getPrivateKey() PrivateKey
        +signData(String) String
    }
    
    class WalletList {
        -wallets Map~String, WalletEntry~
        +addWallet(Wallet) void
        +getWallet(String) Optional~Wallet~
        +getAllWallets() List~WalletEntry~
    }
    
    class WalletDTO {
        -userId String
        -userName String
        -publicKeyBase64 String
    }
    
    class WalletController {
        -walletList WalletList
        +createWallet(String, String) ResponseEntity
        +list() List~WalletDTO~
        +getPublicKeys() Map
        +exportWalletData(String, MultipartFile) ResponseEntity
        +importWallet(MultipartFile) ResponseEntity
        +deleteWallet(String, MultipartFile) ResponseEntity
    }
    
    Transaction <|-- SignedTransaction
    Transaction <|.. FinancialTransaction
    SignedTransaction <|.. SignedFinancialTransaction
    Consensus~T~ <|.. ProofOfWork~T~
    GenesisBlockFactory~T~ <|.. DefaultGenesisBlockFactory~T~
    GenesisBlockFactory~T~ <|.. CustomGenesisBlockFactory~T~
    Blockchain~T~ *-- Block~T~ : contains
    Blockchain~T~ --> Consensus~T~ : uses
    Blockchain~T~ --> JsonUtils : uses
    Blockchain~T~ --> PersistenceManager~T~ : uses
    Block~T~ *-- Transaction : contains
    SignedFinancialTransaction --> CryptoUtils : uses
    Block~T~ --> HashUtils : uses
    ProofOfWork~T~ --> HashUtils : uses
    ProofOfWork~T~ --> ChainConfig : uses
    PersistenceManager~T~ --> JsonUtils : uses
    PersistenceManager~T~ --> ChainConfig : uses
    BlockchainController --> Blockchain : uses
    BlockchainController --> ProofOfWork : uses
    BlockchainController --> PersistenceManager : uses
    BlockchainController --> Mempool : uses
    BlockchainApplication --> BlockchainController : uses
    Wallet --> CryptoUtils : uses
    WalletController --> WalletList : uses
    WalletController --> WalletDTO : creates
    WalletList *-- Wallet : contains
    
    %% Individual styling with colors at 60% opacity and bold text
    style Blockchain fill:#4A90E299,stroke:#2E5984,stroke-width:2px,color:#000,font-weight:bold
    style Block fill:#4A90E299,stroke:#2E5984,stroke-width:2px,color:#000,font-weight:bold
    style Transaction fill:#E74C3C99,stroke:#C0392B,stroke-width:2px,color:#000,font-weight:bold
    style SignedTransaction fill:#E74C3C99,stroke:#C0392B,stroke-width:2px,color:#000,font-weight:bold
    style Consensus fill:#E74C3C99,stroke:#C0392B,stroke-width:2px,color:#000,font-weight:bold
    style GenesisBlockFactory fill:#F39C1299,stroke:#D68910,stroke-width:2px,color:#000,font-weight:bold
    style DefaultGenesisBlockFactory fill:#F39C1299,stroke:#D68910,stroke-width:2px,color:#000,font-weight:bold
    style CustomGenesisBlockFactory fill:#F39C1299,stroke:#D68910,stroke-width:2px,color:#000,font-weight:bold
    style FinancialTransaction fill:#27AE6099,stroke:#1E8449,stroke-width:2px,color:#000,font-weight:bold
    style SignedFinancialTransaction fill:#27AE6099,stroke:#1E8449,stroke-width:2px,color:#000,font-weight:bold
    style ProofOfWork fill:#27AE6099,stroke:#1E8449,stroke-width:2px,color:#000,font-weight:bold
    style CryptoUtils fill:#9B59B699,stroke:#8E44AD,stroke-width:2px,color:#000,font-weight:bold
    style HashUtils fill:#9B59B699,stroke:#8E44AD,stroke-width:2px,color:#000,font-weight:bold
    style ChainConfig fill:#9B59B699,stroke:#8E44AD,stroke-width:2px,color:#000,font-weight:bold
    style JsonUtils fill:#9B59B699,stroke:#8E44AD,stroke-width:2px,color:#000,font-weight:bold
    style PersistenceManager fill:#9B59B699,stroke:#8E44AD,stroke-width:2px,color:#000,font-weight:bold
    style BlockchainController fill:#FF7F5099,stroke:#FF6347,stroke-width:2px,color:#000,font-weight:bold
    style BlockchainApplication fill:#FF7F5099,stroke:#FF6347,stroke-width:2px,color:#000,font-weight:bold
    style Wallet fill:#3498DB99,stroke:#2874A6,stroke-width:2px,color:#000,font-weight:bold
    style WalletList fill:#3498DB99,stroke:#2874A6,stroke-width:2px,color:#000,font-weight:bold
    style WalletDTO fill:#3498DB99,stroke:#2874A6,stroke-width:2px,color:#000,font-weight:bold
    style WalletController fill:#3498DB99,stroke:#2874A6,stroke-width:2px,color:#000,font-weight:bold
    style Mempool fill:#4A90E299,stroke:#2E5984,stroke-width:2px,color:#000,font-weight:bold
```

**Key Relationships:**
- `Blockchain<T>` orchestrates the entire system
- `Block<T>` contains transactions and links to previous blocks
- `Consensus<T>` defines block creation and validation rules
- `Transaction` interface allows custom transaction types
- `SignedTransaction` extends Transaction to add digital signature verification
- `GenesisBlockFactory<T>` creates the initial block
- `CustomGenesisBlockFactory<T>` provides customizable genesis block creation
- `BlockUtils` handles hash computation for blocks
- `CryptoUtils` manages cryptographic operations for digital signatures
- `Mempool<T>` manages pending transactions awaiting inclusion in blocks
- `BlockchainController` exposes blockchain operations via REST API
- `WalletController` manages wallet operations via REST API
- `Wallet` represents a user's cryptographic identity
- `BlockchainApplication` serves as the Spring Boot entry point

---

## 🔁 Example Use Case

Let's say you want to create a **financial ledger**. You would:

1. Implement your own `FinancialTransaction` class.
2. Use the built-in `ProofOfWork` consensus (or write your own).
3. Optionally create a custom genesis block with initial balances.
4. Add transactions and generate new blocks via the consensus plugin.
5. Export your blockchain to JSON for persistence or analysis.

Here's an example from the `Main.java`:

```java
// Create a new blockchain with financial transactions
Blockchain<FinancialTransaction> blockchain = new Blockchain<>();
// Use the built-in Proof of Work consensus algorithm
Consensus<FinancialTransaction> consensus = new ProofOfWork<>();
// Create a mempool to manage pending transactions
Mempool<FinancialTransaction> mempool = new Mempool<>();

// Add some sample transactions to the mempool
mempool.addTransaction(new FinancialTransaction("Alice", "Bob", 100));
mempool.addTransaction(new FinancialTransaction("Charlie", "Dave", 75));

// Get transactions from mempool for the new block
List<FinancialTransaction> pendingTransactions = mempool.getTransactions();

// Generate a new block with the pending transactions
Block<FinancialTransaction> newBlock = consensus.generateBlock(
        pendingTransactions,
        blockchain.getLastBlock()
);

// Validate and add the block to the chain
if (consensus.validateBlock(newBlock, blockchain.getLastBlock())) {
    blockchain.addBlock(newBlock);
    // Remove processed transactions from mempool
    mempool.removeTransactions(pendingTransactions);
    System.out.println("✅ Block added to chain");
}

// Export the blockchain to JSON file
try {
    blockchain.exportToJson(new File("blockchain.json"));
    System.out.println("✅ Blockchain exported to JSON");
    
    // Later, import the blockchain from JSON
    Blockchain<FinancialTransaction> importedChain = 
        Blockchain.importFromJson(new File("blockchain.json"), FinancialTransaction.class);
    System.out.println("✅ Blockchain imported with " + importedChain.getChain().size() + " blocks");
} catch (Exception e) {
    System.err.println("Error during JSON serialization: " + e.getMessage());
}
```

---

## 💻 How to Run

1. Clone this repo:

```bash
git clone https://github.com/yourusername/modular-blockchain-java.git
cd modular-blockchain-java
```

2. Build and run:

```bash
mvn clean install

# Run the core blockchain demo
mvn exec:java -Dexec.mainClass="com.example.blockchain.Main"

# OR run the Spring Boot REST API
mvn spring-boot:run
```

3. Use environment-specific configurations:

```bash
# Run with development configuration
mvn exec:java -Dexec.mainClass="com.example.blockchain.Main" -Dexec.args="blockchain-dev.properties"

# Or use environment variable
set BLOCKCHAIN_ENV=dev
mvn exec:java -Dexec.mainClass="com.example.blockchain.Main"
```

4. Use the CLI Interface:

```bash
# Start the CLI client (ensure REST API is running first)
java -cp target/classes com.example.blockchain.cli.ApiBasedBlockchainCLI

# Available CLI commands:
# - get-chain: View the blockchain
# - add-transaction: Add a new transaction
# - mine-block: Mine a new block
# - get-pending: View pending transactions
# - validate-chain: Validate the blockchain
# - create-wallet: Create a new wallet
# - get-public-keys: List all public keys
# - export-wallet: Export wallet data
# - import-wallet: Import wallet from file
# - delete-wallet: Delete a wallet
```

5. Access the REST API:

```
# View the blockchain
GET http://localhost:8080/api/chain

# Add a transaction
POST http://localhost:8080/api/transactions
Content-Type: application/json

{
  "sender": "Alice",
  "receiver": "Bob",
  "amount": 100
}

# Mine a new block
POST http://localhost:8080/api/mine

# View pending transactions
GET http://localhost:8080/api/pending

# Validate the blockchain
GET http://localhost:8080/api/validate
```

> 📘 For detailed run instructions including all CLI options, environment configurations, and troubleshooting tips, see the [Run Guide](docs/RunGuide.md)
> 
> 🖥️ For comprehensive CLI usage instructions, see the [CLI Guide](docs/CLIGuide.md)

---

## 🖥️ CLI Interface

The framework includes a user-friendly command-line interface for blockchain and wallet operations.

### Quick Start

1. **Start the REST API:**
```bash
mvn spring-boot:run
```

2. **Start the CLI (in separate terminal):**
```bash
java -cp target/classes com.example.blockchain.cli.ApiBasedBlockchainCLI
```

### Available Commands

| Command | Description |
|---------|-------------|
| `get-chain` | View the entire blockchain |
| `add-transaction` | Add a new transaction |
| `mine-block` | Mine a new block |
| `get-pending` | View pending transactions |
| `validate-chain` | Validate blockchain integrity |
| `create-wallet` | Create a new wallet |
| `get-public-keys` | List all public keys |
| `export-wallet` | Export wallet for backup |
| `import-wallet` | Import wallet from backup |
| `delete-wallet` | Delete a wallet |

### Example CLI Session

```bash
Enter command: create-wallet
Enter User ID: alice123
Enter User Name: Alice
✅ Wallet created successfully!

Enter command: add-transaction
Enter sender: Alice
Enter receiver: Bob
Enter amount: 100
✅ Transaction added successfully!

Enter command: mine-block
✅ Block mined successfully!
```

---

## 📦 Key Packages

| Package                     | Purpose                                 |
|----------------------------|-----------------------------------------|
| `com.example.blockchain.core`          | Core block, chain, transaction logic    |
| `com.example.blockchain.consensus`     | Interfaces and algorithms for consensus |
| `com.example.blockchain.transactions`  | Your custom transaction types           |
| `com.example.blockchain.core.utils`    | Core utilities including JSON serialization |
| `com.example.blockchain.crypto`        | Cryptographic utilities and signatures   |
| `com.example.blockchain.logging`       | Logging configuration and utilities     |
| `com.example.blockchain.api`           | REST API controllers and application    |
| `com.example.blockchain.cli`           | Command-line interface and utilities    |
| `com.example.blockchain.wallet`        | Wallet management components and controllers |
| `com.example.blockchain.mempool`       | Transaction pool management and processing |
| `com.example.blockchain.Main`          | Demo runner showing how it all works    |

## 🔧 Utility Classes

- `BlockUtils`: Encapsulates hash computation logic for blocks.
- `CryptoUtils`: Provides methods for RSA keypair generation, signing, and signature verification.
- `JsonUtils`: Handles JSON serialization and deserialization of blockchain data.

## ⚙️ Configuration

The blockchain uses a modular configuration system that allows for different settings per environment:

- **Default Configuration**: `blockchain.properties` in the root directory
- **Environment-specific**: `blockchain-dev.properties`, `blockchain-prod.properties`, etc.
- **Environment Variables**: `BLOCKCHAIN_DIFFICULTY`, `BLOCKCHAIN_GENESIS_HASH`
- **Runtime Selection**: Pass configuration file as command-line argument

### Configuration Properties

| Property             | Description                                      | Default            |
|---------------------|--------------------------------------------------|--------------------|
| `difficulty`         | Number of leading zeros required for PoW hashing | 4                  |
| `genesis_hash`       | Hash value used for the genesis block            | GENESIS_HASH       |
| `log_level`          | Logging level (TRACE, DEBUG, INFO, WARN, ERROR)  | INFO               |
| `persistence.enabled`| Enable automatic blockchain persistence          | true               |
| `persistence.file`   | File path for blockchain persistence storage     | data/chain-data.json |
| `max_transactions_per_block` | Maximum number of transactions in a block | 10                 |

### Usage in Code

```java
// Get the configuration singleton
BlockchainConfig config = BlockchainConfig.getInstance();

// Access configuration values
int difficulty = config.getDifficulty();
String genesisHash = config.getGenesisHash();

// Load a different configuration at runtime
config.setConfigFile("blockchain-dev.properties");
config.reloadConfig();
```

---

## 📝 Logging

The blockchain framework uses SLF4J with Logback for flexible and powerful logging:

- **Centralized Configuration**: Environment-specific logging settings
- **Multiple Log Levels**: TRACE, DEBUG, INFO, WARN, ERROR
- **File & Console Output**: Logs are written to both console and files
- **Configurable via Properties**: Change log levels without code modification

### Log Files

- `logs/blockchain.log`: Main log file (rotated daily)
- `logs/blockchain-dev.log`: Development environment logs (when using dev config)
- `logs/blockchain-prod.log`: Production environment logs (when using prod config)
- `logs/blockchain-error.log`: Error-specific logs (production only)

### Customizing Logs

You can configure logging behavior through:

1. **Configuration files**: Set the `log_level` property in blockchain.properties
2. **Environment-specific configs**: Use different logging profiles for dev/prod
3. **Runtime adjustment**: Use `LoggingUtils.setLogLevel()` to change log levels dynamically

Example of dynamically changing log levels:

```java
// Set blockchain package to DEBUG
LoggingUtils.setBlockchainLogLevel("DEBUG");

// Set a specific class to TRACE
LoggingUtils.setLogLevel("com.example.blockchain.consensus.ProofOfWork", "TRACE");
```

---

## ✍️ Customizing the Blockchain

For detailed instructions on customizing the blockchain, check out our [comprehensive customization guide](docs/CustomizationGuide.md).

### 🧱 1. Define Your Own Transaction Type

The project comes with a `FinancialTransaction` implementation, but you can create your own:

```java
public class CertificateTransaction implements Transaction {
    private String student;
    private String course;
    private String grade;
    private String transactionId;
    private String senderID;
    private String receiverID;

    public CertificateTransaction(String student, String course, String grade) {
        this.student = student;
        this.course = course;
        this.grade = grade;
        this.senderID = "institution";
        this.receiverID = student;
        // Generate hash-based transaction ID
        this.transactionId = HashUtils.sha256(student + course + grade);
    }

    public boolean isValid() {
        return student != null && course != null && grade != null;
    }

    public String getSender() {
        return "institution";
    }

    public String getReceiver() {
        return student;
    }
    
    public String getSenderID() {
        return senderID;
    }
    
    public String getReceiverID() {
        return receiverID;
    }

    public String getSummary() {
        return student + " earned " + grade + " in " + course;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public Object getHash() {
        return transactionId;
    }
    
    public String getType() {
        return "CERTIFICATE";
    }
}
```

#### Creating Secure Signed Transactions

For enhanced security, implement the `SignedTransaction` interface to add digital signature verification:

```java
public class MySignedTransaction implements SignedTransaction {
    private final String sender;
    private final String receiver;
    private final String data;
    private final PublicKey senderPublicKey;
    private final String signature;
    private final String transactionId;
    private final String senderID;
    private final String receiverID;
    
    // Constructor with signature generation
    public MySignedTransaction(String sender, String receiver, String data, 
                              KeyPair keyPair) throws Exception {
        this.sender = sender;
        this.receiver = receiver;
        this.data = data;
        this.senderID = sender;
        this.receiverID = receiver;
        this.senderPublicKey = keyPair.getPublic();
        this.signature = CryptoUtils.signData(this.getSummary(), keyPair.getPrivate());
        // Generate hash-based transaction ID
        this.transactionId = HashUtils.sha256(sender + receiver + data + 
                                             Base64.getEncoder().encodeToString(senderPublicKey.getEncoded()));
    }
    
    @Override
    public boolean isValid() {
        return sender != null && receiver != null && verifySignature();
    }
    
    @Override
    public String getSender() { return sender; }
    
    @Override
    public String getReceiver() { return receiver; }
    
    @Override
    public String getSenderID() { return senderID; }
    
    @Override
    public String getReceiverID() { return receiverID; }
    
    @Override
    public String getSummary() { return sender + ":" + receiver + ":" + data; }
    
    @Override
    public String getTransactionId() { return transactionId; }
    
    @Override
    public Object getHash() { return transactionId; }
    
    @Override
    public String getType() { return "SIGNED"; }
    
    @Override
    public String getSignature() { return signature; }
    
    @Override
    public PublicKey getSenderPublicKey() { return senderPublicKey; }
    
    @Override
    public boolean verifySignature() {
        return CryptoUtils.verifySignature(getSummary(), signature, senderPublicKey);
    }
}

---

### 🔁 2. Implement or Plug in a Consensus Algorithm

The project includes a `ProofOfWork` implementation with configurable difficulty. Here's how to implement your own:

```java
public class ProofOfStake<T extends Transaction> implements Consensus<T> {
    public boolean validateBlock(Block<T> newBlock, Block<T> previousBlock) {
        // Your custom validation logic here
    }

    public Block<T> generateBlock(List<T> txs, Block<T> previousBlock) {
        // Your block creation logic here
    }
}
```

---

### 🧿 3. Customize Genesis Block

The project allows customizing the genesis block (first block in the chain) through the `GenesisBlockFactory` interface:

```java
// Create a custom genesis block with initial transactions
CustomGenesisBlockFactory<FinancialTransaction> factory = 
    CustomGenesisBlockFactory.<FinancialTransaction>builder()
        .withHash("CUSTOM_GENESIS_HASH")
        .addTransaction(new FinancialTransaction("Genesis", "Alice", 1000))
        .addTransaction(new FinancialTransaction("Genesis", "Bob", 1000))
        .withMetadata("creator", "Satoshi")
        .build();

// Create blockchain with custom genesis block
Blockchain<FinancialTransaction> blockchain = new Blockchain<>(factory);
```

This allows for scenarios like:
- Pre-allocating tokens/assets in the genesis block
- Setting custom metadata for the blockchain's creation
- Creating different genesis blocks for different blockchain instances

---

## 🧪 Testing

The project includes comprehensive unit tests built with JUnit 5:

- `BlockchainTest`: Validates basic chain logic.
- `BlockUtilsTest`: Ensures consistent hash computation logic.
- `CryptoUtilsTest`: Verifies RSA key pair generation and digital signature functions.
- `SignedFinancialTransactionTest`: Confirms signature-based transaction validity.
- `BlockchainIntegrationTest`: Covers end-to-end scenarios including invalid chains.
- `BlockValidationTest`: Tests tampered transaction detection in blocks.
- `DynamicLoggingTest`: Verifies runtime log level changes.
- `BlockchainEdgeCasesTest`: Tests empty transaction lists and duplicate transactions.
- `ConfigErrorsTest`: Ensures robust handling of configuration errors.

Run the tests with:

```bash
mvn test
```

Generate test coverage reports with:

```bash
mvn verify
```

You can also run individual tests like:

```bash
mvn test -Dtest=CryptoUtilsTest
```

Or target a specific method:

```bash
mvn test -Dtest=SignedFinancialTransactionTest#testValidSignedTransaction
```

> 📘 For detailed instructions on running tests, debugging failures, and understanding mock transactions, see the [Test Guide](docs/TestGuide.md)


---

## 📚 Planned Features (Future Phases)

- 🌐 P2P networking using sockets or WebSocket
- 🧪 CLI-based or GUI simulation for testnets
- 📊 Enhanced web dashboard for monitoring the chain
- 🔄 Advanced DB-based storage (LevelDB, H2) beyond current JSON persistence
- 🔑 User authentication and wallet integration for the REST API

---

## 🛠️ Technologies Used

- Java 21 (as specified in pom.xml)
- Maven for build and dependency management
- Jackson for JSON serialization/deserialization
- JUnit 5 for testing
- JaCoCo for test coverage
- SHA-256 hashing
- Spring Boot for REST API

---

## 🤝 Contributing

Want to add a new consensus algorithm? Support SQLite or JSON file storage? Submit a pull request!  
This project is open to educational and experimental contributions.

> 📘 For guidelines on writing and running tests, see [`docs/TestGuide.md`](docs/TestGuide.md)

---

## 📄 License

MIT License. Use freely, modify deeply.

---

## 🧠 Inspiration

This project is inspired by the need for a **modular blockchain playground** — a tool that lets developers learn by building, not just by reading or cloning Web3 codebases.