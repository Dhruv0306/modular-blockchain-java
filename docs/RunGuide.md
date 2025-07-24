# Running the Modular Blockchain Java

This guide provides detailed instructions for running the Modular Blockchain Java framework in different environments and with various configuration options.

## Table of Contents

- [Basic Run Commands](#basic-run-commands)
  - [Using Maven](#using-maven)
  - [Using Convenience Scripts](#using-convenience-scripts)
- [CLI Interface](#cli-interface)
  - [Starting the CLI](#starting-the-cli)
  - [CLI Commands](#cli-commands)
- [Environment-Specific Configurations](#environment-specific-configurations)
  - [Using Configuration Files](#using-configuration-files)
  - [Using Environment Variables](#using-environment-variables)
  - [Using Convenience Scripts with Environment Options](#using-convenience-scripts-with-environment-options)
- [REST API](#rest-api)
  - [Running the API](#running-the-api)
  - [API Endpoints](#api-endpoints)
  - [Example API Usage](#example-api-usage)
- [Wallet API](#wallet-api)
  - [Wallet API Endpoints](#wallet-api-endpoints)
  - [Example Wallet API Usage](#example-wallet-api-usage)
- [Logging Options](#logging-options)
- [Advanced Configuration](#advanced-configuration)
  - [Custom Configuration Files](#custom-configuration-files)
  - [Runtime Configuration Changes](#runtime-configuration-changes)
- [Persistence Options](#persistence-options)
  - [Default Persistence](#default-persistence)
  - [Disabling Persistence](#disabling-persistence)
  - [Custom Persistence Location](#custom-persistence-location)
- [Running Tests](#running-tests)
- [Troubleshooting](#troubleshooting)

## Basic Run Commands

### Using Maven

```bash
# Build and run with default settings
mvn clean install

# Run the core blockchain demo
mvn exec:java -Dexec.mainClass="com.example.blockchain.Main"

# Run the Spring Boot REST API
mvn spring-boot:run
```

### Using Convenience Scripts

**For Linux/Mac**
```bash
# Make the script executable
chmod +x run-blockchain.sh

# Run with default settings
./run-blockchain.sh
```

**For Windows**
```bash
# Run with default settings (CMD)
run-blockchain.bat

# Run with default settings (PowerShell)
.\run-blockchain.bat
```

## CLI Interface

The blockchain framework includes a command-line interface for easy interaction with the blockchain and wallet management.

### Starting the CLI

1. **Start the REST API server first:**
```bash
mvn spring-boot:run
```

2. **In a separate terminal, start the CLI:**
```bash
# Ensure the project is built
mvn clean install

# Start the CLI client
java -cp target/classes com.example.blockchain.cli.ApiBasedBlockchainCLI
```

### CLI Commands

Once the CLI is running, you can use these commands:

**Blockchain Operations:**
```bash
get-chain          # View the entire blockchain
add-transaction    # Add a new transaction
mine-block         # Mine a new block
get-pending        # View pending transactions
validate-chain     # Validate blockchain integrity
```

**Wallet Operations:**
```bash
create-wallet      # Create a new wallet
get-public-keys    # List all public keys
get-public-key     # Get specific user's public key
export-wallet      # Export wallet for backup
import-wallet      # Import wallet from backup
delete-wallet      # Delete a wallet
```

**Example CLI Session:**
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

> 📘 For detailed CLI usage instructions, see the [CLI Guide](CLIGuide.md)

## Environment-Specific Configurations

The blockchain framework supports different configurations for development, testing, and production environments.

### Using Configuration Files

```bash
# Run with development configuration
mvn exec:java -Dexec.mainClass="com.example.blockchain.Main" -Dexec.args="blockchain-dev.properties"

# Run with production configuration
mvn exec:java -Dexec.mainClass="com.example.blockchain.Main" -Dexec.args="blockchain-prod.properties"
```

### Using Environment Variables

```bash
# Set environment variable and run
set BLOCKCHAIN_ENV=dev
mvn exec:java -Dexec.mainClass="com.example.blockchain.Main"
```

### Using Convenience Scripts with Environment Options

**For Linux/Mac**
```bash
# Run with development environment
./run-blockchain.sh --env dev

# Run with production environment
./run-blockchain.sh --env prod
```

**For Windows**
```bash
# Run with development environment (CMD)
run-blockchain.bat --env dev

# Run with development environment (PowerShell)
.\run-blockchain.bat --env dev

# Run with production environment (CMD)
run-blockchain.bat --env prod

# Run with production environment (PowerShell)
.\run-blockchain.bat --env prod
```

## REST API

The blockchain now includes a Spring Boot REST API for interacting with the blockchain through HTTP requests.

### Running the API

```bash
# Build the project
mvn clean install

# Run the Spring Boot application
mvn spring-boot:run
```

By default, the API will be available at `http://localhost:8080/api/`.

### API Endpoints

| Endpoint | Method | Description | Request Body | Response |
|----------|--------|-------------|-------------|----------|
| `/api/chain` | GET | Get the full blockchain | None | JSON array of blocks |
| `/api/transactions` | POST | Add a new transaction | Transaction JSON | Success/failure message |
| `/api/mine` | POST | Mine a new block | None | Success/failure message with block hash |
| `/api/pending` | GET | Get pending transactions | None | JSON array of transactions |
| `/api/validate` | GET | Validate the blockchain | None | Validation status message |

### Example API Usage

**View the blockchain:**
```bash
curl http://localhost:8080/api/chain
```

**Add a transaction:**
```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{"sender": "Alice", "receiver": "Bob", "amount": 100}'
```

**Mine a new block:**
```bash
curl -X POST http://localhost:8080/api/mine
```

**View pending transactions:**
```bash
curl http://localhost:8080/api/pending
```

**Validate the blockchain:**
```bash
curl http://localhost:8080/api/validate
```

## Wallet API

The blockchain now includes a wallet management system accessible through REST API endpoints.

### Wallet API Endpoints

| Endpoint | Method | Description | Request Parameters | Response |
|----------|--------|-------------|-------------------|----------|
| `/api/wallets/generate` | POST | Create a new wallet | `userId`, `userName` | Wallet details and key files |
| `/api/wallets` | GET | List all wallets | None | JSON array of wallet DTOs |
| `/api/wallets/public-keys` | GET | Get all public keys | None | Map of user IDs to public keys |
| `/api/wallets/public-key` | GET | Get public key for user | `userId` | Public key string |
| `/api/wallets/export` | GET | Export wallet data | `userId`, `privateKey` | Wallet data file |
| `/api/wallets/import` | POST | Import wallet from file | `file` | Success/failure message |
| `/api/wallets/delete` | DELETE | Delete a wallet | `userId`, `privateKey` | Success/failure message |

### Example Wallet API Usage

**Create a new wallet:**
```bash
curl -X POST http://localhost:8080/api/wallets/generate \
  -F "userId=alice123" \
  -F "userName=Alice"
```

**List all wallets:**
```bash
curl http://localhost:8080/api/wallets
```

**Get a user's public key:**
```bash
curl http://localhost:8080/api/wallets/public-key?userId=alice123
```

**Export wallet data (requires authentication):**
```bash
curl -X GET http://localhost:8080/api/wallets/export \
  -F "userId=alice123" \
  -F "privateKey=@/path/to/private_key.pem"
```

**Import wallet from file:**
```bash
curl -X POST http://localhost:8080/api/wallets/import \
  -F "file=@/path/to/wallet_backup.json"
```

**Delete wallet (requires authentication):**
```bash
curl -X DELETE http://localhost:8080/api/wallets/delete \
  -F "userId=alice123" \
  -F "privateKey=@/path/to/private_key.pem"
```

## Logging Options

You can control the logging level when running the blockchain.

### Using Configuration Files

Set the `log_level` property in your configuration file:

```properties
# In blockchain-dev.properties
log_level=DEBUG

# In blockchain-prod.properties
log_level=INFO
```

### Using Convenience Scripts with Logging Options

**For Linux/Mac**
```bash
# Run with debug logging
./run-blockchain.sh --log DEBUG

# Run with production environment but debug logging
./run-blockchain.sh --env prod --log DEBUG
```

**For Windows**
```bash
# Run with debug logging (CMD)
run-blockchain.bat --log DEBUG

# Run with debug logging (PowerShell)
.\run-blockchain.bat --log DEBUG

# Run with production environment but debug logging (CMD)
run-blockchain.bat --env prod --log DEBUG

# Run with production environment but debug logging (PowerShell)
.\run-blockchain.bat --env prod --log DEBUG
```

## Advanced Configuration

### Mempool Configuration

The Mempool (transaction pool) can be configured using the `max_transactions_per_block` property, which controls how many transactions can be included in a single block when mining:

```properties
# Set maximum transactions per block
max_transactions_per_block=20
```

Or by using environment variables:

```bash
set BLOCKCHAIN_MAX_TRANSACTIONS_PER_BLOCK=20
mvn exec:java -Dexec.mainClass="com.example.blockchain.Main"
```

### Custom Configuration Files

You can create your own configuration files with custom settings:

```bash
# Create a custom configuration file
echo "difficulty=3" > my-custom-config.properties
echo "genesis_hash=CUSTOM_HASH" >> my-custom-config.properties
echo "persistence.enabled=true" >> my-custom-config.properties
echo "persistence.file=data/my-custom-chain.json" >> my-custom-config.properties
echo "max_transactions_per_block=20" >> my-custom-config.properties

# Run with custom configuration
mvn exec:java -Dexec.mainClass="com.example.blockchain.Main" -Dexec.args="my-custom-config.properties"
```

### Runtime Configuration Changes

You can also change configuration at runtime through the API:

```java
// Get the configuration singleton
BlockchainConfig config = BlockchainConfig.getInstance();

// Change configuration file
config.setConfigFile("blockchain-dev.properties");
config.reloadConfig();

// Access configuration values
int difficulty = config.getDifficulty();
String genesisHash = config.getGenesisHash();
boolean persistenceEnabled = config.isPersistenceEnabled();
String persistenceFile = config.getPersistenceFile();
int maxTransactionsPerBlock = config.getMaxTransactionsPerBlock();
```

## Persistence Options

The blockchain now supports automatic persistence of the blockchain state between application runs.

### Default Persistence

By default, persistence is enabled and the blockchain state is saved to `data/chain-data.json` when the application shuts down and loaded from the same file when it starts up.

### Disabling Persistence

You can disable persistence by setting the `persistence.enabled` property to `false` in your configuration file:

```properties
# Disable persistence
persistence.enabled=false
```

Or by using environment variables:

```bash
set BLOCKCHAIN_PERSISTENCE_ENABLED=false
mvn exec:java -Dexec.mainClass="com.example.blockchain.Main"
```

### Custom Persistence Location

You can specify a custom location for the persistence file using the `persistence.file` property:

```properties
# Custom persistence file location
persistence.file=C:/blockchain/my-blockchain-data.json
```

Or by using environment variables:

```bash
set BLOCKCHAIN_PERSISTENCE_FILE=C:/blockchain/my-blockchain-data.json
mvn exec:java -Dexec.mainClass="com.example.blockchain.Main"
```

## Running Tests

### Running All Tests

To run all tests in the project:

```bash
mvn test
```

### Running Specific Tests

To run a specific test class:

```bash
mvn test -Dtest=BlockchainTest
```

To run multiple test classes:

```bash
mvn test -Dtest=BlockchainTest,BlockValidationTest
```

To run a specific test method:

```bash
mvn test -Dtest=BlockchainTest#testAddBlock
```

### Running Tests with Different Configurations

To run tests with a specific environment configuration:

```bash
BLOCKCHAIN_ENV=dev mvn test
```

### Generating Test Coverage Reports

To generate test coverage reports:

```bash
mvn verify
```

The HTML coverage report will be available at `target/site/jacoco/index.html`.

## Troubleshooting

### Common Issues

1. **Configuration Not Found**
   
   If you see an error about configuration file not found:
   
   ```
   Error: Could not find configuration file: blockchain-dev.properties
   ```
   
   Make sure the file exists in the correct location (project root directory).

2. **Java Version Issues**
   
   The project requires Java 21. If you see compatibility errors, check your Java version:
   
   ```bash
   java -version
   ```

3. **Script Permission Issues (Linux/Mac)**
   
   If you can't execute the script:
   
   ```bash
   chmod +x run-blockchain.sh
   ```

4. **Test Failures**

   If tests are failing, check the detailed reports:

   ```bash
   # View test reports
   cat target/surefire-reports/TEST-com.example.blockchain.BlockchainTest.txt
   ```

   Enable debug logging in tests for more information:

   ```java
   @BeforeEach
   void setupLogging() {
       LoggingUtils.setBlockchainLogLevel("DEBUG");
   }
   ```

5. **CLI Issues**

   **CLI won't start:**
   ```bash
   # Ensure project is built
   mvn clean install
   
   # Check Java classpath
   java -cp target/classes com.example.blockchain.cli.ApiBasedBlockchainCLI
   ```
   
   **Connection errors:**
   ```bash
   # Verify REST API is running
   curl http://localhost:8080/api/chain
   
   # Check if port 8080 is available
   netstat -an | grep 8080
   ```
   
   **Wallet file errors:**
   ```bash
   # Check wallet directory exists
   ls -la wallets/
   
   # Verify file permissions
   ls -la wallets/alice123/
   ```

6. **REST API Issues**

   If you're having trouble with the REST API:
   
   - Ensure Spring Boot is running (check console output)
   - Verify the port is not in use by another application
   - Check that your JSON request body is properly formatted
   - Look for error messages in the Spring Boot console output

### Getting Help

If you encounter issues not covered in this guide, please:

1. Check the project logs in the `logs/` directory
2. Review the [README.md](../README.md) for general information
3. Review the [Test Guide](TestGuide.md) for testing-specific issues
4. Submit an issue on the project repository