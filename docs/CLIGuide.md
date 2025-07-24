# CLI Guide - Modular Blockchain Java

This guide covers the command-line interface (CLI) for interacting with the Modular Blockchain Java framework.

## Table of Contents

- [Overview](#overview)
- [Getting Started](#getting-started)
- [Available Commands](#available-commands)
- [Blockchain Commands](#blockchain-commands)
- [Wallet Commands](#wallet-commands)
- [Examples](#examples)
- [Error Handling](#error-handling)
- [Troubleshooting](#troubleshooting)

## Overview

The CLI provides a user-friendly command-line interface for:
- Viewing and managing the blockchain
- Creating and managing wallets
- Adding transactions and mining blocks
- Importing/exporting wallet data

## Getting Started

### Prerequisites

1. Ensure the REST API server is running:
```bash
mvn spring-boot:run
```

2. Start the CLI client:
```bash
java -cp target/classes com.example.blockchain.cli.ApiBasedBlockchainCLI
```

### CLI Interface

Once started, you'll see the main menu:
```
=== Blockchain CLI ===
Available commands:
- get-chain: View the blockchain
- add-transaction: Add a new transaction
- mine-block: Mine a new block
- get-pending: View pending transactions
- validate-chain: Validate the blockchain
- create-wallet: Create a new wallet
- get-public-keys: List all public keys
- get-public-key: Get public key for a user
- export-wallet: Export wallet data
- import-wallet: Import wallet from file
- delete-wallet: Delete a wallet
- help: Show this help message
- exit: Exit the application

Enter command:
```

## Available Commands

### Blockchain Commands

| Command | Description | Parameters |
|---------|-------------|------------|
| `get-chain` | Display the entire blockchain | None |
| `add-transaction` | Add a new transaction to mempool | Interactive prompts |
| `mine-block` | Mine a new block with pending transactions | None |
| `get-pending` | Show pending transactions in mempool | None |
| `validate-chain` | Validate blockchain integrity | None |

### Wallet Commands

| Command | Description | Parameters |
|---------|-------------|------------|
| `create-wallet` | Create a new wallet | Interactive prompts |
| `get-public-keys` | List all registered public keys | None |
| `get-public-key` | Get public key for specific user | Interactive prompt |
| `export-wallet` | Export wallet data for backup | Interactive prompts |
| `import-wallet` | Import wallet from backup file | Interactive prompt |
| `delete-wallet` | Delete a wallet (requires private key) | Interactive prompts |

## Blockchain Commands

### View Blockchain (`get-chain`)

Displays the complete blockchain with formatted output:

```
Enter command: get-chain

=== BLOCKCHAIN ===
Total Blocks: 2

--- Block 0 ---
Index: 0
Previous Hash: 0
Timestamp: 2025-01-24 10:30:00
Transactions: 0
Nonce: 0
Hash: GENESIS_HASH

--- Block 1 ---
Index: 1
Previous Hash: GENESIS_HASH
Timestamp: 2025-01-24 10:35:00
Transactions: 1
  Transaction 1:
    Sender: Alice -> Receiver: Bob
    Amount: 100.0
    Transaction ID: tx123
Nonce: 12345
Hash: 00000a1b2c3d4e5f...
```

### Add Transaction (`add-transaction`)

Interactive transaction creation:

```
Enter command: add-transaction

Enter sender: Alice
Enter receiver: Bob
Enter amount: 100
Enter sender ID: alice123
Enter receiver ID: bob456
Enter transaction type (FinancialTransaction/SignedFinancialTransaction): FinancialTransaction

✅ Transaction added successfully!
```

### Mine Block (`mine-block`)

Mines a new block with pending transactions:

```
Enter command: mine-block

✅ Block mined successfully! Hash: 00000a1b2c3d4e5f...
```

### View Pending Transactions (`get-pending`)

Shows transactions waiting to be mined:

```
Enter command: get-pending

=== PENDING TRANSACTIONS ===
Total Pending: 2

Transaction 1:
  Sender: Alice -> Receiver: Bob
  Amount: 100.0
  Transaction ID: tx123
  Type: FinancialTransaction

Transaction 2:
  Sender: Charlie -> Receiver: Dave
  Amount: 50.0
  Transaction ID: tx124
  Type: FinancialTransaction
```

## Wallet Commands

### Create Wallet (`create-wallet`)

Creates a new wallet with key pair:

```
Enter command: create-wallet

Enter User ID: alice123
Enter User Name: Alice

✅ Wallet created successfully!
📁 Files saved to: wallets/alice123/
   - alice123_publicKey.key
   - alice123_privateKey.key

⚠️  Keep your private key secure - it's required for wallet operations!
```

### List Public Keys (`get-public-keys`)

Displays all registered public keys in a formatted table:

```
Enter command: get-public-keys

=== PUBLIC KEYS ===
User ID       | User Name     | Public Key (truncated)
alice123      | Alice         | MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8A...
bob456        | Bob           | MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8A...
```

### Export Wallet (`export-wallet`)

Exports wallet data for backup (requires private key authentication):

```
Enter command: export-wallet

Enter User ID: alice123
Enter path to private key file: wallets/alice123/alice123_privateKey.key

✅ Wallet exported successfully!
📁 Backup saved to: wallets/alice123/alice123_wallet_backup.json

💡 Tip: Store this backup file securely. You can use it to restore your wallet.
```

### Import Wallet (`import-wallet`)

Imports wallet from backup file:

```
Enter command: import-wallet

Enter path to wallet backup file: /path/to/wallet_backup.json

✅ Wallet imported successfully for user: Alice
```

### Delete Wallet (`delete-wallet`)

Deletes a wallet (requires private key authentication):

```
Enter command: delete-wallet

Enter User ID: alice123
Enter path to private key file: wallets/alice123/alice123_privateKey.key

⚠️  Are you sure you want to delete this wallet? This action cannot be undone.
Enter 'yes' to confirm: yes

✅ Wallet deleted successfully!
```

## Examples

### Complete Workflow Example

1. **Create wallets for two users:**
```
create-wallet
> alice123, Alice

create-wallet  
> bob456, Bob
```

2. **Add a transaction:**
```
add-transaction
> Alice, Bob, 100, alice123, bob456, SignedFinancialTransaction
```

3. **Mine the transaction:**
```
mine-block
```

4. **View the updated blockchain:**
```
get-chain
```

5. **Export wallet for backup:**
```
export-wallet
> alice123, wallets/alice123/alice123_privateKey.key
```

## Error Handling

The CLI provides detailed error messages with appropriate HTTP status codes:

### Common Error Responses

- **400 Bad Request**: Invalid input data
- **403 Forbidden**: Authentication failed (wrong private key)
- **404 Not Found**: Wallet or resource not found
- **409 Conflict**: Blockchain validation failed
- **422 Unprocessable Entity**: Invalid transaction data
- **500 Internal Server Error**: Server error

### Example Error Messages

```
❌ Error: No wallet found for User ID: alice123. Please create a wallet first.

❌ Error: Private key does not match. Operation unauthorized.

❌ Error: Invalid transaction data provided.

❌ Error: Server connection failed. Ensure the REST API is running.
```

## Troubleshooting

### Common Issues

1. **CLI won't start**
   - Ensure Java classpath includes compiled classes
   - Check that the project has been built: `mvn clean install`

2. **Connection errors**
   - Verify REST API is running on `http://localhost:8080`
   - Check firewall settings

3. **File not found errors**
   - Ensure wallet files exist in the specified paths
   - Check file permissions

4. **Authentication failures**
   - Verify you're using the correct private key file
   - Ensure the private key file is not corrupted

### Debug Mode

For detailed error information, check the application logs in the `logs/` directory.

### Getting Help

- Use the `help` command within the CLI for quick reference
- Check the [API Documentation](API_Documentation.md) for REST endpoint details
- Review the [Run Guide](RunGuide.md) for server setup instructions