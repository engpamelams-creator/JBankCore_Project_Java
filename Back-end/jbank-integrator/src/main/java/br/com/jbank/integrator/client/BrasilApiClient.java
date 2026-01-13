package br.com.jbank.integrator.client;

import br.com.jbank.integrator.dto.BankDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Feign Client for Brasil API - Real Integration
 * 
 * Brasil API provides free access to Brazilian public data.
 * Documentation: https://brasilapi.com.br/docs
 * 
 * This client fetches the complete list of Brazilian banks.
 * 
 * @author Pamela Menezes
 */
@FeignClient(
    name = "brasil-api",
    url = "https://brasilapi.com.br/api"
)
public interface BrasilApiClient {
    
    /**
     * Get all Brazilian banks.
     * 
     * Returns a list of all banks registered in Brazil, including:
     * - Banco do Brasil
     * - Caixa Econômica Federal
     * - Nubank
     * - Itaú
     * - Bradesco
     * - And many more...
     * 
     * @return List of BankDTO
     */
    @GetMapping("/banks/v1")
    List<BankDTO> getAllBanks();
}
