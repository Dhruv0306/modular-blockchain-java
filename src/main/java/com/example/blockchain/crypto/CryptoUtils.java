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

public class CryptoUtils {
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        try {
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(2048);
            return gen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new NoSuchAlgorithmException("Failed to generate RSA key pair: RSA algorithm not available", e);
        }
    }

    public static String signData(String data, PrivateKey privateKey) throws Exception {
        try {
            Signature signer = Signature.getInstance("SHA256withRSA");
            signer.initSign(privateKey);
            signer.update(data.getBytes("UTF-8"));
            byte[] signature = signer.sign();
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

    public static boolean verifySignature(String data, String signature, PublicKey publicKey) throws Exception {
        try {
            Signature verifier = Signature.getInstance("SHA256withRSA");
            verifier.initVerify(publicKey);
            verifier.update(data.getBytes("UTF-8"));
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
