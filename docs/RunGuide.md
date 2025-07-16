# Running the Modular Blockchain Java

This guide provides detailed instructions for running the Modular Blockchain Java framework in different environments and with various configuration options.

## Table of Contents

- [Basic Run Commands](#basic-run-commands)
  - [Using Maven](#using-maven)
  - [Using Convenience Scripts](#using-convenience-scripts)
- [Environment-Specific Configurations](#environment-specific-configurations)
  - [Using Configuration Files](#using-configuration-files)
  - [Using Environment Variables](#using-environment-variables)
  - [Using Convenience Scripts with Environment Options](#using-convenience-scripts-with-environment-options)
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
mvn exec:java -Dexec.mainClass="com.example.blockchain.Main"
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

### Custom Configuration Files

You can create your own configuration files with custom settings:

```bash
# Create a custom configuration file
echo "difficulty=3" > my-custom-config.properties
echo "genesis_hash=CUSTOM_HASH" >> my-custom-config.properties
echo "persistence.enabled=true" >> my-custom-config.properties
echo "persistence.file=data/my-custom-chain.json" >> my-custom-config.properties

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

### Getting Help

If you encounter issues not covered in this guide, please:

1. Check the project logs in the `logs/` directory
2. Review the [README.md](../README.md) for general information
3. Review the [Test Guide](TestGuide.md) for testing-specific issues
4. Submit an issue on the project repository