package com.james.jbank.modules.wallets.service;

import com.james.jbank.modules.users.domain.User;
import com.james.jbank.modules.wallets.domain.Wallet;
import com.james.jbank.modules.wallets.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    public Wallet createWallet(User user) {
        Wallet wallet = new Wallet(user);
        return walletRepository.save(wallet);
    }
    
    // Additional domain logic for wallets can go here
}
