package br.com.jbank.core.modulos.carteiras.domain;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    /**
     * Busca uma carteira travando o registro para escrita (PESSIMISTIC_WRITE).
     * 
     * Pamela: Este método emite um 'SELECT ... FOR UPDATE' no banco de dados.
     * Isso impede que qualquer outra transação leia ou altere este registro 
     * até que a transação atual (que chamou este método) termine.
     * 
     * É a nossa principal defesa contra Race Conditions em transferências.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.id = :id")
    Optional<Wallet> findByIdWithLock(@Param("id") UUID id);
}
