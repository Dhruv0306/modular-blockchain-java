package com.example.blockchain;

import static org.junit.jupiter.api.Assertions.*;

import java.security.KeyPair;

import org.junit.jupiter.api.Test;

import com.example.blockchain.crypto.CryptoUtils;
import com.example.blockchain.transactions.SignedFinancialTransaction;

class SignedFinancialTransactionTest {

    @Test
    void testValidSignedTransaction() throws Exception {
        KeyPair pair = CryptoUtils.generateKeyPair();
        String summary = "Alice -> Bob : $100.0";
        String signature = CryptoUtils.signData(summary, pair.getPrivate());

        SignedFinancialTransaction tx = new SignedFinancialTransaction(
                "Alice", "Bob", 100.0, pair.getPublic(), signature);

        assertTrue(tx.isValid());
    }

    @Test
    void testInvalidSignatureFailsValidation() throws Exception {
        KeyPair pair = CryptoUtils.generateKeyPair();
        String signature = CryptoUtils.signData("invalid data", pair.getPrivate());

        SignedFinancialTransaction tx = new SignedFinancialTransaction(
                "Alice", "Bob", 100.0, pair.getPublic(), signature);

        assertFalse(tx.isValid());
    }

    @Test
    void testInvalidAmountFailsValidation() throws Exception {
        KeyPair pair = CryptoUtils.generateKeyPair();
        String summary = "Alice -> Bob : $0.0";
        String signature = CryptoUtils.signData(summary, pair.getPrivate());

        SignedFinancialTransaction tx = new SignedFinancialTransaction(
                "Alice", "Bob", 0.0, pair.getPublic(), signature);

        assertFalse(tx.isValid());
    }

    @Test
    void testNullReceiverFailsValidation() throws Exception {
        KeyPair pair = CryptoUtils.generateKeyPair();
        String summary = "Alice -> null : $100.0";
        String signature = CryptoUtils.signData(summary, pair.getPrivate());

        SignedFinancialTransaction tx = new SignedFinancialTransaction(
                "Alice", null, 100.0, pair.getPublic(), signature);

        assertFalse(tx.isValid());
    }

    @Test
    void testToStringMatchesSummary() throws Exception {
        KeyPair pair = CryptoUtils.generateKeyPair();
        String summary = "Alice -> Bob : $50.0";
        String signature = CryptoUtils.signData(summary, pair.getPrivate());

        SignedFinancialTransaction tx = new SignedFinancialTransaction(
                "Alice", "Bob", 50.0, pair.getPublic(), signature);

        assertEquals(summary, tx.toString());
    }
}
