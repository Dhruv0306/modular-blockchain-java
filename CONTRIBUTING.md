# Contributing to Modular Blockchain Java

Thank you for your interest in contributing to the Modular Blockchain Java project! This guide will help you get started with development, testing, and contributing to the project.

## Table of Contents

- [Development Setup](#development-setup)
- [Project Structure](#project-structure)
- [Building and Running](#building-and-running)
- [Testing](#testing)
- [Code Style and Formatting](#code-style-and-formatting)
- [Creating New Modules](#creating-new-modules)
- [Pull Request Process](#pull-request-process)

## Development Setup

### Prerequisites

- Java 21 or higher
- Maven 3.6 or higher
- Git

### Getting Started

1. Fork the repository on GitHub
2. Clone your fork locally:
   ```bash
   git clone https://github.com/yourusername/modular-blockchain-java.git
   cd modular-blockchain-java
   ```
3. Add the upstream repository as a remote:
   ```bash
   git remote add upstream https://github.com/original-owner/modular-blockchain-java.git
   ```
4. Create a branch for your work:
   ```bash
   git checkout -b feature/your-feature-name
   ```

## Project Structure

The project follows a standard Maven structure:

- `src/main/java/com/example/blockchain/` - Main source code
  - `blockchain/` - Core blockchain components
  - `consensus/` - Consensus algorithm implementations
  - `transactions/` - Transaction type implementations
  - `crypto/` - Utilities for digital signatures and cryptographic operations
  - `logging/` - Logging utilities
- `src/test/java/com/example/blockchain/` - Test code
- `docs/` - Documentation
- `logs/` - Log files (generated at runtime)

## Building and Running

### Building the Project

```bash
mvn clean install
```

### Running the Project

```bash
# Run with default configuration
mvn exec:java -Dexec.mainClass="com.example.blockchain.Main"

# Run with development configuration
mvn exec:java -Dexec.mainClass="com.example.blockchain.Main" -Dexec.args="blockchain-dev.properties"

# Run with production configuration
mvn exec:java -Dexec.mainClass="com.example.blockchain.Main" -Dexec.args="blockchain-prod.properties"
```

### Using the Convenience Script

On Unix-like systems, you can use the provided script:

```bash
# Make the script executable
chmod +x run-blockchain.sh

# Run with development environment
./run-blockchain.sh --env dev

# Run with production environment but debug logging
./run-blockchain.sh --env prod --log DEBUG
```

## Testing

### ✅ Test Class Overview

| **Test Class**                     | **Target Class/Module**            | **Purpose**                                                                 |
|-----------------------------------|------------------------------------|-----------------------------------------------------------------------------|
| `BlockchainTest`                  | `Blockchain`                       | Unit tests for adding transactions, adding blocks, and validating chain integrity. |
| `FinancialTransactionTest`       | `FinancialTransaction`             | Tests transaction validation rules and summary string formatting.          |
| `ProofOfWorkTest`                | `ProofOfWork`                      | Verifies block mining and block validation using proof-of-work consensus.  |
| `SignedFinancialTransactionTest` | `SignedFinancialTransaction`       | Tests digital signature verification, key binding, and validity enforcement. |
| `CryptoUtilsTest`                | `CryptoUtils`                      | Validates RSA key generation, message signing, and signature verification. |
| `BlockUtilsTest`                 | `BlockUtils`                       | Ensures consistent hashing of blocks using utility methods.                |
| `BlockchainIntegrationTest`      | `Blockchain + ProofOfWork`         | Tests end-to-end blockchain operations, tampering detection, and chain robustness. |


### Running Tests

```bash
# Run all tests
mvn test

# Run a specific test class
mvn test -Dtest=BlockchainTest

# Generate test coverage report
mvn verify
```

### Writing Tests

- All tests should be written using JUnit 5
- Place test classes in the same package structure as the classes they test
- Name test classes with the suffix `Test` (e.g., `BlockchainTest`)
- Use descriptive test method names that explain what is being tested
- Follow the AAA pattern (Arrange, Act, Assert)

Example test:

```java
@Test
void shouldValidateValidTransaction() {
    // Arrange
    FinancialTransaction transaction = new FinancialTransaction("Alice", "Bob", 100);
    
    // Act
    boolean isValid = transaction.isValid();
    
    // Assert
    assertTrue(isValid);
}
```

### Test Coverage

The project uses JaCoCo for test coverage reporting. After running `mvn verify`, you can find the coverage report at:

```
target/site/jacoco/index.html
```

Aim for at least 80% test coverage for new code.

## Code Style and Formatting

### Java Code Style

- Use 4 spaces for indentation (not tabs)
- Follow standard Java naming conventions:
  - `camelCase` for variables and methods
  - `PascalCase` for classes and interfaces
  - `UPPER_SNAKE_CASE` for constants
- Keep lines under 100 characters when possible
- Add JavaDoc comments for all public classes and methods

### Commit Messages

- Use the imperative mood ("Add feature" not "Added feature")
- First line should be 50 characters or less
- Include a reference to the issue number if applicable
- Example: `Add ProofOfStake consensus algorithm (#42)`

## Creating New Modules

### Adding a New Transaction Type

1. Create a new class in the `com.example.blockchain.transactions` package
2. Implement the `Transaction` interface
3. Implement all required methods:
   - `isValid()`
   - `getSender()`
   - `getReceiver()`
   - `getSummary()`
4. Add unit tests for your transaction type

Example:

```java
public class AssetTransaction implements Transaction {
    private final String owner;
    private final String recipient;
    private final String assetId;
    
    // Constructor and methods...
    
    @Override
    public boolean isValid() {
        return owner != null && recipient != null && assetId != null;
    }
    
    @Override
    public String getSender() {
        return owner;
    }
    
    @Override
    public String getReceiver() {
        return recipient;
    }
    
    @Override
    public String getSummary() {
        return "Asset " + assetId + " transferred from " + owner + " to " + recipient;
    }
}
```

### Implementing a New Consensus Algorithm

1. Create a new class in the `com.example.blockchain.consensus` package
2. Implement the `Consensus<T>` interface
3. Implement the required methods:
   - `validateBlock(Block<T>, Block<T>)`
   - `generateBlock(List<T>, Block<T>)`
4. Add unit tests for your consensus algorithm

Example:

```java
public class ProofOfStake<T extends Transaction> implements Consensus<T> {
    // Fields and constructor...
    
    @Override
    public boolean validateBlock(Block<T> newBlock, Block<T> previousBlock) {
        // Validation logic...
    }
    
    @Override
    public Block<T> generateBlock(List<T> transactions, Block<T> previousBlock) {
        // Block generation logic...
    }
}
```

### Adding a New Feature Module

For larger features that don't fit into existing modules:

1. Create a new package under `com.example.blockchain`
2. Define clear interfaces for your module
3. Implement the interfaces
4. Add unit tests
5. Update documentation to explain your module

## Pull Request Process

1. Ensure your code passes all tests
2. Update documentation if necessary
3. Add your changes to the CHANGELOG.md file
4. Submit a pull request with a clear description of the changes
5. Wait for code review and address any feedback

Thank you for contributing to Modular Blockchain Java!