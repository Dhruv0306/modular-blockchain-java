package com.example.blockchain.crypto;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.InvalidKeyException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * Utility class providing cryptographic operations for digital signatures.
 * Implements RSA key pair generation, data signing and signature verification.
 */
public class CryptoUtils {

    /**
     * Generates a new RSA key pair for signing and verification.
     * 
     * @return KeyPair containing public and private RSA keys
     * @throws NoSuchAlgorithmException if RSA algorithm is not available
     */
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        try {
            // Initialize RSA key pair generator with 2048 bit key size
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(2048);
            return gen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new NoSuchAlgorithmException("Failed to generate RSA key pair: RSA algorithm not available", e);
        }
    }

    /**
     * Signs the provided data using RSA private key.
     *
     * @param data data to be signed
     * @param privateKey RSA private key used for signing
     * @return Base64 encoded signature string
     * @throws Exception if signing process fails
     */
    public static String signData(String data, PrivateKey privateKey) throws Exception {
        try {
            // Create SHA256withRSA signature instance
            Signature signer = Signature.getInstance("SHA256withRSA");
            signer.initSign(privateKey);
            
            // Update with data and generate signature
            signer.update(data.getBytes("UTF-8"));
            byte[] signature = signer.sign();
            
            // Encode signature as Base64 string
            return Base64.getEncoder().encodeToString(signature);
        } catch (NoSuchAlgorithmException e) {
            throw new NoSuchAlgorithmException("Failed to sign data: SHA256withRSA algorithm not available", e);
        } catch (InvalidKeyException e) {
            throw new InvalidKeyException("Failed to sign data: Invalid private key provided", e);
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedEncodingException("Failed to sign data: UTF-8 encoding not supported");
        } catch (SignatureException e) {
            throw new SignatureException("Failed to sign data: Error during signature operation", e);
        }
    }

    /**
     * Verifies if a signature is valid for the given data using RSA public key.
     *
     * @param data original data that was signed
     * @param signature Base64 encoded signature to verify
     * @param publicKey RSA public key used for verification
     * @return true if signature is valid, false otherwise
     * @throws Exception if verification process fails
     */
    public static boolean verifySignature(String data, String signature, PublicKey publicKey) throws Exception {
        try {
            // Create SHA256withRSA signature instance for verification
            Signature verifier = Signature.getInstance("SHA256withRSA");
            verifier.initVerify(publicKey);
            
            // Update with original data
            verifier.update(data.getBytes("UTF-8"));
            
            // Decode Base64 signature and verify
            byte[] sigBytes = Base64.getDecoder().decode(signature);
            return verifier.verify(sigBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new NoSuchAlgorithmException("Failed to verify signature: SHA256withRSA algorithm not available", e);
        } catch (InvalidKeyException e) {
            throw new InvalidKeyException("Failed to verify signature: Invalid public key provided", e);
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedEncodingException("Failed to verify signature: UTF-8 encoding not supported");
        } catch (SignatureException e) {
            throw new SignatureException("Failed to verify signature: Error during verification operation", e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Failed to verify signature: Invalid Base64 encoded signature", e);
        }
    }
}
