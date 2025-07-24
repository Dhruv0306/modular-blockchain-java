# Modular Blockchain Java - API Documentation

This document provides a comprehensive guide to the REST API endpoints available in the Modular Blockchain Java framework.

## Table of Contents

- [Overview](#overview)
- [Base URL](#base-url)
- [Authentication](#authentication)
- [Blockchain API](#blockchain-api)
  - [Get Blockchain](#get-blockchain)
  - [Add Transaction](#add-transaction)
  - [Mine Block](#mine-block)
  - [Get Pending Transactions](#get-pending-transactions)
  - [Validate Chain](#validate-chain)
- [Wallet API](#wallet-api)
  - [Generate Wallet](#generate-wallet)
  - [List Wallets](#list-wallets)
  - [Get Public Keys](#get-public-keys)
  - [Get Public Key by User ID](#get-public-key-by-user-id)
  - [Export Wallet Data](#export-wallet-data)
  - [Import Wallet](#import-wallet)
  - [Delete Wallet](#delete-wallet)
- [Transaction Types](#transaction-types)
  - [Financial Transaction](#financial-transaction)
  - [Signed Financial Transaction](#signed-financial-transaction)
- [Error Handling](#error-handling)
- [Persistence](#persistence)
- [Examples](#examples)
  - [cURL Examples](#curl-examples)
  - [JavaScript Examples](#javascript-examples)
  - [Java Client Examples](#java-client-examples)

## Overview

The Modular Blockchain Java framework provides a REST API for interacting with the blockchain and wallet management system. This API allows you to:

- View the blockchain and its blocks
- Add transactions to the blockchain
- Mine new blocks
- Validate the blockchain
- Create and manage cryptographic wallets
- Sign and verify transactions
- Export and import wallet data
- Manage transaction pools via mempool

## Base URL

All API endpoints are relative to the base URL:

```
http://localhost:8080/api
```

## Authentication

Most blockchain endpoints are publicly accessible. However, wallet operations that involve private keys (such as exporting wallet data or deleting a wallet) require authentication by providing the private key file. The system validates the private key against the stored wallet data to ensure only the wallet owner can perform sensitive operations.

## Blockchain API

### Get Blockchain

Retrieves the entire blockchain with all blocks and their transactions.

- **URL**: `/chain`
- **Method**: `GET`
- **URL Parameters**: None
- **Success Response**:
  - **Code**: 200 OK
  - **Content**: Array of Block objects
  ```json
  [
    {
      "index": 0,
      "previousHash": "0",
      "timestamp": 1625097600000,
      "transactions": [],
      "nonce": 0,
      "hash": "GENESIS_HASH"
    },
    {
      "index": 1,
      "previousHash": "GENESIS_HASH",
      "timestamp": 1625097660000,
      "transactions": [
        {
          "sender": "Alice",
          "receiver": "Bob",
          "amount": 100,
          "transactionId": "tx123",
          "senderID": "Alice",
          "receiverID": "Bob"
        }
      ],
      "nonce": 12345,
      "hash": "00000a1b2c3d4e5f..."
    }
  ]
  ```

**Note**: For large blockchains, this endpoint returns all blocks which may result in a large response payload.

### Add Transaction

Adds a new transaction to the mempool (transaction pool). The transaction will be included in a future block when mining occurs.

- **URL**: `/transactions`
- **Method**: `POST`
- **Content-Type**: `application/json`
- **Request Body**: Transaction object
  ```json
  {
    "sender": "Alice",
    "receiver": "Bob",
    "amount": 100,
    "senderID": "alice123",
    "receiverID": "bob456",
    "type": "FinancialTransaction"  
  }
  ```
  
  For signed transactions:
  ```json
  {
    "sender": "Alice",
    "receiver": "Bob",
    "amount": 100,
    "senderID": "alice123",
    "receiverID": "bob456",
    "type": "SignedFinancialTransaction"
  }
  ```
  
- **Success Response**:
  - **Code**: 201 Created
  - **Content**: `"Transaction added to MemPool."`
- **Error Response**:
  - **Code**: 400 Bad Request
  - **Content**: `"Invalid transaction data."`
  - **Code**: 422 Unprocessable Entity
  - **Content**: `"Error processing transaction: [error details]"`
  - **Code**: 500 Internal Server Error
  - **Content**: `"Unexpected server error"`

**Note**: When adding a signed transaction, the system will automatically retrieve the sender's wallet and sign the transaction using the stored private key.

### Mine Block

Mines a new block with transactions from the mempool, up to the configured maximum transactions per block. The system uses Proof of Work consensus to generate and validate the block.

- **URL**: `/mine`
- **Method**: `POST`
- **URL Parameters**: None
- **Success Response**:
  - **Code**: 201 Created
  - **Content**: `"Block mined and added to chain: 00000a1b2c3d4e5f..."`
- **Error Response**:
  - **Code**: 400 Bad Request
  - **Content**: `"No transactions to mine."`
  - **Code**: 409 Conflict
  - **Content**: `"Block mining failed."`
  - **Code**: 500 Internal Server Error
  - **Content**: `"Mining operation failed"`

**Note**: The maximum number of transactions per block is configured in the blockchain properties (default is 10). If there are more pending transactions than this limit, only the top N transactions will be included in the block.

### Get Pending Transactions

Retrieves all pending transactions from the mempool that have not yet been included in a block.

- **URL**: `/pending`
- **Method**: `GET`
- **URL Parameters**: None
- **Success Response**:
  - **Code**: 200 OK
  - **Content**: Array of Transaction objects
  ```json
  [
    {
      "sender": "Alice",
      "receiver": "Bob",
      "amount": 100,
      "transactionId": "tx123",
      "senderID": "alice123",
      "receiverID": "bob456",
      "type": "FinancialTransaction"
    },
    {
      "sender": "Charlie",
      "receiver": "Dave",
      "amount": 50,
      "transactionId": "tx124",
      "senderID": "charlie789",
      "receiverID": "dave101",
      "type": "FinancialTransaction"
    }
  ]
  ```

**Note**: For signed transactions, additional fields like `senderPublicKey`, `signature`, and `timestamp` will be included in the response.

### Validate Chain

Validates the integrity of the entire blockchain. This checks that all blocks are properly linked, all hashes are valid, and all signed transactions have valid signatures.

- **URL**: `/validate`
- **Method**: `GET`
- **URL Parameters**: None
- **Success Response**:
  - **Code**: 200 OK
  - **Content**: `"Chain is valid."`
- **Error Response**:
  - **Code**: 409 Conflict
  - **Content**: `"Chain is invalid!"`
  - **Code**: 500 Internal Server Error
  - **Content**: `"Validation error occurred"`

**Note**: This operation performs a full validation of the entire blockchain, which may be resource-intensive for large chains.



## Wallet API

### Generate Wallet

Creates a new wallet with a key pair or returns an existing wallet if the userId already exists.

- **URL**: `/wallets/generate`
- **Method**: `POST`
- **Content-Type**: `multipart/form-data`
- **Form Parameters**:
  - `userId`: Unique identifier for the wallet
  - `userName`: Human-readable name for the wallet owner
- **Success Response**:
  - **Code**: 201 Created
  - **Content**: Multipart response containing:
    - Messages with wallet creation status
    - Usage instructions
    - Public key file for download
    - Private key file for download
- **Error Response**:
  - **Code**: 409 Conflict
  - **Content**: `"Wallet already exists for User ID: [userId]"`
  - **Code**: 400 Bad Request
  - **Content**: `"Invalid userId or userName provided"`
  - **Code**: 500 Internal Server Error
  - **Content**: Error message with details

**Note**: The response includes downloadable public and private key files. The private key should be securely stored by the user as it will be required for wallet operations like export and deletion.

### List Wallets

Lists all wallets in the system (public information only).

- **URL**: `/wallets`
- **Method**: `GET`
- **URL Parameters**: None
- **Success Response**:
  - **Code**: 200 OK
  - **Content**: Array of WalletDTO objects
  ```json
  [
    {
      "userId": "alice123",
      "userName": "Alice",
      "publicKeyBase64": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA..."
    },
    {
      "userId": "bob456",
      "userName": "Bob",
      "publicKeyBase64": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA..."
    }
  ]
  ```

**Note**: This endpoint only returns public wallet information. Private keys are never exposed through this API.

### Get Public Keys

Retrieves all public keys in the system in PEM format.

- **URL**: `/wallets/public-keys`
- **Method**: `GET`
- **URL Parameters**: None
- **Success Response**:
  - **Code**: 200 OK
  - **Content**: Map of user IDs to PEM-formatted public keys
  ```json
  {
    "alice123": "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...\n-----END PUBLIC KEY-----",
    "bob456": "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...\n-----END PUBLIC KEY-----"
  }
  ```

**Note**: These public keys can be used to verify signatures on transactions created by the respective users.

### Get Public Key by User ID

Retrieves the public key for a specific user in PEM format.

- **URL**: `/wallets/public-key`
- **Method**: `GET`
- **URL Parameters**:
  - `userId`: The ID of the user whose public key to retrieve
- **Success Response**:
  - **Code**: 200 OK
  - **Content**: Public key in PEM format
  ```
  -----BEGIN PUBLIC KEY-----
  MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...
  -----END PUBLIC KEY-----
  ```
- **Error Response**:
  - **Code**: 404 Not Found
  - **Content**: `"No wallet found for User ID: [userId]"`
  - **Code**: 500 Internal Server Error
  - **Content**: `"Error retrieving public key for user ID: [userId]. [error details]"`

### Export Wallet Data

Exports wallet data for backup or transfer (requires authentication with private key).

- **URL**: `/wallets/export`
- **Method**: `GET`
- **Content-Type**: `multipart/form-data`
- **Form Parameters**:
  - `userId`: The ID of the wallet to export
  - `privateKey`: The private key file for authentication
- **Success Response**:
  - **Code**: 200 OK
  - **Content**: Multipart response containing:
    - Success message
    - Usage tip
    - Wallet data file for download
- **Error Response**:
  - **Code**: 400 Bad Request
  - **Content**: Various error messages:
    - `"User ID or Private key is missing."`
    - `"No wallet found for User ID: [userId]. Please create a wallet first."`
    - `"Failed to read private key file: [error details]"`
    - `"Private key file is empty."`
    - `"Invalid private key provided for User ID: [userId]"`
  - **Code**: 500 Internal Server Error
  - **Content**: `"Error Validating Privatekey. Error: [error details]"`

**Note**: The exported wallet file contains all wallet data including the encrypted private key and can be used to restore the wallet on another system.

### Import Wallet

Imports a wallet from a backup file previously exported from the system.

- **URL**: `/wallets/import`
- **Method**: `POST`
- **Content-Type**: `multipart/form-data`
- **Form Parameters**:
  - `file`: The wallet backup file
- **Success Response**:
  - **Code**: 201 Created
  - **Content**: `"Wallet imported successfully for user: [userName]"`
- **Error Response**:
  - **Code**: 400 Bad Request
  - **Content**: Various error messages:
    - `"File is empty. Please upload a valid wallet file."`
    - `"Invalid wallet data in file."`
  - **Code**: 409 Conflict
  - **Content**: `"Wallet already exists for User ID: [userId]"`
  - **Code**: 422 Unprocessable Entity
  - **Content**: `"Invalid wallet file format"`
  - **Code**: 500 Internal Server Error
  - **Content**: `"Internal error during import."`

**Note**: The imported wallet will be added to the system's wallet list and can be used immediately for transactions.

### Delete Wallet

Deletes a wallet (requires authentication with private key).

- **URL**: `/wallets/delete`
- **Method**: `DELETE`
- **Content-Type**: `multipart/form-data`
- **Form Parameters**:
  - `userId`: The ID of the wallet to delete
  - `privateKey`: The private key file for authentication
- **Success Response**:
  - **Code**: 200 OK
  - **Content**: `"Wallet deleted successfully for userId: [userId]"`
- **Error Response**:
  - **Code**: 400 Bad Request
  - **Content**: Various error messages:
    - `"No wallet found with userId: [userId]"`
    - `"Private key file is empty."`
    - `"Failed to read private key file: [error details]"`
  - **Code**: 403 Forbidden
  - **Content**: `"Private key does not match. Deletion unauthorized."`
  - **Code**: 500 Internal Server Error
  - **Content**: `"Error validating private key: [error details]"`

**Note**: Once a wallet is deleted, it cannot be recovered unless you have previously exported it. All transactions associated with the wallet will remain in the blockchain, but you will no longer be able to sign new transactions with this wallet.

## Transaction Types

### Financial Transaction

Standard financial transaction used in the blockchain.

```json
{
  "sender": "Alice",
  "receiver": "Bob",
  "amount": 100,
  "transactionId": "tx123",
  "senderID": "alice123",
  "receiverID": "bob456",
  "type": "FinancialTransaction"
}
```

### Signed Financial Transaction

Financial transaction with digital signature for enhanced security. The signature is created using the sender's private key and can be verified using their public key.

```json
{
  "sender": "Alice",
  "receiver": "Bob",
  "amount": 100,
  "transactionId": "tx123",
  "senderID": "alice123",
  "receiverID": "bob456",
  "senderPublicKey": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...",
  "signature": "MEUCIQD0lkJH9BqXFwQv7xOJ9w4U8+SY5IGBH0MbXs+B9eKJ0AIgFOeQvqFu...",
  "timestamp": 1625097600000,
  "type": "SignedFinancialTransaction"
}
```

**Note**: When submitting a transaction with type "SignedFinancialTransaction", the system will automatically retrieve the sender's wallet, sign the transaction data, and verify the signature before adding it to the mempool.

## Error Handling

The API uses HTTP status codes to indicate the success or failure of requests:

- `200 OK`: The request was successful (for read operations)
- `201 Created`: Resource was successfully created
- `400 Bad Request`: The request was invalid or cannot be served
- `403 Forbidden`: Authentication failed or operation not permitted
- `404 Not Found`: The requested resource was not found
- `409 Conflict`: Request conflicts with current state (e.g., duplicate resource, invalid blockchain)
- `422 Unprocessable Entity`: Request is well-formed but contains semantic errors
- `500 Internal Server Error`: An unexpected error occurred on the server

Error responses include a descriptive message explaining the specific error condition.

## Persistence

The blockchain system implements automatic persistence to maintain state across application restarts:

- **Blockchain Persistence**: The blockchain state is automatically saved to disk when the application shuts down and loaded when it starts up.
- **Wallet Persistence**: Wallet data is also persisted between application runs.
- **Configuration**: Persistence can be configured in the blockchain properties file:
  - `persistence.enabled`: Enable/disable persistence (default: true)
  - `persistence.file`: File path for blockchain data (default: data/chain-data.json)
  - `persistence.wallet_file`: File path for wallet data (default: data/wallet-data.json)

The persistence mechanism ensures that blockchain data and wallet information are not lost when the application is restarted.

## Examples

### cURL Examples

**Get the blockchain:**
```bash
curl http://localhost:8080/api/chain
```

**Add a transaction:**
```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{"sender": "Alice", "receiver": "Bob", "amount": 100, "senderID": "alice123", "receiverID": "bob456", "type": "FinancialTransaction"}'
```

**Mine a block:**
```bash
curl -X POST http://localhost:8080/api/mine
```

**Generate a wallet:**
```bash
curl -X POST http://localhost:8080/api/wallets/generate \
  -F "userId=alice123" \
  -F "userName=Alice"
```

**Export a wallet (requires private key file):**
```bash
curl -X GET http://localhost:8080/api/wallets/export \
  -F "userId=alice123" \
  -F "privateKey=@/path/to/alice123_privateKey.key" \
  -o alice_wallet_backup.json
```

**Delete a wallet (requires private key file):**
```bash
curl -X DELETE http://localhost:8080/api/wallets/delete \
  -F "userId=alice123" \
  -F "privateKey=@/path/to/alice123_privateKey.key"
```

### JavaScript Examples

**Get the blockchain:**
```javascript
fetch('http://localhost:8080/api/chain')
  .then(response => response.json())
  .then(data => console.log(data))
  .catch(error => console.error('Error:', error));
```

**Add a transaction:**
```javascript
fetch('http://localhost:8080/api/transactions', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    sender: 'Alice',
    receiver: 'Bob',
    amount: 100,
    senderID: 'alice123',
    receiverID: 'bob456',
    type: 'FinancialTransaction'
  }),
})
.then(response => response.text())
.then(data => console.log(data))
.catch(error => console.error('Error:', error));
```

**Get pending transactions:**
```javascript
fetch('http://localhost:8080/api/pending')
  .then(response => response.json())
  .then(data => console.log(data))
  .catch(error => console.error('Error:', error));
```

**Generate a wallet:**
```javascript
const formData = new FormData();
formData.append('userId', 'alice123');
formData.append('userName', 'Alice');

fetch('http://localhost:8080/api/wallets/generate', {
  method: 'POST',
  body: formData
})
.then(response => response.json())
.then(data => console.log(data))
.catch(error => console.error('Error:', error));
```

### Java Client Examples

**Get the blockchain:**
```java
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class BlockchainClient {
    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/chain"))
                .build();
        
        HttpResponse<String> response = client.send(request, 
                HttpResponse.BodyHandlers.ofString());
        
        System.out.println(response.body());
    }
}
```

**Add a transaction:**
```java
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class BlockchainClient {
    public static void main(String[] args) throws Exception {
        String json = "{\"sender\":\"Alice\",\"receiver\":\"Bob\",\"amount\":100,\"senderID\":\"alice123\",\"receiverID\":\"bob456\",\"type\":\"FinancialTransaction\"}";
        
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/transactions"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        
        HttpResponse<String> response = client.send(request, 
                HttpResponse.BodyHandlers.ofString());
        
        System.out.println(response.body());
    }
}
```

**Mine a block:**
```java
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class BlockchainClient {
    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/mine"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        
        HttpResponse<String> response = client.send(request, 
                HttpResponse.BodyHandlers.ofString());
        
        System.out.println(response.body());
    }
}
```

**Import a wallet:**
```java
import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

public class BlockchainClient {
    public static void main(String[] args) throws Exception {
        // Create multipart form data
        String boundary = "Boundary-" + System.currentTimeMillis();
        
        // Read wallet file
        File walletFile = new File("/path/to/wallet_backup.json");
        byte[] fileContent = Files.readAllBytes(walletFile.toPath());
        
        // Build multipart request body
        String multipartBody = "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"file\"; filename=\"wallet_backup.json\"\r\n" +
                "Content-Type: application/json\r\n\r\n" +
                new String(fileContent) + "\r\n" +
                "--" + boundary + "--\r\n";
        
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/wallets/import"))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofString(multipartBody))
                .build();
        
        HttpResponse<String> response = client.send(request, 
                HttpResponse.BodyHandlers.ofString());
        
        System.out.println(response.body());
    }
}
```