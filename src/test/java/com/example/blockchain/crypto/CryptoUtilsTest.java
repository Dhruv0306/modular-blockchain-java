package com.example.blockchain.crypto;

import static org.junit.jupiter.api.Assertions.*;

import java.security.KeyPair;

import org.junit.jupiter.api.Test;

import com.example.blockchain.crypto.CryptoUtils;

class CryptoUtilsTest {

    @Test
    void testGenerateKeyPairNotNull() throws Exception {
        KeyPair keyPair = CryptoUtils.generateKeyPair();
        assertNotNull(keyPair);
        assertNotNull(keyPair.getPrivate());
        assertNotNull(keyPair.getPublic());
    }

    @Test
    void testSignAndVerifyValidSignature() throws Exception {
        KeyPair keyPair = CryptoUtils.generateKeyPair();
        String data = "secure-data";
        String signature = CryptoUtils.signData(data, keyPair.getPrivate());

        assertTrue(CryptoUtils.verifySignature(data, signature, keyPair.getPublic()));
    }

    @Test
    void testSignatureFailsOnTamperedData() throws Exception {
        KeyPair keyPair = CryptoUtils.generateKeyPair();
        String original = "secure-data";
        String signature = CryptoUtils.signData(original, keyPair.getPrivate());

        assertFalse(CryptoUtils.verifySignature("tampered", signature, keyPair.getPublic()));
    }

    @Test
    void testSignatureFailsOnWrongKey() throws Exception {
        KeyPair senderKey = CryptoUtils.generateKeyPair();
        KeyPair attackerKey = CryptoUtils.generateKeyPair();
        String data = "secure-data";
        String signature = CryptoUtils.signData(data, senderKey.getPrivate());

        assertFalse(CryptoUtils.verifySignature(data, signature, attackerKey.getPublic()));
    }
}
