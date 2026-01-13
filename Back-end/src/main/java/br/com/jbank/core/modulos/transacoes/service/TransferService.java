package br.com.jbank.core.modulos.transacoes.service;

import br.com.jbank.core.modulos.carteiras.domain.Wallet;
import br.com.jbank.core.modulos.carteiras.domain.WalletRepository;
import br.com.jbank.core.modulos.transacoes.controller.dto.TransferRequestDTO;
import br.com.jbank.core.modulos.transacoes.controller.dto.TransferResponseDTO;
import br.com.jbank.core.modulos.transacoes.domain.Transaction;
import br.com.jbank.core.modulos.transacoes.domain.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@lombok.extern.slf4j.Slf4j
public class TransferService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Autowired
    public TransferService(
            TransactionRepository transactionRepository,
            WalletRepository walletRepository,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Executes a financial transfer with ACID guarantees and Pessimistic Locking.
     * <p>
     * Implements Resource Ordering (Deadlock Prevention) by locking wallets based on ID order.
     * </p>
     * 
     * @param request DTO containing transfer details (pre-validated by Controller)
     * @return TransferResponseDTO with the completed transaction data
     * @throws IllegalArgumentException if business rules are violated (e.g., self-transfer, insufficient balance)
     */
    @Transactional
    public TransferResponseDTO performTransfer(TransferRequestDTO request) {
        log.info("Initiating transfer of {} from [{}] to [{}]", request.amount(), request.senderId(), request.receiverId());

        // 0. Fort Knox: PIN Validation (Verify before locking to save resources)
        // Note: For full security, we might want to check this AFTER locking or verify ownership earlier
        // But checking hash is cheap compared to DB lock. However, we need the user to check hash.
        // Let's do it after fetching wallets to ensure we have the correct user data securely.
        
        // 1. Business Validation: Self-transfer check
        if (request.senderId().equals(request.receiverId())) {
            log.warn("Attempted self-transfer for ID: {}", request.senderId());
            throw new IllegalArgumentException("Cannot transfer to the same account.");
        }

        // 2. Deadlock Prevention: Resource Ordering
        // Always lock the wallet with the smaller ID first
        var firstLockId = request.senderId().compareTo(request.receiverId()) < 0 ? request.senderId() : request.receiverId();
        var secondLockId = request.senderId().compareTo(request.receiverId()) < 0 ? request.receiverId() : request.senderId();

        var wallet1 = walletRepository.findByIdWithLock(firstLockId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found with ID: " + firstLockId));
        
        var wallet2 = walletRepository.findByIdWithLock(secondLockId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found with ID: " + secondLockId));

        // Resolve which is sender and receiver
        var senderWallet = wallet1.getId().equals(request.senderId()) ? wallet1 : wallet2;
        var receiverWallet = wallet1.getId().equals(request.receiverId()) ? wallet1 : wallet2;

        // Fort Knox: Verify PIN (Delegated to Domain Entity)
        senderWallet.getUser().validateTransactionPin(request.pin(), passwordEncoder);

        // 3. Balance Validation & Operation
        try {
            senderWallet.debit(request.amount());
        } catch (IllegalStateException e) {
            log.error("Insufficient funds for wallet [{}]. Current balance: {}", senderWallet.getId(), senderWallet.getBalance());
            throw new IllegalArgumentException("Insufficient balance.");
        }
        
        receiverWallet.credit(request.amount());
        
        // 4. Persistence
        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        // 5. Audit/history Logging
        var transaction = Transaction.builder()
                .senderWalletId(senderWallet.getId())
                .receiverWalletId(receiverWallet.getId())
                .amount(request.amount())
                .status(Transaction.TransactionStatus.COMPLETED)
                .build();
        
        transaction = transactionRepository.save(transaction);
        log.info("Transfer completed successfully. Transaction ID: {}", transaction.getId());

        // 6. Return DTO
        return new TransferResponseDTO(
            transaction.getId(),
            transaction.getAmount(),
            transaction.getCreatedAt(),
            transaction.getStatus().name()
        );
    }
}
