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
public class TransferService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    /**
     * Realiza a transferência financeira com garantias ACID e Lock Pessimista.
     * 
     * @param request DTO com dados da solicitação (já validado pelo Controller via @Valid)
     * @return TransferResponseDTO com os dados da transação efetuada
     */
    @Transactional
    public TransferResponseDTO performTransfer(TransferRequestDTO request) {
        
        // 1. Validação de Negócio: Auto-transferência
        if (request.senderId().equals(request.receiverId())) {
            throw new IllegalArgumentException("Não é possível transferir para a mesma conta.");
        }

        // Pamela: Buscamos e travamos as carteiras (Pessimistic Lock).
        // A ordem aqui não importa tanto para Deadlock se sempre ordenarmos por ID,
        // mas para simplicidade vamos buscar sender depois receiver.
        // Em produção de alta escala, ordenaríamos os IDs (sender, receiver) para evitar Deadlock.
        
        Wallet senderWallet = walletRepository.findByIdWithLock(request.senderId())
                .orElseThrow(() -> new IllegalArgumentException("Remetente não encontrado."));

        Wallet receiverWallet = walletRepository.findByIdWithLock(request.receiverId())
                .orElseThrow(() -> new IllegalArgumentException("Destinatário não encontrado."));

        // 2. Validação de Saldo/Domínio
        // O método 'debit' da entidade Wallet já lança exceção se saldo for insuficiente.
        // Aqui apenas chamamos. O Service orquestra, a Entidade protege suas regras.
        try {
            senderWallet.debit(request.amount());
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException("Saldo insuficiente.");
        }
        
        receiverWallet.credit(request.amount());
        
        // 3. Persistência
        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        // 4. Registrar Transação
        Transaction transaction = Transaction.builder()
                .senderWalletId(senderWallet.getId())
                .receiverWalletId(receiverWallet.getId())
                .amount(request.amount())
                .status(Transaction.TransactionStatus.COMPLETED)
                .build();
        
        transaction = transactionRepository.save(transaction);

        // 5. Mapeamento para DTO (Output)
        return new TransferResponseDTO(
            transaction.getId(),
            transaction.getAmount(),
            transaction.getCreatedAt(),
            transaction.getStatus().name()
        );
    }
}
