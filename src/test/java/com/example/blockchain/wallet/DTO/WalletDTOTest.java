package com.example.blockchain.wallet.DTO;

import static org.junit.jupiter.api.Assertions.*;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.blockchain.wallet.core.Wallet;
import com.example.blockchain.wallet.core.WalletList;

class WalletDTOTest {

    private KeyPair keyPair;
    private Wallet wallet;
    private WalletList.WalletEntry walletEntry;
    
    @BeforeEach
    void setUp() throws Exception {
        // Generate a real key pair for testing
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        keyPair = generator.generateKeyPair();
        
        // Create a real wallet with the key pair
        wallet = new Wallet("testUser", "Test User");
        
        // Create a wallet entry
        walletEntry = new WalletList.WalletEntry("entryUser", "Entry User", wallet);
    }
    
    @Test
    void testDefaultConstructor() {
        // Act
        WalletDTO dto = new WalletDTO();
        
        // Assert
        assertNull(dto.getUserId());
        assertNull(dto.getUserName());
        assertNull(dto.getPublicKeyBase64());
    }
    
    @Test
    void testConstructorFromWallet() throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Act
        WalletDTO dto = new WalletDTO(wallet);
        
        // Assert
        assertEquals("testUser", dto.getUserId());
        assertEquals("Test User", dto.getUserName());
        assertNotNull(dto.getPublicKeyBase64());
    }
    
    @Test
    void testConstructorFromWalletEntry() throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Act
        WalletDTO dto = new WalletDTO(walletEntry);
        
        // Assert
        assertEquals("entryUser", dto.getUserId());
        assertEquals("Entry User", dto.getUserName());
        assertNotNull(dto.getPublicKeyBase64());
    }
    
    @Test
    void testGetters() throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Arrange
        WalletDTO dto = new WalletDTO(wallet);
        
        // Act & Assert
        assertEquals("testUser", dto.getUserId());
        assertEquals("Test User", dto.getUserName());
        assertNotNull(dto.getPublicKeyBase64());
    }
}