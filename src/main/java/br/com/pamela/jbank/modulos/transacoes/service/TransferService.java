package br.com.pamela.jbank.modulos.transacoes.service;

import br.com.pamela.jbank.modulos.carteiras.domain.Wallet;
import br.com.pamela.jbank.modulos.carteiras.domain.WalletRepository;
import br.com.pamela.jbank.modulos.transacoes.controller.dto.TransferRequestDTO;
import br.com.pamela.jbank.modulos.transacoes.controller.dto.TransferResponseDTO;
import br.com.pamela.jbank.modulos.transacoes.domain.Transaction;
import br.com.pamela.jbank.modulos.transacoes.domain.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class TransferService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

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
