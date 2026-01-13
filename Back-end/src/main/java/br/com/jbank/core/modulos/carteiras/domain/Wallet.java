package br.com.jbank.core.modulos.carteiras.domain;

import br.com.jbank.core.modulos.usuarios.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Representa a carteira (conta corrente) de um usuário.
 * 
 * Pamela: A separação entre User e Wallet é intencional. 
 * Um usuário pode ter múltiplas carteiras no futuro (ex: Corrente, Investimento),
 * ou joint-accounts (conta conjunta).
 */
@Entity
@Table(name = "tb_wallets")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Pamela: BigDecimal para garantir precisão monetária absoluta.
    // Inicializamos com ZERO para evitar NullPointerException em cálculos.
    @Column(nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    public void debit(BigDecimal amount) {
        // Encapsulamento: A lógica de débito pertence à conta (Wallet), não ao serviço.
        // A própria conta protege seus invariantes (saldo não pode ser negativo no nosso domínio).
        if (this.balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Saldo insuficiente.");
        }
        this.balance = this.balance.subtract(amount);
    }

    public void credit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }
}
