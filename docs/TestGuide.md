# Test Guide for Modular Blockchain Java

This guide explains how to effectively run, analyze, and debug tests for the Modular Blockchain Java project.

## Table of Contents

- [Test Class Overview](#-test-class-overview)
- [Running Tests](#running-tests)
  - [Running All Tests](#running-all-tests)
  - [Running Individual Tests](#running-individual-tests)
  - [Running Tests with Different Configurations](#running-tests-with-different-configurations)
- [Viewing Test Coverage](#viewing-test-coverage)
  - [Generating Coverage Reports](#generating-coverage-reports)
  - [Viewing Coverage Reports](#viewing-coverage-reports)
  - [Coverage Thresholds](#coverage-thresholds)
- [Mock Transactions for Testing](#mock-transactions-for-testing)
  - [Purpose of MockTransaction](#purpose-of-mocktransaction)
  - [When to Use MockTransaction](#when-to-use-mocktransaction)
- [Debugging Test Failures](#debugging-test-failures)
  - [Understanding Test Output](#understanding-test-output)
  - [Common Debugging Techniques](#common-debugging-techniques)
  - [Troubleshooting Common Issues](#troubleshooting-common-issues)
- [Testing JSON Serialization and Persistence](#testing-json-serialization)
  - [Key JSON Serialization Tests](#key-json-serialization-tests)
  - [Example JSON Serialization Test](#example-json-serialization-test)
- [Testing REST API](#testing-rest-api)
  - [API Test Classes](#api-test-classes)
  - [Example API Test](#example-api-test)
- [Testing Wallet Functionality](#testing-wallet-functionality)
  - [Key Wallet Test Classes](#key-wallet-test-classes)
  - [Example Wallet Test](#example-wallet-test)
  - [Testing Wallet Security](#testing-wallet-security)
- [Testing Edge Cases and Boundary Conditions](#testing-edge-cases-and-boundary-conditions)
  - [Key Edge Case Tests](#key-edge-case-tests)
  - [Writing Your Own Edge Case Tests](#writing-your-own-edge-case-tests)

---

## ✅ Test Class Overview

| **Test Class**                     | **Target Class/Module**            | **Purpose**                                                                 |
|-----------------------------------|------------------------------------|-----------------------------------------------------------------------------|
| `BlockchainTest`                  | `Blockchain`                       | Unit tests for adding transactions, adding blocks, and validating chain integrity. |
| `FinancialTransactionTest`       | `FinancialTransaction`             | Tests transaction validation rules and summary string formatting.          |
| `ProofOfWorkTest`                | `ProofOfWork`                      | Verifies block mining and block validation using proof-of-work consensus.  |
| `SignedFinancialTransactionTest` | `SignedFinancialTransaction`       | Tests digital signature verification, key binding, and validity enforcement. |
| `CryptoUtilsTest`                | `CryptoUtils`                      | Validates RSA key generation, message signing, and signature verification. |
| `BlockUtilsTest`                 | `BlockUtils`                       | Ensures consistent hashing of blocks using utility methods.                |
| `BlockchainIntegrationTest`      | `Blockchain + ProofOfWork`         | Tests end-to-end blockchain operations, tampering detection, and chain robustness. |
| `BlockValidationTest`            | `Blockchain + SignedTransaction`   | Tests detection of tampered transactions within otherwise valid blocks.    |
| `DynamicLoggingTest`             | `LoggingUtils`                     | Verifies dynamic log level changes at runtime for debugging flexibility.   |
| `BlockchainEdgeCasesTest`        | `Blockchain`                       | Tests edge cases like empty transaction lists and duplicate transactions.  |
| `ConfigErrorsTest`               | `BlockchainConfig`                 | Ensures robust handling of missing or invalid configuration files.         |
| `JsonUtilsTest`                 | `JsonUtils`                       | Tests JSON serialization and deserialization of blockchain components.    |
| `BlockchainSerializationTest`   | `Blockchain`                      | Tests exporting and importing blockchain data to/from JSON files.        |
| `PersistenceManagerTest`        | `PersistenceManager`              | Tests automatic saving and loading of blockchain state between runs.      |
| `BlockchainControllerTest`      | `BlockchainController`            | Tests REST API endpoints for blockchain interaction.                     |
| `BlockchainApplicationTest`     | `BlockchainApplication`           | Tests Spring Boot application startup and configuration.                 |
| `WalletTest`                    | `Wallet`                          | Tests wallet creation and key pair generation functionality.            |
| `WalletListTest`               | `WalletList`                      | Tests adding, retrieving, and managing multiple wallets.               |
| `WalletControllerTest`         | `WalletController`                | Tests REST API endpoints for wallet operations and authentication.      |
| `WalletDTOTest`                | `WalletDTO`                       | Tests wallet data transfer object creation and serialization.          |

---

## Running Tests

### Running All Tests

To run all tests in the project:

```bash
mvn test
```

### Running Individual Tests

To run a specific test class:

```bash
mvn test -Dtest=BlockchainTest
```

To run a specific test method:

```bash
mvn test -Dtest=BlockchainTest#testAddBlock
```

To run multiple specific test classes:

```bash
mvn test -Dtest=BlockchainTest,ProofOfWorkTest
```

### Running Tests with Different Configurations

To run tests with a specific environment configuration:

```bash
BLOCKCHAIN_ENV=dev mvn test
```

## Viewing Test Coverage

The project uses JaCoCo for test coverage reporting.

### Generating Coverage Reports

To generate test coverage reports:

```bash
mvn verify
```

### Viewing Coverage Reports

After running the command above, you can find the HTML coverage report at:

```
target/site/jacoco/index.html
```

Open this file in your browser to see:

- Overall project coverage
- Package-level coverage
- Class-level coverage
- Method and line coverage details

### Coverage Thresholds

The project aims for:
- Line coverage: 80%
- Branch coverage: 70%
- Method coverage: 80%

## Mock Transactions for Testing

### Purpose of MockTransaction

The project includes a `MockTransaction` class specifically designed for unit testing. This class:

- Provides a simplified transaction implementation that focuses on testability
- Allows controlling validity through constructor parameters
- Eliminates external dependencies that might complicate testing
- Enables predictable behavior in test scenarios

```java
public class MockTransaction implements Transaction {
    private final boolean valid;
    private final String sender;
    private final String receiver;
    
    public MockTransaction(boolean valid) {
        this.valid = valid;
        this.sender = "MockSender";
        this.receiver = "MockReceiver";
    }
    
    public MockTransaction(boolean valid, String sender, String receiver) {
        this.valid = valid;
        this.sender = sender;
        this.receiver = receiver;
    }
    
    @Override
    public boolean isValid() {
        return valid;
    }
    
    @Override
    public String getSender() {
        return sender;
    }
    
    @Override
    public String getReceiver() {
        return receiver;
    }
    
    @Override
    public String getSummary() {
        return "Mock transaction from " + sender + " to " + receiver;
    }
}
```

### When to Use MockTransaction

- Testing the blockchain core functionality without domain-specific logic
- Isolating consensus algorithm testing from transaction implementation details
- Creating test scenarios with predictable transaction validity
- Simulating various transaction states without complex setup

## Debugging Test Failures

### Understanding Test Output

When tests fail, Maven provides:
- The name of the failing test
- The expected vs. actual values
- The line number where the failure occurred

### Common Debugging Techniques

1. **Enable Debug Logging**

   Add the following to your test to see detailed logs:

   ```java
   @BeforeEach
   void setupLogging() {
       LoggingUtils.setBlockchainLogLevel("DEBUG");
   }
   ```

2. **Using Breakpoints**

   If using an IDE like IntelliJ or Eclipse:
   - Set breakpoints in your test or implementation code
   - Run the test in debug mode
   - Inspect variables and execution flow

3. **Isolating Test Failures**

   If a test is failing intermittently:
   - Run the test in isolation
   - Check for dependencies on other tests
   - Look for race conditions or timing issues

4. **Examining Test Reports**

   Detailed test reports are available at:

   ```
   target/surefire-reports/
   ```

   These include:
   - Plain text reports (*.txt)
   - XML reports for CI integration (*.xml)

### Troubleshooting Common Issues

1. **Configuration Issues**
   - Check if tests are using the correct configuration files
   - Verify environment variables are set correctly

2. **Concurrency Problems**
   - Look for shared state between tests
   - Use thread-safe collections when needed

3. **Resource Leaks**
   - Ensure resources are properly closed in @AfterEach or @AfterAll methods
   - Check for unclosed file handles or connections

4. **Assertion Errors**
   - Use more specific assertions with descriptive messages
   - Consider using assertAll for multiple related assertions

## Testing JSON Serialization and Persistence

The project includes tests for JSON serialization and deserialization to ensure blockchain data can be properly saved and loaded.

### Key JSON Serialization Tests

1. **Block Serialization**
   - Tests serializing and deserializing individual blocks
   - Verifies all block properties are preserved

2. **Transaction Serialization**
   - Tests serializing and deserializing different transaction types
   - Ensures transaction validity is maintained after deserialization

3. **Blockchain Serialization**
   - Tests exporting and importing entire blockchain
   - Verifies chain integrity after round-trip serialization
   
4. **Persistence Manager**
   - Tests automatic saving of blockchain state on shutdown
   - Tests automatic loading of blockchain state on startup
   - Verifies error handling for corrupted or missing persistence files

### Example JSON Serialization Test

```java
@Test
void testBlockchainJsonRoundTrip() throws Exception {
    // Create a blockchain with some transactions and blocks
    Blockchain<FinancialTransaction> blockchain = new Blockchain<>();
    blockchain.addTransaction(new FinancialTransaction("Alice", "Bob", 100));
    
    // Add a block
    ProofOfWork<FinancialTransaction> consensus = new ProofOfWork<>();
    Block<FinancialTransaction> block = consensus.generateBlock(
        blockchain.getPendingTransactions(), blockchain.getLastBlock());
    blockchain.addBlock(block);
    
    // Export to JSON file
    File tempFile = File.createTempFile("blockchain", ".json");
    blockchain.exportToJson(tempFile);
    
    // Import from JSON file
    Blockchain<FinancialTransaction> imported = 
        Blockchain.importFromJson(tempFile, FinancialTransaction.class);
    
    // Verify the imported blockchain
    assertEquals(blockchain.getChain().size(), imported.getChain().size());
    assertEquals(blockchain.getChain().get(1).getHash(), 
                imported.getChain().get(1).getHash());
    
    // Clean up
    tempFile.delete();
}
```

## Testing REST API

The project now includes tests for the Spring Boot REST API endpoints.

### API Test Classes

1. **BlockchainControllerTest**
   - Tests REST API endpoints for blockchain interaction
   - Verifies proper handling of HTTP requests and responses
   - Tests transaction validation through the API
   - Tests block mining through the API

2. **BlockchainApplicationTest**
   - Tests Spring Boot application startup
   - Verifies proper configuration loading
   - Tests application context initialization

### Example API Test

```java
@SpringBootTest
@AutoConfigureMockMvc
public class BlockchainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetBlockchain() throws Exception {
        mockMvc.perform(get("/api/chain"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void testAddTransaction() throws Exception {
        FinancialTransaction tx = new FinancialTransaction("Alice", "Bob", 100);
        
        mockMvc.perform(post("/api/transactions")
               .contentType(MediaType.APPLICATION_JSON)
               .content(JsonUtils.toJson(tx)))
               .andExpect(status().isOk())
               .andExpect(content().string(containsString("Transaction added")));
    }

    @Test
    public void testMineBlock() throws Exception {
        // First add a transaction
        FinancialTransaction tx = new FinancialTransaction("Alice", "Bob", 100);
        mockMvc.perform(post("/api/transactions")
               .contentType(MediaType.APPLICATION_JSON)
               .content(JsonUtils.toJson(tx)));
        
        // Then mine a block
        mockMvc.perform(post("/api/mine"))
               .andExpect(status().isOk())
               .andExpect(content().string(containsString("Block mined")));
    }
}
```

## Testing Wallet Functionality

The project includes tests for the wallet management system to ensure secure and reliable wallet operations.

### Key Wallet Test Classes

1. **WalletTest**
   - Tests wallet creation and key pair generation
   - Verifies public/private key functionality
   - Tests wallet data serialization

2. **WalletListTest**
   - Tests adding and retrieving wallets
   - Verifies wallet lookup by user ID
   - Tests wallet list persistence

3. **WalletControllerTest**
   - Tests REST API endpoints for wallet operations
   - Verifies authentication for sensitive operations
   - Tests wallet import/export functionality

### Example Wallet Test

```java
@Test
void testWalletCreationAndSigning() throws Exception {
    // Create a new wallet
    Wallet wallet = new Wallet("testUser", "Test User");
    
    // Create a test message
    String message = "Test message to sign";
    
    // Sign the message using the wallet's private key
    String signature = CryptoUtils.signData(message, wallet.getPrivateKey());
    
    // Verify the signature using the wallet's public key
    boolean isValid = CryptoUtils.verifySignature(message, signature, wallet.getPublicKey());
    
    // Assert that the signature is valid
    assertTrue(isValid, "Signature should be valid when verified with the correct public key");
    
    // Try to verify with a different wallet's public key
    Wallet anotherWallet = new Wallet("anotherUser", "Another User");
    boolean isInvalid = CryptoUtils.verifySignature(message, signature, anotherWallet.getPublicKey());
    
    // Assert that the signature is invalid with the wrong public key
    assertFalse(isInvalid, "Signature should be invalid when verified with a different public key");
}
```

### Testing Wallet Security

When testing wallet functionality, focus on these security aspects:

1. **Key Pair Generation**: Test that generated key pairs are unique and secure
2. **Signature Verification**: Test that signatures can only be verified with the correct public key
3. **Authentication**: Test that sensitive operations require valid private key authentication
4. **Error Handling**: Test proper error responses for invalid inputs or unauthorized access
5. **Persistence**: Test that wallet data is properly saved and loaded

## Testing Edge Cases and Boundary Conditions

The project includes dedicated tests for edge cases and boundary conditions to ensure the blockchain remains robust in unusual situations.

### Key Edge Case Tests

1. **Empty Transaction Lists**
   - Tests creating blocks with no transactions
   - Ensures the blockchain can handle empty blocks correctly

2. **Duplicate Transactions**
   - Tests adding identical transactions to the blockchain
   - Verifies proper handling of transaction duplication

3. **Transaction Tampering**
   - Tests detection of tampered transactions in blocks
   - Ensures the blockchain can identify invalid signatures

4. **Configuration Errors**
   - Tests missing configuration files
   - Tests invalid configuration formats
   - Tests partial configuration definitions

### Writing Your Own Edge Case Tests

When adding new features, consider testing these edge cases:

```java
// Test with null or empty inputs
@Test
void testWithEmptyInput() {
    // Test with empty input
    List<YourTransaction> emptyList = new ArrayList<>();
    // Verify correct behavior with empty input
}

// Test with maximum values
@Test
void testWithMaxValues() {
    // Test with maximum allowed values
    YourTransaction tx = new YourTransaction(MAX_VALUE);
    // Verify correct behavior with maximum values
}

// Test with invalid inputs
@Test
void testWithInvalidInput() {
    // Test with invalid input
    YourTransaction invalidTx = new YourTransaction(INVALID_VALUE);
    // Verify correct handling of invalid input
}
```