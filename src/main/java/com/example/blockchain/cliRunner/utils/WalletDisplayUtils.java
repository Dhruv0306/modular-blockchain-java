package com.example.blockchain.cliRunner.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.hc.core5.http.HttpEntity;

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

    /**
     * Process multipart response from wallet generation endpoint
     * 
     * @param entity The HttpEntity containing the multipart response
     * @param userId The user ID for naming the saved files
     */
    public static void processMultipartResponse(HttpEntity entity, String userId) {
        try {
            // Create directory for wallet files if it doesn't exist
            Path walletDir = Paths.get("wallets");
            if (!Files.exists(walletDir)) {
                Files.createDirectories(walletDir);
            }

            // Create temporary directory to extract parts
            Path tempDir = Files.createTempDirectory("wallet-response-");

            // Save the full response to a temporary file
            File tempFile = tempDir.resolve("response.bin").toFile();
            try (FileOutputStream outStream = new FileOutputStream(tempFile);
                    InputStream inStream = entity.getContent()) {

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
            }

            // Read the response file and extract parts
            String content = Files.readString(tempFile.toPath());

            // Extract key files from attachments
            extractAttachments(content, userId, walletDir);

            // Extract messages
            extractAndDisplayMessages(content);

            // Clean up temporary files
            Files.delete(tempFile.toPath());
            Files.delete(tempDir);

        } catch (IOException e) {
            System.out.println("Error processing multipart response: " + e.getMessage());
        }
    }

    /**
     * Extract attachments from multipart content
     */
    private static void extractAttachments(String content, String userId, Path walletDir) {
        try {
            // Look for attachment headers
            String attachmentHeader = "Content-Disposition: attachment; filename=";
            int pos = 0;

            while ((pos = content.indexOf(attachmentHeader, pos)) != -1) {
                // Extract filename
                int filenameStart = pos + attachmentHeader.length() + 1; // +1 for the quote
                int filenameEnd = content.indexOf('"', filenameStart);

                if (filenameEnd != -1) {
                    String originalFilename = content.substring(filenameStart, filenameEnd);

                    // Determine if it's a public or private key
                    String targetFilename;
                    if (originalFilename.contains("publicKey")) {
                        targetFilename = userId + "_publicKey.key";
                    } else if (originalFilename.contains("privateKey")) {
                        targetFilename = userId + "_privateKey.key";
                    } else {
                        // Skip non-key files
                        pos = filenameEnd;
                        continue;
                    }

                    // Find the start of the file content (after headers)
                    int contentStart = content.indexOf("\r\n\r\n", filenameEnd);
                    if (contentStart != -1) {
                        contentStart += 4; // Skip the double newline

                        // Find the end of this part (next boundary)
                        int contentEnd = content.indexOf("--", contentStart);
                        if (contentEnd != -1) {
                            // Extract the file content
                            String fileContent = content.substring(contentStart, contentEnd).trim();
                            
                            // Debug content
                            System.out.println("Extracted content length: " + fileContent.length());
                            
                            // If content is empty or doesn't contain key markers, try direct search
                            if (fileContent.isEmpty() || !fileContent.contains("-----BEGIN")) {
                                // Try to find key content directly in the entire response
                                String keyType = originalFilename.contains("publicKey") ? "PUBLIC" : "PRIVATE";
                                String startMarker = "-----BEGIN " + keyType + " KEY-----";
                                String endMarker = "-----END " + keyType + " KEY-----";
                                
                                // For private key, we need to be more precise to avoid the format example
                                if (keyType.equals("PRIVATE")) {
                                    // Look for the private key in the attachment section
                                    String attachmentMarker = "Content-Disposition: attachment; filename=\"" + 
                                                             originalFilename + "\"";
                                    int attachmentPos = content.indexOf(attachmentMarker);
                                    
                                    if (attachmentPos != -1) {
                                        // Find the start marker after the attachment header
                                        int keyStart = content.indexOf(startMarker, attachmentPos);
                                        if (keyStart != -1) {
                                            int keyEnd = content.indexOf(endMarker, keyStart);
                                            if (keyEnd != -1) {
                                                // Include the end marker
                                                keyEnd += endMarker.length();
                                                fileContent = content.substring(keyStart, keyEnd);
                                                System.out.println("Found private key using attachment search, length: " + fileContent.length());
                                            }
                                        }
                                    }
                                } else {
                                    // For public key, the original approach works fine
                                    int keyStart = content.indexOf(startMarker);
                                    if (keyStart != -1) {
                                        int keyEnd = content.indexOf(endMarker, keyStart);
                                        if (keyEnd != -1) {
                                            // Include the end marker
                                            keyEnd += endMarker.length();
                                            fileContent = content.substring(keyStart, keyEnd);
                                            System.out.println("Found public key using direct search, length: " + fileContent.length());
                                        }
                                    }
                                }
                            }
                            
                            // Only save if we have content
                            if (!fileContent.isEmpty()) {
                                // Save to file
                                Path filePath = walletDir.resolve(targetFilename);
                                Files.writeString(filePath, fileContent);
                                
                                System.out.println(
                                        "Saved " + (originalFilename.contains("publicKey") ? "public" : "private") +
                                                " key to: " + filePath);
                            } else {
                                System.out.println("Warning: Empty content for " + 
                                        (originalFilename.contains("publicKey") ? "public" : "private") + " key");
                            }
                        }
                    }
                }

                pos = filenameEnd + 1;
            }
        } catch (IOException e) {
            System.out.println("Error extracting attachments: " + e.getMessage());
        }
    }



    /**
     * Extract and display message parts from multipart content
     * 
     * @param content The multipart content
     */
    private static void extractAndDisplayMessages(String content) {
        // Find all message parts
        String messageHeader = "Content-Disposition: form-data; name=\"message\"";
        int pos = 0;

        while ((pos = content.indexOf(messageHeader, pos)) != -1) {
            // Find the start of the message data
            int dataStart = content.indexOf("\r\n\r\n", pos);
            if (dataStart != -1) {
                dataStart += 4; // Skip the double newline

                // Find the end of this part
                int dataEnd = content.indexOf("--", dataStart);
                if (dataEnd != -1) {
                    // Extract and display the message
                    String message = content.substring(dataStart, dataEnd).trim();
                    System.out.println("messages: " + message);
                }
            }
            pos += messageHeader.length();
        }

        // Also extract other message types (message2, message3, etc.)
        extractOtherMessages(content, "message2");
        extractOtherMessages(content, "message3");
    }

    private static void extractOtherMessages(String content, String messageName) {
        String messageHeader = "Content-Disposition: form-data; name=\"" + messageName + "\"";
        int pos = content.indexOf(messageHeader);

        if (pos != -1) {
            int dataStart = content.indexOf("\r\n\r\n", pos);
            if (dataStart != -1) {
                dataStart += 4; // Skip the double newline

                int dataEnd = content.indexOf("--", dataStart);
                if (dataEnd != -1) {
                    String message = content.substring(dataStart, dataEnd).trim();
                    System.out.println(messageName + ": " + message);
                }
            }
        }
    }
}
