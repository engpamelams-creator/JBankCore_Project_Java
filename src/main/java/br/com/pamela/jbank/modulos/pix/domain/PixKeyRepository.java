package br.com.pamela.jbank.modulos.pix.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface PixKeyRepository extends JpaRepository<PixKey, UUID> {
    
    boolean existsByKey(String key);
    
    List<PixKey> findAllByUserId(UUID userId);
    
    // Para validações futuras: contar quantas chaves um usuário tem
    int countByUserId(UUID userId);
}
