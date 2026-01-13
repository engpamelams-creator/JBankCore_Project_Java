package br.com.jbank.integrator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a Brazilian Bank from Brasil API.
 * 
 * Source: https://brasilapi.com.br/api/banks/v1
 * 
 * @author Pamela Menezes
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankDTO {
    
    /**
     * ISPB - Identificador do Sistema de Pagamentos Brasileiro
     */
    @JsonProperty("ispb")
    private String ispb;
    
    /**
     * Short name of the bank
     */
    @JsonProperty("name")
    private String name;
    
    /**
     * Bank code (e.g., 001 for Banco do Brasil)
     */
    @JsonProperty("code")
    private Integer code;
    
    /**
     * Full name of the bank
     */
    @JsonProperty("fullName")
    private String fullName;
}
