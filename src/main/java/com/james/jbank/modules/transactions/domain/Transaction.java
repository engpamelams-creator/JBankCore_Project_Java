package com.james.jbank.modules.transactions.domain;

import com.james.jbank.common.BaseEntity;
import com.james.jbank.modules.wallets.domain.Wallet;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transaction extends BaseEntity {

    @Column(nullable = false)
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "sender_wallet_id", nullable = false)
    private Wallet sender;

    @ManyToOne
    @JoinColumn(name = "receiver_wallet_id", nullable = false)
    private Wallet receiver;

    public Transaction(BigDecimal amount, Wallet sender, Wallet receiver) {
        this.amount = amount;
        this.sender = sender;
        this.receiver = receiver;
    }
}
