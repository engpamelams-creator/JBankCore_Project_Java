package br.com.jbank.integrator.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing an account balance from Open Finance.
 * 
 * PRODUCTION: This would come from real Open Finance APIs like:
 * - Banco do Brasil Open Banking
 * - Belvo API
 * - Pluggy
 * 
 * For now, this is a simulated response.
 * 
 * @author Pamela Menezes
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountBalanceDTO {
    
    /**
     * Account balance
     */
    @JsonProperty("balance")
    private BigDecimal balance;
    
    /**
     * Currency (BRL for Brazilian Real)
     */
    @JsonProperty("currency")
    private String currency;
    
    /**
     * Consent ID from Open Finance authorization
     */
    @JsonProperty("consentId")
    private String consentId;
    
    /**
     * Account holder name
     */
    @JsonProperty("accountHolder")
    private String accountHolder;
}
