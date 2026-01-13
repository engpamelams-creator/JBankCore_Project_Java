package br.com.pamela.jbank.modulos.transacoes.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa uma transação financeira no sistema.
 * 
 * Pamela: Mantenho o nome da classe em Inglês (Transaction) seguindo o padrão de mercado,
 * mas as explicações de design estão todas em Português para facilitar o onboarding do time.
 */
@Entity
@Table(name = "tb_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Pamela: Utilizo BigDecimal aqui obrigatoriamente.
    // O tipo 'double' ou 'float' do Java segue o padrão IEEE 754 de ponto flutuante,
    // o que pode causar erros de arredondamento em operações financeiras sensíveis.
    // Com BigDecimal, garantimos a precisão decimal exata necessária para um banco.
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private UUID senderWalletId;

    @Column(nullable = false)
    private UUID receiverWalletId;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // Pamela: Enum para status da transação.
    // Optei por gravar como String no banco para facilitar a leitura em queries manuais de debug/suporte,
    // embora o Ordinal fosse levemente mais performático.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    public enum TransactionStatus {
        PENDING, COMPLETED, FAILED
    }
}
