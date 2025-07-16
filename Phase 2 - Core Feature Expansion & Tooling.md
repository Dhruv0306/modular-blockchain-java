## 🔄 Phase 2: Core Feature Expansion & Tooling

| #  | Task                                                 | Description                                                                 | Suggested Branch Name                 |
|----|------------------------------------------------------|-----------------------------------------------------------------------------|----------------------------------------|
| 1  | 🧩 **JSON Serialization for Blocks and Transactions** | Add methods to serialize/deserialize blocks and transactions to/from JSON   | `feature/json-serialization`          |
| 2  | 💾 **Persistent Storage**                             | Store blockchain data to disk (e.g., as JSON or custom flat files)          | `feature/file-storage-persistence`    |
| 3  | 🌐 **REST API Interface (Spring Boot)**               | Expose endpoints for adding transactions, viewing chain, mining, etc.       | `feature/spring-api-controller`       |
| 4  | 🔐 **Wallet System & Key Management**                 | Generate/manage public/private keys, sign transactions securely             | `feature/wallet-support`              |
| 5  | 🕒 **Transaction Pool / Mempool**                     | Manage unconfirmed transactions waiting to be mined                         | `feature/txpool-mempool-sim`          |
| 6  | 🌍 **Multi-Node Simulation**                          | Simulate syncing between blockchain nodes (mock networking)                 | `feature/multi-node-sync`             |
| 7  | 🧑‍💻 **CLI Runner**                                   | Create a `BlockchainCLI.java` for interactive terminal-based operations     | `feature/cli-interface`               |
| 8  | 📤 **JSON Import/Export CLI Commands**                | Commands to export and import chain data to/from JSON files                 | `feature/json-cli-commands`           |
| 9  | 🧭 **Visual Block Explorer (Optional)**               | Build a lightweight frontend to visualize blockchain activity               | `feature/block-explorer-ui`           |

### 🔧 Dependencies & Enhancements
- Consider using **Jackson** or **Gson** for JSON support.
- Use **Spring Boot** with **Spring Web** for REST APIs.
- Abstract I/O interfaces to allow switching from file to database later.
- Include **unit tests and integration tests** in each feature branch.
- Integrate with your existing logging and configuration system.

> 🧩 Starting: JSON Serialization for Blocks and Transactions (Task 1)
