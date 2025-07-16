package com.example.blockchain.crypto;

import static org.junit.jupiter.api.Assertions.*;

import java.security.KeyPair;

import org.junit.jupiter.api.Test;

import com.example.blockchain.crypto.CryptoUtils;

/**
 * Test class for CryptoUtils functionality
 * Tests key pair generation, signing and signature verification
 */
class CryptoUtilsTest {

    /**
     * Test that key pair generation produces non-null keys
     * Verifies both public and private keys are generated
     */
    @Test
    void testGenerateKeyPairNotNull() throws Exception {
        KeyPair keyPair = CryptoUtils.generateKeyPair();
        assertNotNull(keyPair);
        assertNotNull(keyPair.getPrivate());
        assertNotNull(keyPair.getPublic());
    }

    /**
     * Test that a signature can be created and verified
     * Uses the same key pair for signing and verification
     */
    @Test
    void testSignAndVerifyValidSignature() throws Exception {
        KeyPair keyPair = CryptoUtils.generateKeyPair();
        String data = "secure-data";
        String signature = CryptoUtils.signData(data, keyPair.getPrivate());

        // Verify signature using matching public key
        assertTrue(CryptoUtils.verifySignature(data, signature, keyPair.getPublic()));
    }

    /**
     * Test that signature verification fails when data is tampered
     * Verifies that changing the original data invalidates the signature
     */
    @Test
    void testSignatureFailsOnTamperedData() throws Exception {
        KeyPair keyPair = CryptoUtils.generateKeyPair();
        String original = "secure-data";
        String signature = CryptoUtils.signData(original, keyPair.getPrivate());

        // Attempt verification with modified data
        assertFalse(CryptoUtils.verifySignature("tampered", signature, keyPair.getPublic()));
    }

    /**
     * Test that signature verification fails with wrong public key
     * Simulates an attacker trying to verify with different key pair
     */
    @Test
    void testSignatureFailsOnWrongKey() throws Exception {
        KeyPair senderKey = CryptoUtils.generateKeyPair();
        KeyPair attackerKey = CryptoUtils.generateKeyPair();
        String data = "secure-data";
        String signature = CryptoUtils.signData(data, senderKey.getPrivate());

        // Attempt verification with different public key
        assertFalse(CryptoUtils.verifySignature(data, signature, attackerKey.getPublic()));
    }
}
