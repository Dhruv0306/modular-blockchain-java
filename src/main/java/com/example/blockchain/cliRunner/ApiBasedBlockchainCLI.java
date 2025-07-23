package com.example.blockchain.cliRunner;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;

import com.example.blockchain.cliRunner.utils.WalletDisplayUtils;

public class ApiBasedBlockchainCLI {
    private static final String API_BASE_URL = "http://localhost:8080/api";
    private final HttpClient client;

    public ApiBasedBlockchainCLI() {
        this.client = HttpClient.newHttpClient();
    }

    public void start() throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=".repeat(20));
        System.out.println("Welcome to the Blockchain CLI");
        System.out.println("Connected to Blockchain API CLI");
        System.out.println("Type 'help' for available commands.");
        System.out.println("=".repeat(20));

        while (true) {
            System.out.println("\n>> ");
            String command = scanner.nextLine().trim();

            switch (command) {
                case "help" -> showHelp();
                case "view-chain" -> getChain();
                case "view-pending" -> getPendingTransactions();
                case "add-tx" -> addTransaction(scanner);
                case "mine" -> sendPost("/mine", "");
                case "validate-chain" -> sendGet("/validate");
                case "create-wallet" -> createWallet(scanner);
                case "get-public-keys" -> getPublicKeys();
                case "get-public-key" -> getPublicKey(scanner);
                case "exit" -> {
                    System.out.println("Exiting CLI.");
                    return;
                }
                default -> System.out.println("Unknown command. Type 'help'.");
            }
        }
    }

    private void getPublicKeys() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/wallets/public-keys"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        WalletDisplayUtils.printDivider();
        if (response.statusCode() == 200) {
            WalletDisplayUtils.displayPublicKeyMap(response.body());
        } else {
            System.out.println("Failed to retrieve public keys");
            System.out.println(response.body());
        }
        WalletDisplayUtils.printDivider();
    }

    private void getPublicKey(Scanner scanner) throws Exception {
        System.out.print("Enter userId: ");
        String userId = scanner.nextLine().trim();
        String url = API_BASE_URL + "/wallets/public-key?userId=" + userId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        WalletDisplayUtils.printDivider();
        System.out.println("Response status code: " + response.statusCode());
        if (response.body().startsWith("-----BEGIN PUBLIC KEY-----")) {
            System.out.println("Public Key for '" + userId + "':");
            System.out.println(response.body());
        } else if (response.body().startsWith("No wallet found for User ID:")) {
            System.out.println("No wallet found for userId: " + userId);
        } else {
            System.out.println("Unexpected error: " + response.statusCode());
            System.out.println(response.body());
        }
        WalletDisplayUtils.printDivider();
    }

    private void createWallet(Scanner scanner) throws IOException, InterruptedException {
        System.out.print("User ID: ");
        String userId = scanner.nextLine();
        System.out.print("User Name: ");
        String userName = scanner.nextLine();

        String url = String.format("%s/wallets/generate?userId=%s&userName=%s", API_BASE_URL, userId, userName);

        // Create directory for wallet files if it doesn't exist
        Path walletDir = Paths.get("wallets");
        if (!Files.exists(walletDir)) {
            Files.createDirectories(walletDir);
        }

        // Use Apache HttpComponents for handling multipart response
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            
            // Use the recommended HttpClientResponseHandler approach
            httpClient.execute(httpPost, response -> {
                int statusCode = response.getCode();
                HttpEntity entity = response.getEntity();
                
                WalletDisplayUtils.printDivider();
                if (statusCode == 200) {
                    System.out.printf("Wallet created for '%s' [%s]%n", userName, userId);
                    
                    // Check if response is multipart
                    Header contentTypeHeader = response.getHeader("Content-Type");
                    String contentType = contentTypeHeader != null ? contentTypeHeader.getValue() : "";
                    
                    if (contentType.contains("multipart")) {
                        // Process multipart response using utility method
                        WalletDisplayUtils.processMultipartResponse(entity, userId);
                    } else {
                        // Handle as regular response
                        System.out.println("Response received but not in multipart format");
                        System.out.println("Content-Type: " + contentType);
                    }
                } else {
                    System.out.println("Failed to create wallet. Status code: " + statusCode);
                    if (entity != null) {
                        try {
                            System.out.println(org.apache.hc.core5.http.io.entity.EntityUtils.toString(entity));
                        } catch (ParseException e) {
                            System.out.println("Error parsing response body: " + e.getMessage());
                        }
                    }
                }
                return null; // The response handler must return something
            });
        } catch (Exception e) {
            System.out.println("Error during wallet creation: " + e.getMessage());
        }
        WalletDisplayUtils.printDivider();
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
                """);
        System.out.println("=".repeat(50));
    }

    private void sendGet(String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + endpoint))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }

    private void sendPost(String endpoint, String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
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

        sendPost("/transactions", jsonBody);
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
                        new TypeReference<List<Map<String, Object>>>() {});
                
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
                        System.out.println("  TX #" + (j+1) + ": " + tx.get("sender") + " -> " + 
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
            System.out.println(response.body());
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
                        new TypeReference<List<Map<String, Object>>>() {});
                
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
                        System.out.println("TX #" + (i+1));
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
            System.out.println(response.body());
        }
    }
    public static void main(String[] args) throws Exception {
        new ApiBasedBlockchainCLI().start();
    }
}