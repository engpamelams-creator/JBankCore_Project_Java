package com.james.jbank.domain.transaction;

import com.james.jbank.domain.wallet.Wallet;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "sender_wallet_id")
    private Wallet sender;

    @ManyToOne
    @JoinColumn(name = "receiver_wallet_id")
    private Wallet receiver;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
