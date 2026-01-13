package br.com.jbank.integrator.client;

import br.com.jbank.integrator.dto.AccountBalanceDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign Client for Open Finance - Simulated Integration
 * 
 * PRODUCTION NOTES:
 * ==================
 * In a real Open Finance integration, you would need:
 * 
 * 1. OAuth2 Configuration:
 *    - Client ID from financial institution (e.g., Banco do Brasil)
 *    - Client Secret (stored securely in environment variables)
 *    - Token URL for authentication
 * 
 * 2. User Consent:
 *    - Redirect user to bank's authorization page
 *    - Receive consent ID after user approves
 *    - Use consent ID to fetch account data
 * 
 * 3. Certificates:
 *    - Digital certificates for secure communication
 *    - Registered with Banco Central do Brasil
 * 
 * 4. Real URLs:
 *    - Banco do Brasil: https://openbanking.api.bb.com.br
 *    - Belvo: https://api.belvo.com
 *    - Pluggy: https://api.pluggy.ai
 * 
 * For now, this is a MOCK implementation to demonstrate the architecture.
 * 
 * @author Pamela Menezes
 */
@FeignClient(
    name = "open-finance",
    url = "${open-finance.base-url:http://localhost:9999}"
)
public interface OpenFinanceClient {
    
    /**
     * Get account balance from Open Finance.
     * 
     * PRODUCTION: This would require:
     * - Valid OAuth2 token in Authorization header
     * - Consent ID from user authorization
     * - Digital certificates
     * 
     * CURRENT: Returns mocked data for demonstration
     * 
     * @param consentId Consent ID from user authorization
     * @return Account balance information
     */
    @GetMapping("/accounts/{consentId}/balance")
    AccountBalanceDTO getAccountBalance(@PathVariable String consentId);
}
