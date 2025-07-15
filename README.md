# Modular Blockchain Java

A lightweight, pluggable, and customizable **private blockchain framework** written in Java.

This project is designed for developers, researchers, and educators who want to **define their own consensus algorithms** and **transaction formats** while building a fully functional private blockchain.

> Think of this as a blockchain *engine*, not a coin. No tokens. Just logic.

---

## Table of Contents

- [Overview](#-overview)
- [Core Features](#-core-features)
- [Architecture Overview](#️-architecture-overview)
- [Example Use Case](#-example-use-case)
- [How to Run](#-how-to-run)
- [Key Packages](#-key-packages)
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
| 📝 Structured Logging       | SLF4J logging with environment-specific configurations                       |
| 🧪 Comprehensive Testing    | JUnit 5 test suite with high coverage for all components                     |

---

## 🏗️ Architecture Overview

```text
Blockchain<T extends Transaction>
├── List<Block<T>> chain
├── List<T> pendingTransactions
├── Consensus<T> consensusPlugin
└── GenesisBlockFactory<T> genesisBlockFactory
```

- `Block<T>` holds a list of user-defined transactions
- `Transaction` is an interface that you implement
- `Consensus<T>` defines how blocks are created and validated
- `GenesisBlockFactory<T>` creates the initial block of the chain

---

## 🔁 Example Use Case

Let's say you want to create a **financial ledger**. You would:

1. Implement your own `FinancialTransaction` class.
2. Use the built-in `ProofOfWork` consensus (or write your own).
3. Optionally create a custom genesis block with initial balances.
4. Add transactions and generate new blocks via the consensus plugin.
5. Print or analyze your blockchain in memory.

Here's an example from the `Main.java`:

```java
// Create a new blockchain with financial transactions
Blockchain<FinancialTransaction> blockchain = new Blockchain<>();
// Use the built-in Proof of Work consensus algorithm
Consensus<FinancialTransaction> consensus = new ProofOfWork<>();

// Add some sample transactions
blockchain.addTransaction(new FinancialTransaction("Alice", "Bob", 100));
blockchain.addTransaction(new FinancialTransaction("Charlie", "Dave", 75));

// Generate a new block with the pending transactions
Block<FinancialTransaction> newBlock = consensus.generateBlock(
        blockchain.getPendingTransactions(),
        blockchain.getLastBlock()
);

// Validate and add the block to the chain
if (consensus.validateBlock(newBlock, blockchain.getLastBlock())) {
    blockchain.addBlock(newBlock);
    System.out.println("✅ Block added to chain");
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
mvn exec:java -Dexec.mainClass="com.example.blockchain.Main"
```

3. Use environment-specific configurations:

```bash
# Run with default configuration
mvn exec:java -Dexec.mainClass="com.example.blockchain.Main"

# Run with development configuration
mvn exec:java -Dexec.mainClass="com.example.blockchain.Main" -Dexec.args="blockchain-dev.properties"

# Run with production configuration
mvn exec:java -Dexec.mainClass="com.example.blockchain.Main" -Dexec.args="blockchain-prod.properties"

# Or use environment variable
set BLOCKCHAIN_ENV=dev
mvn exec:java -Dexec.mainClass="com.example.blockchain.Main"
```

4. Or use the convenience script:

```bash
# Make the script executable
chmod +x run-blockchain.sh

# Run with development environment
./run-blockchain.sh --env dev

# Run with production environment but debug logging
./run-blockchain.sh --env prod --log DEBUG
```

---

## 📦 Key Packages

| Package                     | Purpose                                 |
|----------------------------|-----------------------------------------|
| `com.example.blockchain.blockchain`    | Core block, chain, transaction logic    |
| `com.example.blockchain.consensus`     | Interfaces and algorithms for consensus |
| `com.example.blockchain.transactions`  | Your custom transaction types           |
| `com.example.blockchain.Main`          | Demo runner showing how it all works    |

## ⚙️ Configuration

The blockchain uses a modular configuration system that allows for different settings per environment:

- **Default Configuration**: `blockchain.properties` in the root directory
- **Environment-specific**: `blockchain-dev.properties`, `blockchain-prod.properties`, etc.
- **Environment Variables**: `BLOCKCHAIN_DIFFICULTY`, `BLOCKCHAIN_GENESIS_HASH`
- **Runtime Selection**: Pass configuration file as command-line argument

### Configuration Properties

| Property       | Description                                      | Default     |
|---------------|--------------------------------------------------|-------------|
| `difficulty`   | Number of leading zeros required for PoW hashing | 4           |
| `genesis_hash` | Hash value used for the genesis block            | GENESIS_HASH |
| `log_level`    | Logging level (TRACE, DEBUG, INFO, WARN, ERROR)  | INFO        |

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

    public boolean isValid() {
        return student != null && course != null && grade != null;
    }

    public String getSender() {
        return "institution";
    }

    public String getReceiver() {
        return student;
    }

    public String getSummary() {
        return student + " earned " + grade + " in " + course;
    }
}
```

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

- `BlockchainTest`: Tests the core blockchain functionality
- `FinancialTransactionTest`: Tests the transaction implementation
- `ProofOfWorkTest`: Tests the consensus algorithm

Run the tests with:

```bash
mvn test
```

Generate test coverage reports with:

```bash
mvn verify
```

---

## 📚 Planned Features (Future Phases)

- 🔄 JSON or DB-based persistent storage (LevelDB, H2)
- 🌐 P2P networking using sockets or WebSocket
- 🧪 CLI-based or GUI simulation for testnets
- 📊 Web dashboard for monitoring the chain

---

## 🛠️ Technologies Used

- Java 21 (as specified in pom.xml)
- Maven for build and dependency management
- JUnit 5 for testing
- JaCoCo for test coverage
- SHA-256 hashing
- Standard libraries only (Phase 1)

---

## 🤝 Contributing

Want to add a new consensus algorithm? Support SQLite or JSON file storage? Submit a pull request!  
This project is open to educational and experimental contributions.

---

## 📄 License

MIT License. Use freely, modify deeply.

---

## 🧠 Inspiration

This project is inspired by the need for a **modular blockchain playground** — a tool that lets developers learn by building, not just by reading or cloning Web3 codebases.
