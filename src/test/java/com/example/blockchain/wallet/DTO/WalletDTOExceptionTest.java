package com.example.blockchain.wallet.DTO;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.blockchain.wallet.core.Wallet;
import com.example.blockchain.wallet.core.WalletList;

@ExtendWith(MockitoExtension.class)
class WalletDTOExceptionTest {

    @Mock
    private Wallet wallet;
    
    @Mock
    private WalletList.WalletEntry walletEntry;

    @Test
    void testNoSuchAlgorithmExceptionHandling() throws Exception {
        // Arrange
        NoSuchAlgorithmException originalException = new NoSuchAlgorithmException("Test algorithm error");
        when(wallet.getPublicKey()).thenThrow(originalException);

        // Act & Assert
        NoSuchAlgorithmException exception = assertThrows(
                NoSuchAlgorithmException.class,
                () -> new WalletDTO(wallet)
        );

        // Verify exception contains expected message and original cause
        assertTrue(exception.getMessage().contains("Failed to get public key from wallet"));
        assertTrue(exception.getMessage().contains("Test algorithm error"));
        assertSame(originalException, exception.getCause());
    }

    @Test
    void testInvalidKeySpecExceptionHandling() throws Exception {
        // Arrange
        InvalidKeySpecException originalException = new InvalidKeySpecException("Test key spec error");
        when(wallet.getPublicKey()).thenThrow(originalException);

        // Act & Assert
        InvalidKeySpecException exception = assertThrows(
                InvalidKeySpecException.class,
                () -> new WalletDTO(wallet)
        );

        // Verify exception contains expected message and original cause
        assertTrue(exception.getMessage().contains("Failed to get public key from wallet"));
        assertTrue(exception.getMessage().contains("Test key spec error"));
        assertSame(originalException, exception.getCause());
    }
    
    @Test
    void testWalletEntryNoSuchAlgorithmExceptionHandling() throws Exception {
        // Arrange
        NoSuchAlgorithmException originalException = new NoSuchAlgorithmException("Entry algorithm error");
        // Setup the wallet entry with userId and userName
        walletEntry.userId = "testUser";
        walletEntry.userName = "Test User";
        walletEntry.wallet = wallet;
        
        when(wallet.getPublicKey()).thenThrow(originalException);

        // Act & Assert
        NoSuchAlgorithmException exception = assertThrows(
                NoSuchAlgorithmException.class,
                () -> new WalletDTO(walletEntry)
        );

        // Verify exception contains expected message and original cause
        assertTrue(exception.getMessage().contains("Failed to get public key from wallet"));
        assertTrue(exception.getMessage().contains("Entry algorithm error"));
        assertSame(originalException, exception.getCause());
    }

    @Test
    void testWalletEntryInvalidKeySpecExceptionHandling() throws Exception {
        // Arrange
        InvalidKeySpecException originalException = new InvalidKeySpecException("Entry key spec error");
        // Setup the wallet entry with userId and userName
        walletEntry.userId = "testUser";
        walletEntry.userName = "Test User";
        walletEntry.wallet = wallet;
        
        when(wallet.getPublicKey()).thenThrow(originalException);

        // Act & Assert
        InvalidKeySpecException exception = assertThrows(
                InvalidKeySpecException.class,
                () -> new WalletDTO(walletEntry)
        );

        // Verify exception contains expected message and original cause
        assertTrue(exception.getMessage().contains("Failed to get public key from wallet"));
        assertTrue(exception.getMessage().contains("Entry key spec error"));
        assertSame(originalException, exception.getCause());
    }
}