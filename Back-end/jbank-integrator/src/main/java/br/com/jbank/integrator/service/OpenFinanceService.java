package br.com.jbank.integrator.service;

import br.com.jbank.integrator.dto.AccountBalanceDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Service for Open Finance integrations.
 * 
 * CURRENT: Returns mocked data for demonstration
 * PRODUCTION: Would integrate with real Open Finance APIs
 * 
 * @author Pamela Menezes
 */
@Slf4j
@Service
public class OpenFinanceService {
    
    /**
     * Get account balance from Open Finance.
     * 
     * PRODUCTION IMPLEMENTATION WOULD:
     * 1. Validate consent ID
     * 2. Check if consent is still valid (not expired)
     * 3. Make authenticated request to Open Finance API
     * 4. Handle OAuth2 token refresh if needed
     * 5. Return real account balance
     * 
     * CURRENT: Returns mocked data
     * 
     * @param consentId Consent ID from user authorization
     * @return Mocked account balance
     */
    public AccountBalanceDTO getAccountBalance(String consentId) {
        log.info("🏦 Fetching account balance for consent ID: {}", consentId);
        
        // MOCK: In production, this would call openFinanceClient.getAccountBalance(consentId)
        // and handle OAuth2 authentication
        
        log.warn("⚠️ MOCK: Returning simulated balance. In production, this would call real Open Finance API");
        
        return new AccountBalanceDTO(
            new BigDecimal("15750.50"),  // Mock balance
            "BRL",                        // Brazilian Real
            consentId,
            "Pamela Menezes"             // Mock account holder
        );
    }
    
    /**
     * Check if a consent is still valid.
     * 
     * PRODUCTION: Would check consent expiration date
     * CURRENT: Always returns true (mock)
     * 
     * @param consentId Consent ID
     * @return true if valid, false otherwise
     */
    public boolean isConsentValid(String consentId) {
        log.info("Checking if consent {} is valid", consentId);
        
        // MOCK: In production, check expiration date
        return true;
    }
}
