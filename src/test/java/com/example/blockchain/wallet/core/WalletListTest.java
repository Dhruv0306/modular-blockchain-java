package com.example.blockchain.wallet.core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WalletListTest {

    private WalletList walletList;
    
    @BeforeEach
    void setUp() {
        walletList = new WalletList();
    }
    
    @Test
    void testContainsUser_WhenUserExists_ReturnsTrue() {
        // Arrange
        String userId = "user123";
        String userName = "Test User";
        Wallet wallet = new Wallet();
        walletList.addWallet(userId, userName, wallet);
        
        // Act
        boolean result = walletList.containsUser(userId);
        
        // Assert
        assertTrue(result);
    }
    
    @Test
    void testContainsUser_WhenUserDoesNotExist_ReturnsFalse() {
        // Arrange
        String nonExistentUserId = "nonexistent";
        
        // Act
        boolean result = walletList.containsUser(nonExistentUserId);
        
        // Assert
        assertFalse(result);
    }
    
    @Test
    void testContainsUser_AfterRemovingUser_ReturnsFalse() {
        // Arrange
        String userId = "user456";
        String userName = "Another User";
        Wallet wallet = new Wallet();
        walletList.addWallet(userId, userName, wallet);
        
        // Act
        walletList.removeWallet(userId);
        boolean result = walletList.containsUser(userId);
        
        // Assert
        assertFalse(result);
    }
    
    @Test
    void testGetWalletByUserID_WhenUserExists_ReturnsWalletEntry() {
        // Arrange
        String userId = "user789";
        String userName = "Test User";
        Wallet wallet = new Wallet();
        walletList.addWallet(userId, userName, wallet);
        
        // Act
        WalletList.WalletEntry result = walletList.getWalletByUserID(userId);
        
        // Assert
        assertNotNull(result);
        assertEquals(userId, result.userId);
        assertEquals(userName, result.userName);
        assertSame(wallet, result.wallet);
    }
    
    @Test
    void testGetWalletByUserID_WhenUserDoesNotExist_ReturnsNull() {
        // Arrange
        String nonExistentUserId = "nonexistent";
        
        // Act
        WalletList.WalletEntry result = walletList.getWalletByUserID(nonExistentUserId);
        
        // Assert
        assertNull(result);
    }
}