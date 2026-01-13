package br.com.jbank.core.modulos.transacoes.controller.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

/**
 * Representa os dados necessários para solicitar uma transferência.
 * Fort Knox Upgrade: Exige PIN transacional.
 */
public record TransferRequestDTO(
    
    @NotNull(message = "O ID do remetente é obrigatório.")
    UUID senderId,
    
    @NotNull(message = "O ID do destinatário é obrigatório.")
    UUID receiverId,
    
    @NotNull(message = "O valor da transferência é obrigatório.")
    @Positive(message = "O valor da transferência deve ser maior que zero.")
    BigDecimal amount,

    @NotNull(message = "Senha Transacional é obrigatória")
    @Pattern(regexp = "\\d{4}", message = "PIN deve ter 4 dígitos numéricos")
    String pin
) {}
