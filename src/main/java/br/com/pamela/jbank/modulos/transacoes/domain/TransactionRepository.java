package br.com.pamela.jbank.modulos.transacoes.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    // Pamela: O JpaRepository já nos fornece os métodos padrão (save, findById).
    // Métodos específicos de negócio seriam adicionados aqui.
}
