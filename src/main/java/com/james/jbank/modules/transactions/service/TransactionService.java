package com.james.jbank.modules.transactions.service;

import com.james.jbank.modules.transactions.domain.Transaction;
import com.james.jbank.modules.transactions.dto.CreateTransferDTO;
import com.james.jbank.modules.transactions.repository.TransactionRepository;
import com.james.jbank.modules.wallets.domain.Wallet;
import com.james.jbank.modules.wallets.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    @Transactional
    public Transaction performTransfer(CreateTransferDTO dto) {
        // 1. Retrieve and Lock Wallets (Pessimistic Write)
        // We lock the payer to ensure they can't double-spend concurrently.
        Wallet payerWallet = walletRepository.findByIdForUpdate(dto.payerId())
                .orElseThrow(() -> new IllegalArgumentException("Payer wallet not found"));

        Wallet payeeWallet = walletRepository.findByIdForUpdate(dto.payeeId())
                .orElseThrow(() -> new IllegalArgumentException("Payee wallet not found"));

        // 2. Validate Transaction Balance
        validateBalance(payerWallet, dto.value());

        // 3. Perform atomic operations
        payerWallet.debit(dto.value());
        payeeWallet.credit(dto.value());

        // 4. Record the History
        Transaction transaction = new Transaction(dto.value(), payerWallet, payeeWallet);
        
        walletRepository.save(payerWallet);
        walletRepository.save(payeeWallet);
        
        return transactionRepository.save(transaction);
    }

    private void validateBalance(Wallet wallet, BigDecimal amount) {
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }
    }
}
