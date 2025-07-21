package com.example.blockchain.cliRunner;

import java.io.IOException;
import java.io.ObjectInputValidation;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

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
                case "view-chain" -> sendGet("/chain");
                case "view-pending" -> sendGet("/pending");
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
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        WalletDisplayUtils.printDivider();
        if (response.statusCode() == 200) {
            System.out.printf("Wallet created for '%s' [%s]\n", userName, userId);
            System.out.println("Wallet keys saved to disk (see wallets/ directory)");
        } else {
            System.out.println("Failed to create wallet. Server response:");
            System.out.println(response.body());
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
                Wallet Commands"
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

    public static void main(String[] args) throws Exception {
        new ApiBasedBlockchainCLI().start();
    }
}
