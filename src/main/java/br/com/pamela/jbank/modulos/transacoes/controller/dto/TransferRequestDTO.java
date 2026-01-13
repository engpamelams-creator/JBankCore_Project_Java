package br.com.pamela.jbank.modulos.transacoes.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Representa os dados necessários para solicitar uma transferência.
 * Implementado como Record para imutabilidade e concisão (Java 16+).
 */
public record TransferRequestDTO(
    
    @NotNull(message = "O ID do remetente é obrigatório.")
    UUID senderId,
    
    @NotNull(message = "O ID do destinatário é obrigatório.")
    UUID receiverId,
    
    @NotNull(message = "O valor da transferência é obrigatório.")
    @Positive(message = "O valor da transferência deve ser maior que zero.")
    BigDecimal amount
) {}
