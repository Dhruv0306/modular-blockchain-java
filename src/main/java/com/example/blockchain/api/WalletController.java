package com.example.blockchain.api;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.blockchain.core.config.ChainConfig;
import com.example.blockchain.core.utils.PersistenceManager;
import com.example.blockchain.logging.BlockchainLoggerFactory;
import com.example.blockchain.logging.LoggingUtils;
import com.example.blockchain.wallet.DTO.WalletDTO;
import com.example.blockchain.wallet.core.Wallet;
import com.example.blockchain.wallet.core.WalletList;
import com.example.blockchain.wallet.utils.WalletUtils;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * REST Controller for managing blockchain wallets.
 * Provides endpoints for wallet CRUD operations including creation, retrieval,
 * import/export functionality and deletion. Implements wallet persistence
 * to maintain state across application restarts.
 */
@RestController
@RequestMapping("/api/wallets")
public class WalletController {
    private static final Logger logger = BlockchainLoggerFactory.getLogger(WalletController.class);

    // Central repository maintaining all wallet instances
    @Autowired
    private WalletList walletList;

    /**
     * Initializes controller and configures logging.
     * Called during Spring bean instantiation.
     */
    public WalletController() {
        LoggingUtils.configureLoggingFromConfig();
    }

    /**
     * Loads persisted wallets from disk storage on startup.
     * Only loads if current wallet list is empty.
     */
    @PostConstruct
    public void loadWalletsFromDiskIfNeeded() {
        if (walletList.isEmpty()) {
            String walletListPath = Path.of(
                    ChainConfig.getInstance().getPersistenceDirectory(),
                    ChainConfig.getInstance().getPersistenceWalletFile()).toString();

            PersistenceManager.loadWalletList(walletListPath).ifPresent(loaded -> {
                walletList.getAllWalletsAsMap().putAll(loaded.getAllWalletsAsMap());
            });
        }
    }

    /**
     * Creates a new wallet or returns existing one for a user.
     * Generates and provides downloadable public/private key pairs.
     * 
     * @param userId   Unique identifier for the user
     * @param userName Display name for the user
     * @return MultiValueMap containing wallet details and key files
     * @throws Exception if wallet creation or key generation fails
     */
    @PostMapping("/generate")
    public ResponseEntity<MultiValueMap<String, Object>> createWallet(@RequestParam("userId") String userId,
            @RequestParam("userName") String userName)
            throws Exception {
        logger.info("Creating new wallet for user: {}", userName);
        try {
            Wallet wallet = null;
            boolean alreadyExist = false;
            if (walletList.getWalletByUserID(userId) != null) {
                logger.warn("Wallet already exists for user: {}", userName);
                logger.info("Returning existing wallet for user: {}", userName);
                wallet = walletList.getWalletByUserID(userId).wallet;
                alreadyExist = true;
            } else {
                wallet = new Wallet(userId, userName);
                walletList.addWallet(userId, userName, wallet);
                WalletUtils.saveWalletKeys(wallet, "data/wallet-data", userId);
                logger.info("Successfully created wallet for user: {}", userName);
            }

            // Generate temporary key files for the wallet
            logger.debug("Generating temporary key files for user: {}", userName);
            File publicKeyFile = WalletUtils.generateTempKeyFile(wallet.getPublicKey(), userId + "_publicKey.key",
                    true);
            File privateKeyFile = WalletUtils.generateTempKeyFile(wallet.getPrivateKey(), userId + "_privateKey.key",
                    false);

            // Set content-disposition for download
            HttpHeaders publicKeyHeaders = new HttpHeaders();
            publicKeyHeaders
                    .setContentDisposition(ContentDisposition.attachment().filename(publicKeyFile.getName()).build());

            HttpHeaders privateKeyHeaders = new HttpHeaders();
            privateKeyHeaders
                    .setContentDisposition(ContentDisposition.attachment().filename(privateKeyFile.getName()).build());

            HttpEntity<FileSystemResource> publicKeyEntity = new HttpEntity<>(new FileSystemResource(publicKeyFile),
                    publicKeyHeaders);
            HttpEntity<FileSystemResource> privateKeyEntity = new HttpEntity<>(new FileSystemResource(privateKeyFile),
                    privateKeyHeaders);

            // Build multipart response
            logger.debug("Building response for wallet creation");
            MultiValueMap<String, Object> responseBody = new LinkedMultiValueMap<>();
            if (!alreadyExist)
                responseBody.add("message", "Wallet created for User:{Name: " + userName + ", ID: " + userId + "}");
            if (!alreadyExist)
                responseBody.add("message2",
                        "Your wallet has been created successfully. Please download your keys below.");
            if (!alreadyExist)
                responseBody.add("message3",
                        "Keep your private key safe. It will be required to authenticate wallet deletion and exports.");
            responseBody.add("Use: ",
                    "Use the public key for transactions and the private key for wallet operations.");
            responseBody.add("Use Method",
                    "Add Private Key for deleting wallet or exporting Wallet Data.");
            responseBody.add("private Key Use Format",
                    "Private Key Fromate: -----BEGIN PRIVATE KEY-----<base64_encoded_private_key>-----END PRIVATE KEY-----");

            responseBody.add("publicKey", publicKeyEntity);
            responseBody.add("privateKey", privateKeyEntity);

            return ResponseEntity.ok()
                    .contentType(MediaType.MULTIPART_MIXED)
                    .body(responseBody);
        } catch (Exception e) {
            logger.error("Failed to create wallet for user: {}", userName, e);
            throw e;
        }
    }

    /**
     * Lists all wallets in the system.
     * Returns wallet data transfer objects containing public information only.
     * 
     * @return List of WalletDTO objects
     */
    @GetMapping
    public List<WalletDTO> list() {
        logger.debug("Retrieving list of all wallets");
        return walletList.getAllWalletEntries().stream()
                .map(t -> {
                    try {
                        return new WalletDTO(t);
                    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                        logger.error("Error retrieving wallet data", e.getMessage());
                        return new WalletDTO();
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Retrieves public keys for all wallets.
     * Returns map of user IDs to PEM-formatted public keys.
     * 
     * @return Map of user IDs to public key strings
     */
    @GetMapping("/public-keys")
    public Map<Object, Object> getPublicKeys() {
        logger.debug("Retrieving public keys of all wallets");
        return walletList.getAllWalletsAsMap().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                    try {
                        PublicKey publicKey = entry.getValue().wallet.getPublicKey();
                        String base64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
                        return "-----BEGIN PUBLIC KEY-----\n" + base64 + "\n-----END PUBLIC KEY-----";
                    } catch (Exception e) {
                        logger.error("Error retrieving public key for user: {}", entry.getKey(), e);
                        return "Error retrieving public key";
                    }
                }));
    }

    /**
     * Gets public key for specific user.
     * Returns PEM-formatted public key string.
     * 
     * @param userId Target user identifier
     * @return Public key string or error message
     */
    @GetMapping("/public-key")
    public String getPublicKey(@RequestParam("userId") String userId) {
        logger.debug("Retrieving public key for user ID: {}", userId);
        Wallet wallet = walletList.getWalletByUserID(userId).wallet;
        if (wallet != null) {
            PublicKey publicKey;
            try {
                publicKey = wallet.getPublicKey();
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                logger.error("Error retrieving public key for user ID: " + userId + ". " + e.getMessage());
                String error = "Error retrieving public key for user ID: " + userId + ". " + e.getMessage();
                return error;
            }
            String base64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            return "-----BEGIN PUBLIC KEY-----\n" + base64 + "\n-----END PUBLIC KEY-----";
        } else {
            logger.warn("No wallet found for user ID: {}", userId);
            return "No wallet found for User ID: " + userId;
        }
    }

    /**
     * Exports wallet data after validating ownership.
     * Requires private key authentication.
     * 
     * @param userId     User identifier
     * @param privateKey Private key file for authentication
     * @return Wallet data file and status messages
     */
    @GetMapping("/export")
    public ResponseEntity<MultiValueMap<String, Object>> exportWalletData(@RequestParam("userId") String userId,
            @RequestParam("privateKey") MultipartFile privateKey) {
        // Validate user ID and private key
        if (userId == null || userId.isEmpty() || privateKey == null || privateKey.isEmpty()) {
            logger.warn("Either of user ID or private key is missing.");
            MultiValueMap<String, Object> errorBody = new LinkedMultiValueMap<>();
            errorBody.add("Error", "User ID or Private key is missing.");
            return ResponseEntity.badRequest().body(errorBody);
        }

        // Validate the userId against the wallet
        logger.debug("Exporting wallet data for user ID: {}", userId);
        Wallet wallet = walletList.getWalletByUserID(userId).wallet;
        if (wallet == null) {
            logger.warn("No wallet found for user ID: {}", userId);
            MultiValueMap<String, Object> errorBody = new LinkedMultiValueMap<>();
            errorBody.add("Error", "No wallet found for User ID: " + userId + ". Please create a wallet first.");
            return ResponseEntity.badRequest().body(errorBody);
        }

        // Read Private Key from the file provided
        String privateKeyString;
        try {
            privateKeyString = WalletUtils.readPrivateKeyFromFile(privateKey);
        } catch (IOException e) {
            logger.error("Error reading private key file: {}", e.getMessage());
            MultiValueMap<String, Object> errorBody = new LinkedMultiValueMap<>();
            errorBody.add("Error", "Failed to read private key file: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorBody);
        }

        if (privateKeyString == null || privateKeyString.isEmpty()) {
            logger.warn("Private key file is empty.");
            MultiValueMap<String, Object> errorBody = new LinkedMultiValueMap<>();
            errorBody.add("Error", "Private key file is empty.");
            return ResponseEntity.badRequest().body(errorBody);
        }

        // Validate the private key
        try {
            if (!WalletUtils.isPrivateKeyVlid(userId, wallet, privateKeyString)) {
                logger.warn("Invalid private key provided for user ID: {}", userId);
                MultiValueMap<String, Object> errorBody = new LinkedMultiValueMap<>();
                errorBody.add("Error", "Invalid private key provided for User ID: " + userId);
                return ResponseEntity.badRequest().body(errorBody);
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            logger.error("Error Validating Privatekey. \nError: ", e.getMessage());
            String error = "Error Validating Privatekey. \nError: " + e.getMessage();
            MultiValueMap<String, Object> errorBody = new LinkedMultiValueMap<>();
            errorBody.add("Error", error);
            return ResponseEntity.badRequest().body(errorBody);
        }

        try {
            File walletFile = WalletUtils.exportWalletData(wallet, userId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.attachment().filename(walletFile.getName()).build());
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            FileSystemResource resource = new FileSystemResource(walletFile);
            HttpEntity<FileSystemResource> entity = new HttpEntity<>(resource, headers);

            MultiValueMap<String, Object> responseBody = new LinkedMultiValueMap<>();
            responseBody.add("message", "Wallet data exported successfully for User ID: " + userId);
            responseBody.add("tip", "You can use this wallet file to restore or migrate your wallet in future.");
            responseBody.add("walletData", entity);

            return ResponseEntity.ok()
                    .contentType(MediaType.MULTIPART_MIXED)
                    .body(responseBody);
        } catch (Exception e) {
            logger.error("Failed to export wallet data for user ID: {}", userId, e);
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * Imports wallet from backup file.
     * Validates wallet data integrity before import.
     * 
     * @param file Wallet backup file
     * @return Success/failure status message
     */
    @PostMapping("/import")
    public ResponseEntity<String> importWallet(@RequestParam("file") MultipartFile file) {
        // Validate that a non-empty file was provided
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty. Please upload a valid wallet file.");
        }
        try {
            // Import and validate the wallet data from the uploaded file
            Wallet wallet = WalletUtils.importWalletFromFile(file);
            if (wallet == null) {
                logger.warn("Invalid wallet data in file: {}", file.getOriginalFilename());
                return ResponseEntity.badRequest().body("Invalid wallet data in file.");
            }

            // Check if a wallet with this ID already exists in the system
            if (walletList.getWalletByUserID(wallet.getUserId()) != null) {
                logger.warn("Wallet already exists for user ID: {}", wallet.getUserId());
                return ResponseEntity.badRequest().body("Wallet already exists for User ID: " + wallet.getUserId());
            }

            // Add the valid wallet to the system's wallet list
            logger.info("Adding imported wallet for user: {}", wallet.getUserName());
            walletList.addWallet(wallet.getUserId(), wallet.getUserName(), wallet);
            return ResponseEntity.ok("Wallet imported successfully for user: " + wallet.getUserName());
        } catch (Exception e) {
            logger.error("Failed to import wallet", e);
            return ResponseEntity.status(500).body("Internal error during import.");
        }
    }

    /**
     * Deletes wallet after ownership verification.
     * Requires private key authentication.
     * 
     * @param userId     User identifier
     * @param privateKey Private key file for authentication
     * @return Success/failure status message
     */
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteWallet(@RequestParam("userId") String userId,
            @RequestParam("privateKey") MultipartFile privateKey) {
        // Verify the wallet exists
        Wallet wallet = walletList.getWalletByUserID(userId).wallet;
        if (wallet == null) {
            return ResponseEntity.badRequest().body("No wallet found with userId: " + userId);
        }

        // Check if the private key file is empty
        if (privateKey == null || privateKey.isEmpty()) {
            return ResponseEntity.badRequest().body("Private key file is empty.");
        }

        // Read Private Key from the file provided
        String privateKeyString;
        try {
            privateKeyString = WalletUtils.readPrivateKeyFromFile(privateKey);
        } catch (IOException e) {
            logger.error("Error reading private key file: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Failed to read private key file: " + e.getMessage());
        }
        if (privateKeyString == null || privateKeyString.isEmpty()) {
            logger.warn("Private key file is empty.");
            return ResponseEntity.badRequest().body("Private key file is empty.");
        }

        // Validate the provided private key matches the wallet
        try {
            if (!WalletUtils.isPrivateKeyVlid(userId, wallet, privateKeyString)) {
                return ResponseEntity.status(403).body("Private key does not match. Deletion unauthorized.");
            }
        } catch (NoSuchAlgorithmException e) {
            logger.error("Error validating private key: " + e.getMessage());
            String error = "Error validating private key: " + e.getMessage();
            return ResponseEntity.status(500).body(error);
        } catch (InvalidKeySpecException e) {
            logger.error("Error validating private key: " + e.getMessage());
            String error = "Error validating private key: " + e.getMessage();
            return ResponseEntity.status(500).body(error);
        }

        // Remove the wallet if validation passes
        walletList.removeWallet(userId);
        return ResponseEntity.ok("Wallet deleted successfully for userId: " + userId);
    }

    /**
     * Persists wallet list to disk on shutdown.
     * Automatically called by Spring container.
     */
    @PreDestroy
    public void saveWallets() {
        logger.info("Saving wallet list before shutdown");
        String path = Path.of(ChainConfig.getInstance().getPersistenceDirectory(),
                ChainConfig.getInstance().getPersistenceWalletFile()).toString();
        PersistenceManager.saveWalletList(walletList, path);
        logger.debug("Wallet list saved to path: {}", path);
    }
}
