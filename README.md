# Modular Blockchain Java

A lightweight, pluggable, and customizable **private blockchain framework** written in Java.

This project is designed for developers, researchers, and educators who want to **define their own consensus algorithms** and **transaction formats** while building a fully functional private blockchain.

> Think of this as a blockchain *engine*, not a coin. No tokens. Just logic.

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

---

## 🏗️ Architecture Overview

```text
Blockchain<T extends Transaction>
├── List<Block<T>> chain
├── List<T> pendingTransactions
└── Consensus<T> consensusPlugin
```

- `Block<T>` holds a list of user-defined transactions
- `Transaction` is an interface that you implement
- `Consensus<T>` defines how blocks are created and validated

---

## 🔁 Example Use Case

Let’s say you want to create a **financial ledger**. You would:

1. Implement your own `FinancialTransaction` class.
2. Use the built-in `ProofOfWork` consensus (or write your own).
3. Add transactions and generate new blocks via the consensus plugin.
4. Print or analyze your blockchain in memory.

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
mvn exec:java -Dexec.mainClass="com.yourname.blockchain.Main"
```

---

## 📦 Key Packages

| Package                     | Purpose                                 |
|----------------------------|-----------------------------------------|
| `blockchain`               | Core block, chain, transaction logic    |
| `consensus`                | Interfaces and algorithms for consensus |
| `transactions`             | Your custom transaction types           |
| `Main.java`                | Demo runner showing how it all works    |

---

## ✍️ Customizing the Blockchain

### 🧱 1. Define Your Own Transaction Type

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

## 📚 Planned Features (Future Phases)

- 🔄 JSON or DB-based persistent storage (LevelDB, H2)
- 🌐 P2P networking using sockets or WebSocket
- 🧪 CLI-based or GUI simulation for testnets
- 📊 Web dashboard for monitoring the chain

---

## 🛠️ Technologies Used

- Java 17+
- Maven
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
