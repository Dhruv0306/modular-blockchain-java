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
  - [Get Block by Index](#get-block-by-index)
  - [Get Blockchain Statistics](#get-blockchain-statistics)
- [Wallet API](#wallet-api)
  - [Create Wallet](#create-wallet)
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
- [Rate Limiting](#rate-limiting)
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

## Base URL

All API endpoints are relative to the base URL:

```
http://localhost:8080/api
```

## Authentication

Most blockchain endpoints are publicly accessible. However, wallet operations that involve private keys require authentication by providing the private key file.

## Blockchain API

### Get Blockchain

Retrieves the entire blockchain.

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
          "transactionId": "tx123"
        }
      ],
      "nonce": 12345,
      "hash": "00000a1b2c3d4e5f..."
    }
  ]
  ```

### Add Transaction

Adds a new transaction to the pending transactions pool.

- **URL**: `/transactions`
- **Method**: `POST`
- **Content-Type**: `application/json`
- **Request Body**: Transaction object
  ```json
  {
    "sender": "Alice",
    "receiver": "Bob",
    "amount": 100
  }
  ```
- **Success Response**:
  - **Code**: 200 OK
  - **Content**: `"Transaction added."`
- **Error Response**:
  - **Code**: 400 Bad Request
  - **Content**: `"Invalid transaction."`

### Mine Block

Mines a new block with the current pending transactions.

- **URL**: `/mine`
- **Method**: `POST`
- **URL Parameters**: None
- **Success Response**:
  - **Code**: 200 OK
  - **Content**: `"Block mined with hash: 00000a1b2c3d4e5f..."`
- **Error Response**:
  - **Code**: 400 Bad Request
  - **Content**: `"No pending transactions to mine."`

### Get Pending Transactions

Retrieves all pending transactions that have not yet been included in a block.

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
      "transactionId": "tx123"
    },
    {
      "sender": "Charlie",
      "receiver": "Dave",
      "amount": 50,
      "transactionId": "tx124"
    }
  ]
  ```

### Validate Chain

Validates the integrity of the entire blockchain.

- **URL**: `/validate`
- **Method**: `GET`
- **URL Parameters**: None
- **Success Response**:
  - **Code**: 200 OK
  - **Content**: `"Chain is valid."`
- **Error Response**:
  - **Code**: 200 OK (with error message)
  - **Content**: `"Chain is invalid!"`

### Get Block by Index

Retrieves a specific block by its index.

- **URL**: `/block/{index}`
- **Method**: `GET`
- **URL Parameters**:
  - `index`: The index of the block to retrieve
- **Success Response**:
  - **Code**: 200 OK
  - **Content**: Block object
  ```json
  {
    "index": 1,
    "previousHash": "GENESIS_HASH",
    "timestamp": 1625097660000,
    "transactions": [
      {
        "sender": "Alice",
        "receiver": "Bob",
        "amount": 100,
        "transactionId": "tx123"
      }
    ],
    "nonce": 12345,
    "hash": "00000a1b2c3d4e5f..."
  }
  ```
- **Error Response**:
  - **Code**: 404 Not Found
  - **Content**: None

### Get Blockchain Statistics

Retrieves statistics about the blockchain.

- **URL**: `/stats`
- **Method**: `GET`
- **URL Parameters**: None
- **Success Response**:
  - **Code**: 200 OK
  - **Content**: Statistics object
  ```json
  {
    "blockCount": 2,
    "pendingTransactions": 1,
    "totalTransactions": 3
  }
  ```

## Wallet API

### Create Wallet

Creates a new wallet with a key pair.

- **URL**: `/wallets/generate`
- **Method**: `POST`
- **Content-Type**: `multipart/form-data`
- **Form Parameters**:
  - `userId`: Unique identifier for the wallet
  - `userName`: Human-readable name for the wallet owner
- **Success Response**:
  - **Code**: 200 OK
  - **Content**: Wallet creation confirmation with download links
  ```json
  {
    "message": "Wallet created successfully",
    "userId": "alice123",
    "userName": "Alice",
    "publicKeyDownload": "/api/wallets/public-key?userId=alice123",
    "privateKeyDownload": "/api/wallets/private-key?userId=alice123"
  }
  ```
- **Error Response**:
  - **Code**: 400 Bad Request
  - **Content**: `"User ID already exists."`

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

### Get Public Keys

Retrieves all public keys in the system.

- **URL**: `/wallets/public-keys`
- **Method**: `GET`
- **URL Parameters**: None
- **Success Response**:
  - **Code**: 200 OK
  - **Content**: Map of user IDs to public keys
  ```json
  {
    "alice123": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...",
    "bob456": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA..."
  }
  ```

### Get Public Key by User ID

Retrieves the public key for a specific user.

- **URL**: `/wallets/public-key`
- **Method**: `GET`
- **URL Parameters**:
  - `userId`: The ID of the user whose public key to retrieve
- **Success Response**:
  - **Code**: 200 OK
  - **Content**: Public key in Base64 format
  ```
  MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...
  ```
- **Error Response**:
  - **Code**: 404 Not Found
  - **Content**: `"Wallet not found."`

### Export Wallet Data

Exports wallet data for backup or transfer (requires authentication).

- **URL**: `/wallets/export`
- **Method**: `GET`
- **Content-Type**: `multipart/form-data`
- **Form Parameters**:
  - `userId`: The ID of the wallet to export
  - `privateKey`: The private key file for authentication
- **Success Response**:
  - **Code**: 200 OK
  - **Content**: Wallet data file (download)
- **Error Response**:
  - **Code**: 401 Unauthorized
  - **Content**: `"Authentication failed."`
  - **Code**: 404 Not Found
  - **Content**: `"Wallet not found."`

### Import Wallet

Imports a wallet from a backup file.

- **URL**: `/wallets/import`
- **Method**: `POST`
- **Content-Type**: `multipart/form-data`
- **Form Parameters**:
  - `file`: The wallet backup file
- **Success Response**:
  - **Code**: 200 OK
  - **Content**: `"Wallet imported successfully."`
- **Error Response**:
  - **Code**: 400 Bad Request
  - **Content**: `"Invalid wallet file."`
  - **Code**: 409 Conflict
  - **Content**: `"Wallet with this ID already exists."`

### Delete Wallet

Deletes a wallet (requires authentication).

- **URL**: `/wallets/delete`
- **Method**: `DELETE`
- **Content-Type**: `multipart/form-data`
- **Form Parameters**:
  - `userId`: The ID of the wallet to delete
  - `privateKey`: The private key file for authentication
- **Success Response**:
  - **Code**: 200 OK
  - **Content**: `"Wallet deleted successfully."`
- **Error Response**:
  - **Code**: 401 Unauthorized
  - **Content**: `"Authentication failed."`
  - **Code**: 404 Not Found
  - **Content**: `"Wallet not found."`

## Transaction Types

### Financial Transaction

Standard financial transaction used in the blockchain.

```json
{
  "sender": "Alice",
  "receiver": "Bob",
  "amount": 100,
  "transactionId": "tx123"
}
```

### Signed Financial Transaction

Financial transaction with digital signature for enhanced security.

```json
{
  "sender": "Alice",
  "receiver": "Bob",
  "amount": 100,
  "transactionId": "tx123",
  "senderPublicKey": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...",
  "signature": "MEUCIQD0lkJH9BqXFwQv7xOJ9w4U8+SY5IGBH0MbXs+B9eKJ0AIgFOeQvqFu...",
  "timestamp": 1625097600000
}
```

## Error Handling

The API uses standard HTTP status codes to indicate the success or failure of requests:

- `200 OK`: The request was successful
- `400 Bad Request`: The request was invalid or cannot be served
- `401 Unauthorized`: Authentication failed
- `404 Not Found`: The requested resource does not exist
- `409 Conflict`: The request conflicts with the current state of the server
- `500 Internal Server Error`: An error occurred on the server

Error responses include a message describing the error.

## Rate Limiting

To prevent abuse, the API implements rate limiting:

- Basic endpoints: 100 requests per minute
- Mining endpoints: 10 requests per minute
- Wallet creation: 5 requests per minute

Exceeding these limits will result in a `429 Too Many Requests` response.

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
  -d '{"sender": "Alice", "receiver": "Bob", "amount": 100}'
```

**Mine a block:**
```bash
curl -X POST http://localhost:8080/api/mine
```

**Create a wallet:**
```bash
curl -X POST http://localhost:8080/api/wallets/generate \
  -F "userId=alice123" \
  -F "userName=Alice"
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
    amount: 100
  }),
})
.then(response => response.text())
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
        String json = "{\"sender\":\"Alice\",\"receiver\":\"Bob\",\"amount\":100}";
        
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