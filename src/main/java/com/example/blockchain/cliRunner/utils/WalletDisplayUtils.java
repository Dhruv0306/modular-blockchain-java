package com.example.blockchain.cliRunner.utils;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class WalletDisplayUtils {
    public static void printDivider() {
        System.out.println("=".repeat(60));
    }

    public static void displayPublicKeyMap(String jsonResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> keys = mapper.readValue(jsonResponse, Map.class);

            System.out.println("Registered Public Keys:");
            System.out.println("+----------------+---------------------------------------------+");
            System.out.println("| User ID        | Public Key (Shortened)                     |");
            System.out.println("+----------------+---------------------------------------------+");

            for (Map.Entry<String, String> entry : keys.entrySet()) {
                String userId = entry.getKey();
                String key = entry.getValue().replace("-----BEGIN PUBLIC KEY-----", "")
                        .replace("-----END PUBLIC KEY-----", "")
                        .replace("\r", "").replace("\n", "").replace("\s", "");
                String shortened = key.length() > 40 ? key.substring(0, 40) + "..." : key;
                System.out.printf("| %-14s | %-43s |\n", userId, shortened);
            }

            System.out.println("+----------------+---------------------------------------------+");
        } catch (Exception e) {
            System.out.println("Failed to parse public keys: " + e.getMessage());
        }
    }
}
