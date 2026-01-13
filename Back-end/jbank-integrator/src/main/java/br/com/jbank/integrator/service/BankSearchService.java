package br.com.jbank.integrator.service;

import br.com.jbank.integrator.client.BrasilApiClient;
import br.com.jbank.integrator.dto.BankDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for searching Brazilian banks using Brasil API.
 * 
 * @author Pamela Menezes
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BankSearchService {
    
    private final BrasilApiClient brasilApiClient;
    
    /**
     * Get all Brazilian banks from Brasil API.
     * 
     * This method uses OpenFeign to make a declarative HTTP call.
     * Much cleaner than using RestTemplate!
     * 
     * @return List of all Brazilian banks
     */
    public List<BankDTO> getAllBanks() {
        log.info("Fetching all Brazilian banks from Brasil API...");
        
        try {
            List<BankDTO> banks = brasilApiClient.getAllBanks();
            log.info("✅ Successfully fetched {} banks from Brasil API", banks.size());
            return banks;
            
        } catch (Exception e) {
            log.error("❌ Error fetching banks from Brasil API", e);
            throw new RuntimeException("Failed to fetch banks from Brasil API: " + e.getMessage(), e);
        }
    }
    
    /**
     * Search for a specific bank by code.
     * 
     * @param code Bank code (e.g., 001 for Banco do Brasil)
     * @return BankDTO or null if not found
     */
    public BankDTO findByCode(Integer code) {
        log.info("Searching for bank with code: {}", code);
        
        return getAllBanks().stream()
                .filter(bank -> bank.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }
}
