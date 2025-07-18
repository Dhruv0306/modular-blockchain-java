package com.example.blockchain.wallet.utils;

import com.example.blockchain.wallet.core.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class WalletUtilsTest {

    private Wallet mockWallet;
    private String userId;
    private java.security.PrivateKey mockPrivateKey;
    private String encodedPrivateKey;

    @BeforeEach
    void setUp() throws Exception {
        // Generate a real key pair for testing
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();
        
        // Create mock wallet
        userId = "testUser";
        mockWallet = Mockito.mock(Wallet.class);
        mockPrivateKey = keyPair.getPrivate();
        
        // Set up mock behavior
        when(mockWallet.getUserId()).thenReturn(userId);
        when(mockWallet.getPrivateKey()).thenReturn(mockPrivateKey);
        
        // Encode the private key for testing
        encodedPrivateKey = Base64.getEncoder().encodeToString(mockPrivateKey.getEncoded());
    }

    @Test
    void testValidPrivateKey() {
        // Test with valid inputs
        boolean result = WalletUtils.isPrivateKeyVlid(userId, mockWallet, encodedPrivateKey);
        assertTrue(result, "Valid private key should return true");
    }

    @Test
    void testInvalidUserId() {
        // Test with mismatched user ID
        boolean result = WalletUtils.isPrivateKeyVlid("wrongUser", mockWallet, encodedPrivateKey);
        assertFalse(result, "Invalid user ID should return false");
    }

    @Test
    void testInvalidPrivateKey() {
        // Test with invalid private key
        String wrongKey = Base64.getEncoder().encodeToString("wrongKey".getBytes());
        boolean result = WalletUtils.isPrivateKeyVlid(userId, mockWallet, wrongKey);
        assertFalse(result, "Invalid private key should return false");
    }

    @Test
    void testNullUserId() {
        // Test with null user ID
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            WalletUtils.isPrivateKeyVlid(null, mockWallet, encodedPrivateKey);
        });
        assertEquals("User ID cannot be null or empty", exception.getMessage());
    }

    @Test
    void testEmptyUserId() {
        // Test with empty user ID
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            WalletUtils.isPrivateKeyVlid("", mockWallet, encodedPrivateKey);
        });
        assertEquals("User ID cannot be null or empty", exception.getMessage());
    }

    @Test
    void testNullWallet() {
        // Test with null wallet
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            WalletUtils.isPrivateKeyVlid(userId, null, encodedPrivateKey);
        });
        assertEquals("Wallet cannot be null", exception.getMessage());
    }

    @Test
    void testNullPrivateKey() {
        // Test with null private key
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            WalletUtils.isPrivateKeyVlid(userId, mockWallet, null);
        });
        assertEquals("Private key cannot be null or empty", exception.getMessage());
    }

    @Test
    void testEmptyPrivateKey() {
        // Test with empty private key
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            WalletUtils.isPrivateKeyVlid(userId, mockWallet, "");
        });
        assertEquals("Private key cannot be null or empty", exception.getMessage());
    }

    @Test
    void testInvalidBase64PrivateKey() {
        // Test with invalid Base64 encoding
        boolean result = WalletUtils.isPrivateKeyVlid(userId, mockWallet, "not-valid-base64!");
        assertFalse(result, "Invalid Base64 encoding should return false");
    }
}