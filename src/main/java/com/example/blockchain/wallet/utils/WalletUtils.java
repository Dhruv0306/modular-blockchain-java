package com.example.blockchain.wallet.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.example.blockchain.logging.LoggingUtils;
import com.example.blockchain.wallet.core.Wallet;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class for handling wallet-related operations including key
 * management,
 * wallet import/export, and validation functions.
 */
public class WalletUtils {
    // Logger instance for this class to handle logging operations
    private static final Logger logger = LoggerFactory.getLogger(WalletUtils.class);

    /**
     * Saves the public and private keys of a wallet to files in the specified
     * directory. The keys are stored in Base64 encoded format.
     *
     * @param wallet    The wallet containing the keys to save
     * @param directory The directory path where the key files will be stored
     * @param name      The base name for the key files
     * @throws Exception If there is an error creating directories or writing files
     */
    public static void saveWalletKeys(Wallet wallet, String directory, String name) throws Exception {
        // Configure logging using external configuration
        LoggingUtils.configureLoggingFromConfig();
        logger.info("Saving wallet keys for user: {}", name);

        // Create directory if it doesn't exist
        Files.createDirectories(Paths.get(directory));

        // Save public key to file with _pub.key extension
        Files.writeString(Paths.get(directory, name + "_pub.key"),
                Base64.getEncoder().encodeToString(wallet.getPublicKey().getEncoded()));

        // Save private key to file with _priv.key extension
        Files.writeString(Paths.get(directory, name + "_priv.key"),
                Base64.getEncoder().encodeToString(wallet.getPrivateKey().getEncoded()));
    }

    /**
     * Generates a temporary key file with the specified key and file name.
     * The key is stored in PEM format with appropriate headers.
     *
     * @param key      The cryptographic key to be written to the file
     * @param fileName The name of the file to create
     * @param isPublic Indicates if the key is a public key (true) or private key
     *                 (false)
     * @return The created temporary file containing the key in PEM format
     */
    public static File generateTempKeyFile(Key key, String fileName, boolean isPublic) {
        // Configure logging using external configuration
        LoggingUtils.configureLoggingFromConfig();

        // Convert key to Base64 string
        String base64 = Base64.getEncoder().encodeToString(key.getEncoded());

        // Create PEM formatted string for the key with appropriate header
        String header = isPublic ? "PUBLIC" : "PRIVATE";
        String pem = "-----BEGIN " + header + " KEY-----\n" +
                base64 + "\n" +
                "-----END " + header + " KEY-----\n";

        File tempFile = null;
        try {
            // Create a temporary file with the specified name
            tempFile = File.createTempFile(fileName.replace(".key", ""), ".key");

            // Write the PEM formatted key to the temporary file
            tempFile.deleteOnExit(); // Ensure the file is deleted on JVM exit

            // Write the PEM content to the file
            FileWriter writer = new FileWriter(tempFile);
            writer.write(pem);
            writer.close();
            logger.info("Temporary key file created: {}", tempFile.getAbsolutePath());
        } catch (IOException e) {
            logger.error("Failed to create temporary key file: {}", fileName, e);
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
        return tempFile;
    }

    /**
     * Validates a private key against a wallet and user ID.
     * Checks if the provided private key matches the one in the wallet
     * and if the user ID matches the wallet's user ID.
     *
     * @param userId     The user ID to validate against
     * @param wallet     The wallet containing the reference private key
     * @param privateKey The private key to validate (Base64 encoded)
     * @return true if the private key is valid, false otherwise
     * @throws IllegalArgumentException if any input parameter is null or empty
     */
    public static boolean isPrivateKeyVlid(String userId, Wallet wallet, String privateKey) {
        // Validate input parameters
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        if (wallet == null) {
            throw new IllegalArgumentException("Wallet cannot be null");
        }
        if (privateKey == null || privateKey.isEmpty()) {
            throw new IllegalArgumentException("Private key cannot be null or empty");
        }

        // Configure logging using external configuration
        LoggingUtils.configureLoggingFromConfig();
        logger.info("Validating private key for user: {}", userId);

        try {
            // Decode the base64 encoded private key
            byte[] decodedKey = Base64.getDecoder().decode(privateKey);
            Key privateKeyObj = wallet.getPrivateKey();

            // Compare the decoded key with the wallet's private key
            boolean isValid = userId.equals(wallet.getUserId())
                    && java.util.Arrays.equals(decodedKey, privateKeyObj.getEncoded());
            if (isValid) {
                logger.info("Private key validation successful for user: {}", userId);
            } else {
                logger.warn("Private key validation failed for user: {}", userId);
            }
            // Return the validation result
            return isValid;
        } catch (Exception e) {
            logger.error("Error validating private key for user: {}", userId, e);
            return false;
        }
    }

    /**
     * Exports wallet data to a JSON file including encoded public and private keys.
     * The keys are stored in PEM format within the JSON structure.
     *
     * @param wallet The wallet to export
     * @param userId The user ID associated with the wallet
     * @return File containing the exported wallet data in JSON format, or null if
     *         export fails
     */
    public static File exportWalletData(Wallet wallet, String userId) {
        // Configure logging using external configuration
        LoggingUtils.configureLoggingFromConfig();
        logger.info("Exporting wallet data for user: {}", userId);

        try {
            // Embed PEM strings into the wallet object
            wallet.setEncodedPrivateKey(
                    "-----BEGIN PRIVATE KEY-----\n" +
                            Base64.getEncoder().encodeToString(wallet.getPrivateKey().getEncoded()) +
                            "\n-----END PRIVATE KEY-----");
            wallet.setEncodedPublicKey(
                    "-----BEGIN PUBLIC KEY-----\n" +
                            Base64.getEncoder().encodeToString(wallet.getPublicKey().getEncoded()) +
                            "\n-----END PUBLIC KEY-----");

            ObjectMapper mapper = new ObjectMapper();
            File tempFile = File.createTempFile(userId + "_wallet", ".json");
            tempFile.deleteOnExit();
            mapper.writerWithDefaultPrettyPrinter().writeValue(tempFile, wallet);

            logger.info("Wallet data exported successfully for user: {}", userId);
            return tempFile;

        } catch (IOException e) {
            logger.error("Failed to export wallet data for user: {}", userId, e);
            return null;
        }
    }

    /**
     * Imports a wallet from a JSON file containing encoded public and private keys.
     * The keys should be in PEM format within the JSON structure.
     *
     * @param file The MultipartFile containing the wallet data in JSON format
     * @return A new Wallet instance populated with the imported data
     * @throws RuntimeException if there are any errors during import
     */
    public static Wallet importWalletFromFile(MultipartFile file) {
        Wallet wallet = null; // Initialize wallet object

        try {
            // Configure logging using external configuration
            LoggingUtils.configureLoggingFromConfig();
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(content);

            // Extract wallet data from JSON
            String userId = root.get("userId").asText();
            String userName = root.get("userName").asText();
            String privateKeyPEM = root.get("encodedPrivateKey").asText().replace("\n", "\n");
            String publicKeyPEM = root.get("encodedPublicKey").asText().replace("\n", "\n");

            // Create RSA key factory
            KeyFactory factory = KeyFactory.getInstance("RSA");

            // Decode Private Key from PEM format
            String privateKeyBase64 = extractBase64FromPem(privateKeyPEM);
            byte[] privBytes = Base64.getDecoder().decode(privateKeyBase64);
            PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privBytes);
            PrivateKey privateKey = factory.generatePrivate(privSpec);

            // Decode Public Key from PEM format
            String publicKeyBase64 = extractBase64FromPem(publicKeyPEM);
            byte[] pubBytes = Base64.getDecoder().decode(publicKeyBase64);
            X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(pubBytes);
            PublicKey publicKey = factory.generatePublic(pubSpec);

            // Create new wallet with imported data
            KeyPair keyPair = new KeyPair(publicKey, privateKey);
            wallet = new Wallet();
            wallet.setUserId(userId);
            wallet.setUserName(userName);
            wallet.setEncodedPrivateKey(Base64.getEncoder().encodeToString(privateKey.getEncoded()));
            wallet.setEncodedPublicKey(Base64.getEncoder().encodeToString(publicKey.getEncoded()));

        } catch (NoSuchAlgorithmException e) {
            logger.error("RSA algorithm not available", e);
            throw new RuntimeException("Failed to initialize key factory", e);
        } catch (InvalidKeySpecException e) {
            logger.error("Invalid key specification while importing wallet", e);
            throw new RuntimeException("Failed to generate keys from imported data", e);
        } catch (IOException e) {
            logger.error("Error reading wallet file data", e);
            throw new RuntimeException("Failed to process wallet import file", e);
        }

        return wallet;
    }

    private static String extractBase64FromPem(String pem) {
        return pem.replaceAll("-----BEGIN (.*)-----", "")
                .replaceAll("-----END (.*)-----", "")
                .replaceAll("\\s", "");
    }

    /**
     * Reads a private key from a file and returns it in Base64 encoded format.
     * This format is compatible with the isPrivateKeyVlid method.
     *
     * @param privateKeyFile The MultipartFile containing the private key
     * @return The private key as a Base64 encoded string
     * @throws RuntimeException if there are any errors reading the file
     */
    public static String readPrivateKeyFromFile(MultipartFile privateKeyFile) {
        // Configure logging using external configuration
        LoggingUtils.configureLoggingFromConfig();
        logger.info("Reading private key from file: {}", privateKeyFile.getOriginalFilename());

        try {
            // Read the file content
            String content = new String(privateKeyFile.getBytes(), StandardCharsets.UTF_8);

            // Check if the content is in PEM format
            if (content.contains("-----BEGIN PRIVATE KEY-----")) {
                // Extract the Base64 part from PEM format
                String base64Key = content
                        .replace("-----BEGIN PRIVATE KEY-----", "")
                        .replace("-----END PRIVATE KEY-----", "")
                        .replaceAll("\\r\\n|\\n|\\r", "");
                logger.info("Successfully read private key in PEM format");
                return base64Key;
            } else {
                // Assume the content is already in Base64 format
                // Validate that it's actually Base64
                try {
                    Base64.getDecoder().decode(content.trim());
                    logger.info("Successfully read private key in Base64 format");
                    return content.trim();
                } catch (IllegalArgumentException e) {
                    logger.error("File does not contain a valid Base64 encoded private key", e);
                    throw new RuntimeException("Invalid private key format", e);
                }
            }
        } catch (IOException e) {
            logger.error("Error reading private key file", e);
            throw new RuntimeException("Failed to read private key file", e);
        }
    }
}
