package com.example.blockchain.wallet.DTO;

import java.util.Base64;

import com.example.blockchain.wallet.core.Wallet;
import com.example.blockchain.wallet.core.WalletList;

public class WalletDTO {
    private String userId;
    private String userName;
    private String publicKeyBase64;

    public WalletDTO(Wallet wallet) {
        this.userId = wallet.getUserId();
        this.userName = wallet.getUserName();
        this.publicKeyBase64 = Base64.getEncoder().encodeToString(wallet.getPublicKey().getEncoded());
    }

    public WalletDTO(WalletList.WalletEntry entry) {
        this.userId = entry.userId;
        this.userName = entry.userName;
        this.publicKeyBase64 = Base64.getEncoder().encodeToString(entry.wallet.getPublicKey().getEncoded());
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getPublicKeyBase64() {
        return publicKeyBase64;
    }
}
