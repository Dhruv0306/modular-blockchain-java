package com.example.blockchain.cliRunner;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import com.example.blockchain.cliRunner.utils.WalletDisplayUtils;

public class ApiBasedBlockchainCLI {
    private static final String API_BASE_URL = "http://localhost:8080/api";
    private final HttpClient client;

    public ApiBasedBlockchainCLI() {
        this.client = HttpClient.newHttpClient();
    }

    public void start() throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=".repeat(50));
        System.out.println("Welcome to the Blockchain CLI");
        System.out.println("Connected to Blockchain API CLI");
        System.out.println("Type 'help' for available commands.");
        System.out.println("=".repeat(50));

        boolean running = true;

        while (running) {
            System.out.print("> ");
            String command = scanner.nextLine().trim();

            try {
                switch (command) {
                    case "help" -> showHelp();
                    case "view-chain" -> getChain();
                    case "view-pending" -> getPendingTransactions();
                    case "add-tx" -> addTransaction(scanner);
                    case "mine" -> mineBlock();
                    case "validate-chain" -> validateChain();
                    case "create-wallet" -> createWallet(scanner);
                    case "get-public-keys" -> getPublicKeys();
                    case "get-public-key" -> getPublicKey(scanner);
                    case "import-wallet" -> importWallet(scanner);
                    case "export-wallet" -> exportWallet(scanner);
                    case "delete-wallet" -> deleteWallet(scanner);
                    case "exit" -> {
                        running = false;
                        System.out.println("Exiting CLI. Goodbye!");
                    }
                    default -> System.out.println("Unknown command. Type 'help' for available commands.");
                }
            } catch (Exception e) {
                System.out.println("Error executing command: " + e.getMessage());
                e.printStackTrace();
            }
        }
        scanner.close();
    }

    // Method to create a wallet with proper status code handling
    private void createWallet(Scanner scanner) throws Exception {
        System.out.print("Enter User ID: ");
        String userId = scanner.nextLine();
        System.out.print("Enter User Name: ");
        String userName = scanner.nextLine();

        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(API_BASE_URL + "/wallets/generate");

            // Add form parameters
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("userId", userId));
            params.add(new BasicNameValuePair("userName", userName));
            httpPost.setEntity(new UrlEncodedFormEntity(params));

            // Execute and get the response
            httpClient.execute(httpPost, response -> {
                int statusCode = response.getCode();
                HttpEntity entity = response.getEntity();
                String contentType = entity.getContentType();

                if (statusCode == 201) {
                    System.out.println("Wallet created successfully!");
                    if (contentType != null && contentType.contains("multipart")) {
                        // Handle multipart response (download keys)
                        WalletDisplayUtils.processMultipartResponse(entity, userId);
                    }
                } else if (statusCode == 200) {
                    System.out.println("Wallet already exists, returning existing wallet.");
                    if (contentType != null && contentType.contains("multipart")) {
                        WalletDisplayUtils.processMultipartResponse(entity, userId);
                    }
                } else if (statusCode == 400) {
                    System.out.println("Bad request: " + EntityUtils.toString(entity));
                } else if (statusCode == 500) {
                    System.out.println("Server error: " + EntityUtils.toString(entity));
                } else {
                    System.out.println("Unexpected status code: " + statusCode);
                    System.out.println(EntityUtils.toString(entity));
                }
                return null;
            });
        } catch (Exception e) {
            System.out.println("Error during wallet creation: " + e.getMessage());
        }
    }

    // Method to get public keys with proper status code handling
    private void getPublicKeys() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/wallets/public-keys"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            System.out.println("Public Keys:");
            WalletDisplayUtils.displayPublicKeyMap(response.body());
        } else {
            System.out.println("Failed to retrieve public keys. Status code: " + response.statusCode());
            System.out.println("Error: " + response.body());
        }
    }

    // Method to get a specific public key with proper status code handling
    private void getPublicKey(Scanner scanner) throws Exception {
        System.out.print("Enter User ID: ");
        String userId = scanner.nextLine();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/wallets/public-key?userId=" + userId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            System.out.println("Public Key for " + userId + ":");
            System.out.println(response.body());
        } else if (response.statusCode() == 404) {
            System.out.println("No wallet found for User ID: " + userId);
        } else if (response.statusCode() == 500) {
            System.out.println("Server error: " + response.body());
        } else {
            System.out.println("Error (Status " + response.statusCode() + "): " + response.body());
        }
    }

    // Method to import wallet with proper status code handling
    private void importWallet(Scanner scanner) throws Exception {
        System.out.println("Please provide the path to the wallet file:");
        String filePath = scanner.nextLine();

        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("File not found: " + filePath);
            return;
        }

        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(API_BASE_URL + "/wallets/import");

            // Create multipart/form-data request
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("file", file, ContentType.APPLICATION_OCTET_STREAM, file.getName());
            HttpEntity multipart = builder.build();
            httpPost.setEntity(multipart);

            // Execute and get the response using the non-deprecated method
            httpClient.execute(httpPost, response -> {
                int statusCode = response.getCode();
                HttpEntity entity = response.getEntity();
                String responseBody = EntityUtils.toString(entity);

                if (statusCode == 201) {
                    System.out.println("Wallet imported successfully: " + responseBody);
                } else if (statusCode == 400) {
                    System.out.println("Invalid wallet data: " + responseBody);
                } else if (statusCode == 409) {
                    System.out.println("Wallet already exists: " + responseBody);
                } else if (statusCode == 500) {
                    System.out.println("Server error: " + responseBody);
                } else {
                    System.out.println("Error (Status " + statusCode + "): " + responseBody);
                }
                return null;
            });
        } catch (Exception e) {
            System.out.println("Error importing wallet: " + e.getMessage());
        }
    }

    // Method to export wallet with proper status code handling
    private void exportWallet(Scanner scanner) throws Exception {
        System.out.print("Enter User ID: ");
        String userId = scanner.nextLine();
        System.out.println("Enter path to private key file:");
        String privateKeyPath = scanner.nextLine();

        File privateKeyFile = new File(privateKeyPath);
        if (!privateKeyFile.exists()) {
            System.out.println("Private key file not found: " + privateKeyPath);
            return;
        }

        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();

            // Create multipart request
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addTextBody("userId", userId);
            builder.addBinaryBody("privateKey", privateKeyFile, ContentType.APPLICATION_OCTET_STREAM,
                    privateKeyFile.getName());

            HttpGet httpGet = new HttpGet(API_BASE_URL + "/wallets/export");
            httpGet.setEntity(builder.build());

            // Execute and get the response
            httpClient.execute(httpGet, response -> {
                int statusCode = response.getCode();
                HttpEntity entity = response.getEntity();
                String contentType = entity.getContentType();

                if (statusCode == 200) {
                    System.out.println("Wallet exported successfully!");
                    if (contentType != null && contentType.contains("multipart")) {
                        // Handle multipart response (download wallet data)
                        WalletDisplayUtils.processMultipartResponse(entity, userId);
                    } else {
                        System.out.println(EntityUtils.toString(entity));
                    }
                } else if (statusCode == 400) {
                    System.out.println("Bad request: " + EntityUtils.toString(entity));
                } else if (statusCode == 403) {
                    System.out.println("Authentication failed: " + EntityUtils.toString(entity));
                } else if (statusCode == 404) {
                    System.out.println("Wallet not found: " + EntityUtils.toString(entity));
                } else if (statusCode == 500) {
                    System.out.println("Server error: " + EntityUtils.toString(entity));
                } else {
                    System.out.println("Unexpected status code: " + statusCode);
                    System.out.println(EntityUtils.toString(entity));
                }
                return null;
            });
        } catch (Exception e) {
            System.out.println("Error during wallet export: " + e.getMessage());
        }
    }

    // Method to delete wallet with proper status code handling
    private void deleteWallet(Scanner scanner) throws Exception {
        System.out.print("Enter User ID to delete: ");
        String userId = scanner.nextLine();
        System.out.println("Enter path to private key file (for authentication):");
        String privateKeyPath = scanner.nextLine();

        File privateKeyFile = new File(privateKeyPath);
        if (!privateKeyFile.exists()) {
            System.out.println("Private key file not found: " + privateKeyPath);
            return;
        }

        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();

            // Create multipart request for DELETE operation
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addTextBody("userId", userId);
            builder.addBinaryBody("privateKey", privateKeyFile, ContentType.APPLICATION_OCTET_STREAM,
                    privateKeyFile.getName());

            // Since Java HTTP client doesn't support DELETE with body easily, we'll use
            // Apache HttpClient
            HttpDelete httpDelete = new HttpDelete(API_BASE_URL + "/wallets/delete");
            httpDelete.setHeader("X-HTTP-Method-Override", "DELETE"); // Some APIs support this pattern
            httpDelete.setEntity(builder.build());

            // Execute and get the response
            httpClient.execute(httpDelete, response -> {
                int statusCode = response.getCode();
                HttpEntity entity = response.getEntity();
                String responseBody = EntityUtils.toString(entity);

                if (statusCode == 200) {
                    System.out.println("Wallet deleted successfully: " + responseBody);
                } else if (statusCode == 400) {
                    System.out.println("Bad request: " + responseBody);
                } else if (statusCode == 403) {
                    System.out.println("Authentication failed: " + responseBody);
                } else if (statusCode == 404) {
                    System.out.println("Wallet not found: " + responseBody);
                } else if (statusCode == 500) {
                    System.out.println("Server error: " + responseBody);
                } else {
                    System.out.println("Error (Status " + statusCode + "): " + responseBody);
                }
                return null;
            });
        } catch (Exception e) {
            System.out.println("Error during wallet deletion: " + e.getMessage());
        }
    }

    private void showHelp() {
        System.out.println("=".repeat(50));
        System.out.println("""
                Available Commands:
                help             - Show this help message
                view-chain       - View full blockchain
                view-pending     - Show transactions in mempool
                add-tx           - Add new transaction
                mine             - Mine a block
                validate-chain   - Check blockchain integrity
                exit             - Quit CLI
                Wallet Commands:
                create-wallet     - Create a new wallet with userId and name
                get-public-keys   - List all userId → publicKey mappings
                get-public-key    - Get public key for a specific userId
                import-wallet     - Import wallet from a file
                export-wallet     - Export wallet to a file
                delete-wallet     - Delete a wallet
                """);
        System.out.println("=".repeat(50));
    }

    private void sendGet(String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + endpoint))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Handle different status codes
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            System.out.println(response.body());
        } else {
            System.out.println("Error (Status " + response.statusCode() + "): " + response.body());
        }
    }

    private void sendPost(String endpoint, String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Handle different status codes
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            System.out.println("Success: " + response.body());
        } else {
            System.out.println("Error (Status " + response.statusCode() + "): " + response.body());
        }
    }

    private void addTransaction(Scanner scanner) throws Exception {
        System.out.print("Sender: ");
        String sender = scanner.nextLine();
        System.out.print("Receiver: ");
        String receiver = scanner.nextLine();
        System.out.print("Amount: ");
        String amount = scanner.nextLine();
        System.out.print("Sender ID: ");
        String senderId = scanner.nextLine();
        System.out.print("ReceiverID: ");
        String receiverId = scanner.nextLine();
        System.out.print("""
                Enter type of the transection.
                Use 1 for FinancialTransaction.
                Use 2 for SignedFinancialTransaction.
                type:
                """);
        String type = (scanner.nextLine().equals("1")) ? "FinancialTransaction" : "SignedFinancialTransaction";
        String jsonBody = String.format("""
                    {
                      "sender": "%s",
                      "receiver": "%s",
                      "amount": %s,
                      "senderID": "%s",
                      "receiverID": "%s",
                      "type": "%s"
                    }
                """, sender, receiver, amount, senderId, receiverId, type);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/transactions"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 201) {
            System.out.println("Transaction created successfully: " + response.body());
        } else if (response.statusCode() == 400) {
            System.out.println("Invalid transaction data: " + response.body());
        } else if (response.statusCode() == 422) {
            System.out.println("Transaction validation failed: " + response.body());
        } else {
            System.out.println("Error (Status " + response.statusCode() + "): " + response.body());
        }
    }

    private void getChain() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/chain"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            // Parse the JSON response to extract block information
            ObjectMapper mapper = new ObjectMapper();
            try {
                List<Map<String, Object>> blocks = mapper.readValue(response.body(),
                        new TypeReference<List<Map<String, Object>>>() {
                        });

                System.out.println("==".repeat(25));
                System.out.println("BLOCKCHAIN");
                System.out.println("==".repeat(25));

                for (int i = 0; i < blocks.size(); i++) {
                    Map<String, Object> block = blocks.get(i);
                    System.out.println("Block #" + block.get("index"));
                    System.out.println("Hash: " + block.get("hash"));
                    System.out.println("Previous Hash: " + block.get("previousHash"));
                    System.out.println("Timestamp: " + new Date((Long) block.get("timestamp")));
                    System.out.println("Nonce: " + block.get("nonce"));

                    List<Map<String, Object>> transactions = (List<Map<String, Object>>) block.get("transactions");
                    System.out.println("Transactions: " + transactions.size());

                    for (int j = 0; j < transactions.size(); j++) {
                        Map<String, Object> tx = transactions.get(j);
                        System.out.println("  TX #" + (j + 1) + ": " + tx.get("sender") + " -> " +
                                tx.get("receiver") + ", Amount: " + tx.get("amount"));
                    }
                    System.out.println("-".repeat(50));
                }
            } catch (Exception e) {
                System.out.println("Error parsing blockchain data: " + e.getMessage());
                System.out.println(response.body());
            }
        } else {
            System.out.println("Failed to retrieve blockchain. Status code: " + response.statusCode());
            System.out.println("Message: " + response.body());
        }
    }

    private void getPendingTransactions() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/pending"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                List<Map<String, Object>> transactions = mapper.readValue(response.body(),
                        new TypeReference<List<Map<String, Object>>>() {
                        });

                System.out.println("==".repeat(25));
                System.out.println("PENDING TRANSACTIONS");
                System.out.println("==".repeat(25));

                if (transactions.isEmpty()) {
                    System.out.println("No pending transactions in mempool.");
                } else {
                    System.out.println("Total pending transactions: " + transactions.size());
                    System.out.println("-".repeat(50));

                    for (int i = 0; i < transactions.size(); i++) {
                        Map<String, Object> tx = transactions.get(i);
                        System.out.println("TX #" + (i + 1));
                        System.out.println("  ID: " + tx.get("transactionId"));
                        System.out.println("  From: " + tx.get("sender") + " (ID: " + tx.get("senderID") + ")");
                        System.out.println("  To: " + tx.get("receiver") + " (ID: " + tx.get("receiverID") + ")");
                        System.out.println("  Amount: " + tx.get("amount"));
                        System.out.println("  Type: " + tx.get("type"));
                        System.out.println("-".repeat(50));
                    }
                }
            } catch (Exception e) {
                System.out.println("Error parsing pending transactions: " + e.getMessage());
                System.out.println(response.body());
            }
        } else {
            System.out.println("Failed to retrieve pending transactions. Status code: " + response.statusCode());
            System.out.println("Message: " + response.body());
        }
    }

    private void mineBlock() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/mine"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 201) {
            System.out.println("Block mined successfully: " + response.body());
        } else if (response.statusCode() == 404) {
            System.out.println("No transactions to mine: " + response.body());
        } else if (response.statusCode() == 422) {
            System.out.println("Block validation failed: " + response.body());
        } else {
            System.out.println("Mining error (Status " + response.statusCode() + "): " + response.body());
        }
    }

    private void validateChain() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/validate"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            System.out.println("Blockchain validation result: " + response.body());
        } else if (response.statusCode() == 409) {
            System.out.println("Blockchain validation failed: " + response.body());
        } else {
            System.out.println("Validation error (Status " + response.statusCode() + "): " + response.body());
        }
    }

    public static void main(String[] args) throws Exception {
        new ApiBasedBlockchainCLI().start();
    }
}