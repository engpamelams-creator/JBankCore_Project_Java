package br.com.jbank.integrator.controller;

import br.com.jbank.integrator.dto.AccountBalanceDTO;
import br.com.jbank.integrator.dto.BankDTO;
import br.com.jbank.integrator.service.BankSearchService;
import br.com.jbank.integrator.service.OpenFinanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for External Integrations.
 * 
 * This controller acts as a gateway for:
 * - Brasil API (Real integration)
 * - Open Finance (Simulated integration)
 * 
 * @author Pamela Menezes
 */
@Slf4j
@RestController
@RequestMapping("/integrations")
@RequiredArgsConstructor
public class IntegratorController {
    
    private final BankSearchService bankSearchService;
    private final OpenFinanceService openFinanceService;
    
    /**
     * Get all Brazilian banks from Brasil API.
     * 
     * REAL INTEGRATION: Fetches live data from https://brasilapi.com.br
     * 
     * Example response:
     * [
     *   {
     *     "ispb": "00000000",
     *     "name": "BCO DO BRASIL S.A.",
     *     "code": 1,
     *     "fullName": "Banco do Brasil S.A."
     *   },
     *   ...
     * ]
     * 
     * @return List of all Brazilian banks
     */
    @GetMapping("/banks")
    public ResponseEntity<List<BankDTO>> getAllBanks() {
        log.info("📊 GET /integrations/banks - Fetching all Brazilian banks");
        
        List<BankDTO> banks = bankSearchService.getAllBanks();
        
        log.info("✅ Returning {} banks", banks.size());
        return ResponseEntity.ok(banks);
    }
    
    /**
     * Get a specific bank by code.
     * 
     * @param code Bank code (e.g., 001 for Banco do Brasil)
     * @return Bank information or 404 if not found
     */
    @GetMapping("/banks/{code}")
    public ResponseEntity<BankDTO> getBankByCode(@PathVariable Integer code) {
        log.info("🔍 GET /integrations/banks/{} - Searching for bank", code);
        
        BankDTO bank = bankSearchService.findByCode(code);
        
        if (bank == null) {
            log.warn("❌ Bank with code {} not found", code);
            return ResponseEntity.notFound().build();
        }
        
        log.info("✅ Found bank: {}", bank.getName());
        return ResponseEntity.ok(bank);
    }
    
    /**
     * Get account balance from Open Finance.
     * 
     * SIMULATED: Returns mocked data for demonstration
     * PRODUCTION: Would require OAuth2 authentication and valid consent
     * 
     * @param consentId Consent ID from user authorization
     * @return Account balance information
     */
    @GetMapping("/open-finance/balance/{consentId}")
    public ResponseEntity<AccountBalanceDTO> getAccountBalance(@PathVariable String consentId) {
        log.info("💰 GET /integrations/open-finance/balance/{} - Fetching account balance", consentId);
        
        if (!openFinanceService.isConsentValid(consentId)) {
            log.warn("❌ Invalid or expired consent: {}", consentId);
            return ResponseEntity.status(403).build();
        }
        
        AccountBalanceDTO balance = openFinanceService.getAccountBalance(consentId);
        
        log.info("✅ Returning balance: {} {}", balance.getBalance(), balance.getCurrency());
        return ResponseEntity.ok(balance);
    }
    
    /**
     * Health check endpoint.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("{\"status\":\"UP\",\"service\":\"jbank-integrator\"}");
    }
}
